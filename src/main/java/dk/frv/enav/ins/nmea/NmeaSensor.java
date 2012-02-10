/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.frv.enav.ins.nmea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.ais.binary.SixbitException;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage18;
import dk.frv.ais.message.AisMessageException;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.proprietary.GatehouseFactory;
import dk.frv.ais.proprietary.IProprietarySourceTag;
import dk.frv.ais.reader.ISendResultListener;
import dk.frv.ais.reader.SendException;
import dk.frv.ais.reader.SendRequest;
import dk.frv.ais.reader.SendThread;
import dk.frv.ais.reader.SendThreadPool;
import dk.frv.ais.sentence.Abk;
import dk.frv.ais.sentence.Sentence;
import dk.frv.ais.sentence.SentenceException;
import dk.frv.ais.sentence.Vdm;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gps.GnssTimeMessage;
import dk.frv.enav.ins.gps.IGnssTimeListener;

/**
 * Abstract class for reading and handling NMEA messages
 */
public abstract class NmeaSensor extends MapHandlerChild implements Runnable {

	private static final Logger LOG = Logger.getLogger(NmeaSensor.class);

	public enum Status {
		CONNECTED, DISCONNECTED
	};

	private boolean replay = false;
	
	private Date replayStartDate = null;
	private Date dataStart = null;
	private Date dataEnd = null;
	private Date replayStart = null;
	private Date replayEnd = null;
	private Date replayTime = new Date(0);
	
	protected SendThreadPool sendThreadPool = new SendThreadPool();
	private int replaySpeedup = 1;
	protected Set<SensorType> sensorTypes = new HashSet<SensorType>();
	private boolean simulateGps = false;
	private long simulatedOwnShip;
	private Set<IGpsListener> gpsListeners = new HashSet<IGpsListener>();
	private Set<IVesselAisListener> vesselAisListeners = new HashSet<IVesselAisListener>();
	private Set<IGnssTimeListener> gnssTimeListeners = new HashSet<IGnssTimeListener>(); 
	private Vdm vdm = new Vdm();
	
	
	public NmeaSensor() {
		
	}
	
	/**
	 * Main method to read NMEA messages from stream
	 * 
	 * @param stream
	 * @throws IOException
	 */
	protected void readLoop(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String msg;
		
		while ((msg = reader.readLine()) != null) {
			if (replay) {
				handleReplay(msg);
			}
			
			handleSentence(msg);
		}		
		
	}
	
	/**
	 * Method to send addressed or broadcast AIS messages (ABM or BBM). 
	 * @param sendRequest
	 * @param resultListener A class to handle the result when it is ready. 
	 */
	public abstract void send(SendRequest sendRequest,  ISendResultListener resultListener) throws SendException;
	
	/**
	 * The method to do the actual sending
	 * @param sendRequest
	 * @param resultListener
	 * @param out
	 * @throws SendException 
	 */
	protected void doSend(SendRequest sendRequest, ISendResultListener resultListener, OutputStream out) throws SendException {
		if (out == null) {
			throw new SendException("Not connected");
		}
		
		// Get sentences
		String[] sentences = sendRequest.createSentences();
		
		// Create and start thread
		SendThread sendThread = sendThreadPool.createSendThread(sendRequest, resultListener);
		
		// Write to out
		String str = StringUtils.join(sentences, "\r\n") + "\r\n";
		LOG.debug("Sending:\n" + str);
		try {
			out.write(str.getBytes());
		} catch (IOException e) {
			throw new SendException("Could not send AIS message: " + e.getMessage());
		}
		
		// Start send thread
		sendThread.start();
	}
	
	protected void handleAbk(String msg) {
		Abk abk = new Abk();
		try {
			abk.parse(msg);
			sendThreadPool.handleAbk(abk);
		} catch (Exception e) {
			LOG.error("Failed to parse ABK: " + msg + ": " + e.getMessage());
		}
	}
	
	protected void handleReplay(String msg) {
		// Check if proprietary sentence
		if (!Sentence.hasProprietarySentence(msg)) {
			return;
		}
		// Get Gatehouse timestamp
		GatehouseFactory ghFactory = new GatehouseFactory();
		if (!ghFactory.match(msg)) {
			return;
		}
		IProprietarySourceTag sourceTag = ghFactory.getSourceTag(msg);
		if (sourceTag == null || sourceTag.getTimestamp() == null) {
			return;
		}
		
		Date timestamp = sourceTag.getTimestamp();
		// TODO if timestamp before some starttime, then just return
		
		// Set replay time to current timestamp
		setReplayTime(timestamp);
		
		if (dataStart == null && getReplayStartDate() != null) {
			if (timestamp.before(getReplayStartDate())) {
				return;
			}
		}
		
		Date now = new Date();
			
		dataEnd = timestamp;
		if (dataStart == null) {
			dataStart = timestamp;
		}
		if (replayStart == null) {
			replayStart = now;
		}
		
		long elapsedData = timestamp.getTime() - dataStart.getTime();
		long elapsedReal = (now.getTime() - replayStart.getTime()) * replaySpeedup;
		long diff = elapsedData - elapsedReal;
		if (diff > 500) {
			EeINS.sleep(diff / replaySpeedup);			
		}
		
		replayEnd = now;
		
	}
	
	protected void handleProprietary(String msg) {
		if (msg.indexOf("$PSTT,10A") >= 0) {
			handlePstt(msg);
		}
		
	}
	
	protected void handleSentence(String msg) {
		if (!isSimulateGps() && gpsListeners.size() > 0 && msg.indexOf("$GPRMC") >= 0) {
			handleGpRmc(msg);
		} else if (vesselAisListeners.size() > 0 && isVdm(msg)) {
			handleAis(msg);
		} else if (Abk.isAbk(msg)) {
			handleAbk(msg);
		} else if (msg.indexOf("$P") >= 0) {
			handleProprietary(msg);
		}
	}

	protected boolean isVdm(String msg) {
		return (msg.indexOf("!AIVDM") >= 0 || msg.indexOf("!AIVDO") >= 0 || msg.indexOf("!BSVDM") >= 0);
	}

	protected void handleAis(String msg) {
		try {
			int result = vdm.parse(msg);
			if (result == 0) {
				// Complete message
				AisMessage aisMessage = AisMessage.getInstance(vdm);
				if (aisMessage == null) {
					vdm = new Vdm();
					return;
				}
				
				boolean ownMessage = false;
				// Check if simulated own ship
				if (isSimulateGps()) {
					ownMessage = (aisMessage.getUserId() == simulatedOwnShip);
				} else {
					ownMessage = vdm.isOwnMessage();
				}
				
				// Distribute GPS from own mesasge
				if (ownMessage) {
					handleGpsFromOwnMessage(aisMessage);
				}
				
				// Distribute message
				synchronized (vesselAisListeners) {
					for (IVesselAisListener vesselAisListener : vesselAisListeners) {
						if (ownMessage) {							
							vesselAisListener.receiveOwnMessage(aisMessage);
						} else {
							vesselAisListener.receive(aisMessage);
						}
					}
				}				

				vdm = new Vdm();
				return;

			} else {
				// result = 1: Wait for more data
				return;
			}
		} catch (AisMessageException e) {
			LOG.error("AisMessageException: " + e.getMessage() + " msg: " + msg);
		} catch (SentenceException e) {
			LOG.error("SentenceException: " + e.getMessage() + " msg: " + msg);
		} catch (SixbitException e) {
			LOG.error("SixbitException: " + e.getMessage() + " msg: " + msg);
		}

	}

	protected void handleGpsFromOwnMessage(AisMessage aisMessage) {
		GpsMessage gpsMessage = new GpsMessage();
		boolean foundPos = false;

		if (aisMessage instanceof AisPositionMessage) {
			AisPositionMessage posMessage = (AisPositionMessage) aisMessage;
			gpsMessage.setSog((double) posMessage.getSog() / 10.0);
			gpsMessage.setCog((double) posMessage.getCog() / 10.0);
			gpsMessage.setPos(posMessage.getPos().getGeoLocation());
			foundPos = true;
		} else if (aisMessage instanceof AisMessage18) {
			AisMessage18 msg18 = (AisMessage18) aisMessage;
			gpsMessage.setSog((double) msg18.getSog() / 10.0);
			gpsMessage.setCog((double) msg18.getCog() / 10.0);
			gpsMessage.setPos(msg18.getPos().getGeoLocation());
			foundPos = true;
		}

		if (!foundPos) {
			return;
		}
		
		gpsMessage.validateFields();

		if (replay) {
			GnssTimeMessage gnssTimeMessage = new GnssTimeMessage(getReplayTime());
			synchronized (gnssTimeListeners) {
				for (IGnssTimeListener gnssTimeListener : gnssTimeListeners) {
					gnssTimeListener.receive(gnssTimeMessage);
				}
			}
		}
		
		synchronized (gpsListeners) {
			for (IGpsListener gpsListener : gpsListeners) {
				gpsListener.receive(gpsMessage);
			}
		}
	}

	protected void handleGpRmc(String msg) {
		GpRmcSentence sentence = new GpRmcSentence();
		try {
			sentence.parse(msg);
		} catch (Exception e) {
			LOG.error("Failed to parse GPRMC sentence: " + msg + " : " + e.getMessage());
			return;
		}
		// Only AIS own messages will be used for positioning
//		synchronized (gpsListeners) {
//			for (IGpsListener gpsListener : gpsListeners) {
//				gpsListener.receive(sentence.getGpsMessage());
//			}
//		}
		synchronized (gnssTimeListeners) {
			for (IGnssTimeListener gnssTimeListener : gnssTimeListeners) {
				gnssTimeListener.receive(sentence.getGnssTimeMessage());
			}
		}
	}
	
	private void handlePstt(String msg) {
		PsttSentence psttSentence = new PsttSentence();
		try {
			if (psttSentence.parse(msg)) {
				synchronized (gnssTimeListeners) {
					for (IGnssTimeListener gnssTimeListener : gnssTimeListeners) {
						gnssTimeListener.receive(psttSentence.getGnssTimeMessage());
					}
				}
			}
		} catch (SentenceException e) {
			LOG.error("Failed to handle $PSTT,10A: " + e.getMessage());
		}
	}


	public void addGpsListener(IGpsListener gpsListener) {
		synchronized (gpsListeners) {
			gpsListeners.add(gpsListener);
		}
	}
	
	public void removeGpsListener(IGpsListener gpsListener) {
		synchronized (gpsListeners) {
			gpsListeners.remove(gpsListener);
		}
	}

	public void addAisListener(IVesselAisListener vesselAisListener) {
		synchronized (vesselAisListeners) {
			vesselAisListeners.add(vesselAisListener);
		}
	}
	
	public void removeAisListener(IVesselAisListener vesselAisListener) {
		synchronized (vesselAisListeners) {
			vesselAisListeners.remove(vesselAisListener);
		}
	}
	
	public void addGnssTimeListener(IGnssTimeListener gnssTimeListener) {
		synchronized (gnssTimeListeners) {
			gnssTimeListeners.add(gnssTimeListener);
		}
	}
	
	public void removeGnssTimeListener(IGnssTimeListener gnssTimeListener) {
		synchronized (gnssTimeListeners) {
			gnssTimeListeners.remove(gnssTimeListener);
		}
	}

	public boolean isSimulateGps() {
		return simulateGps;
	}

	public void setSimulateGps(boolean simulateGps) {
		this.simulateGps = simulateGps;
	}

	public long getSimulatedOwnShip() {
		return simulatedOwnShip;
	}

	public void setSimulatedOwnShip(long simulatedOwnShip) {
		this.simulatedOwnShip = simulatedOwnShip;
	}

	public void addSensorType(SensorType type) {
		sensorTypes.add(type);
	}
	
	public boolean isSensorType(SensorType type) {
		return sensorTypes.contains(type);
	}
	
	public void start() {
		(new Thread(this)).start();
	}
	
	public boolean isReplay() {
		return replay;
	}
	
	public void setReplay(boolean replay) {
		this.replay = replay;
	}
	
	public int getReplaySpeedup() {
		return replaySpeedup;
	}
	
	public void setReplaySpeedup(int replaySpeedup) {
		this.replaySpeedup = replaySpeedup;
	}
	
	public Date getReplayStart() {
		return replayStart;
	}

	public Date getReplayEnd() {
		return replayEnd;
	}
	
	public Date getDataStart() {
		return dataStart;
	}
	
	public Date getDataEnd() {
		return dataEnd;
	}
	
	public Date getReplayTime() {
		synchronized (replayTime) {
			return replayTime;
		}		
	}
	
	public void setReplayTime(Date replayTime) {
		synchronized (replayTime) {
			this.replayTime = replayTime;
		}
	}
	
	public Date getReplayStartDate() {
		return replayStartDate;
	}
	
	public void setReplayStartDate(Date replayStartDate) {
		this.replayStartDate = replayStartDate;
	}

}
