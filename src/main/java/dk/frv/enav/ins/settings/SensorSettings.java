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
package dk.frv.enav.ins.settings;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bbn.openmap.util.PropUtils;

import dk.frv.enav.common.FormatException;
import dk.frv.enav.common.util.DateUtils;
import dk.frv.enav.common.util.ParseUtils;

public class SensorSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG = Logger.getLogger(SensorSettings.class);
	
	private static final String PREFIX = "sensor.";
	
	public enum SensorConnectionType {
		NONE, TCP, SERIAL, FILE, AIS_SHARED;
		public static SensorConnectionType parseString(String type) {
			if (type.equalsIgnoreCase("TCP")) {
				return TCP;
			} else if (type.equalsIgnoreCase("SERIAL")) {
				return SERIAL;
			} else if (type.equalsIgnoreCase("FILE")) {
				return FILE;
			} else if (type.equalsIgnoreCase("AIS_SHARED")) {
				return AIS_SHARED;
			}
			return NONE;
		}
	}
	
	private SensorConnectionType aisConnectionType = SensorConnectionType.TCP; 
	private String aisHostOrSerialPort = "localhost";
	private String aisFilename = "";
	private int aisTcpPort = 4001;
	
	private SensorConnectionType gpsConnectionType = SensorConnectionType.AIS_SHARED;
	private String gpsHostOrSerialPort = "COM11";
	private String gpsFilename = "";
	private int gpsTcpPort = 8888;
	
	private boolean simulateGps = false;
	private long simulatedOwnShip = 219622000; // Scanlines Helsinore
	/**
	 * If farther away than this range, the messages are discarded
	 * In nautical miles (theoretical distance is about 40 miles)
	*/
	private double aisSensorRange = 0;
		
	private int replaySpeedup = 1;
	private Date replayStartDate = null;

	public SensorSettings() {
		
	}
	
	public void readProperties(Properties props) {
		aisConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "aisConnectionType", aisConnectionType.name()));
		aisHostOrSerialPort = props.getProperty(PREFIX + "aisHostOrSerialPort", aisHostOrSerialPort);
		aisTcpPort = PropUtils.intFromProperties(props, PREFIX + "aisTcpPort", aisTcpPort);
		gpsConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "gpsConnectionType", gpsConnectionType.name()));
		gpsHostOrSerialPort = props.getProperty(PREFIX + "gpsHostOrSerialPort", gpsHostOrSerialPort);
		gpsTcpPort = PropUtils.intFromProperties(props, PREFIX + "gpsTcpPort", gpsTcpPort);
		simulateGps = PropUtils.booleanFromProperties(props, PREFIX + "simulateGps", simulateGps);
		simulatedOwnShip = PropUtils.longFromProperties(props, PREFIX + "simulatedOwnShip", simulatedOwnShip);
		aisSensorRange = PropUtils.doubleFromProperties(props, PREFIX + "aisSensorRange", aisSensorRange);
		aisFilename = props.getProperty(PREFIX + "aisFilename", aisFilename);
		gpsFilename = props.getProperty(PREFIX + "gpsFilename", gpsFilename);
		replaySpeedup = PropUtils.intFromProperties(props, PREFIX + "replaySpeedup", replaySpeedup);
		String replayStartStr = props.getProperty(PREFIX + "replayStartDate", "");
		if (replayStartStr.length() > 0) {
			try {
				replayStartDate = ParseUtils.parseIso8602(replayStartStr);
				LOG.info("replayStartDate: " + replayStartDate);
			} catch (FormatException e) {
				LOG.error("Failed to parse replayStartDate");
			}
		}
	}
	
	public void setProperties(Properties props) {
		props.put(PREFIX + "aisConnectionType", aisConnectionType.name());
		props.put(PREFIX + "aisHostOrSerialPort", aisHostOrSerialPort);
		props.put(PREFIX + "aisTcpPort", Integer.toString(aisTcpPort));
		props.put(PREFIX + "gpsConnectionType", gpsConnectionType.name());
		props.put(PREFIX + "gpsHostOrSerialPort", gpsHostOrSerialPort);
		props.put(PREFIX + "gpsTcpPort", Integer.toString(gpsTcpPort));
		props.put(PREFIX + "simulateGps", Boolean.toString(simulateGps));
		props.put(PREFIX + "simulatedOwnShip", Long.toString(simulatedOwnShip));
		props.put(PREFIX + "aisSensorRange", Double.toString(aisSensorRange));
		props.put(PREFIX + "aisFilename", aisFilename);
		props.put(PREFIX + "gpsFilename", gpsFilename);
		props.put(PREFIX + "replaySpeedup", Integer.toString(replaySpeedup));
		String replayStartStr = "";
		if (replayStartDate != null) {			
			replayStartStr = DateUtils.getISO8620(replayStartDate);
		}
		props.put(PREFIX + "replayStartDate", replayStartStr);
	}

	public SensorConnectionType getAisConnectionType() {
		return aisConnectionType;
	}

	public void setAisConnectionType(SensorConnectionType aisConnectionType) {
		this.aisConnectionType = aisConnectionType;
	}

	public String getAisHostOrSerialPort() {
		return aisHostOrSerialPort;
	}

	public void setAisHostOrSerialPort(String aisHostOrSerialPort) {
		this.aisHostOrSerialPort = aisHostOrSerialPort;
	}

	public int getAisTcpPort() {
		return aisTcpPort;
	}

	public void setAisTcpPort(int aisTcpPort) {
		this.aisTcpPort = aisTcpPort;
	}

	public SensorConnectionType getGpsConnectionType() {
		return gpsConnectionType;
	}

	public void setGpsConnectionType(SensorConnectionType gpsConnectionType) {
		this.gpsConnectionType = gpsConnectionType;
	}

	public String getGpsHostOrSerialPort() {
		return gpsHostOrSerialPort;
	}

	public void setGpsHostOrSerialPort(String gpsHostOrSerialPort) {
		this.gpsHostOrSerialPort = gpsHostOrSerialPort;
	}

	public int getGpsTcpPort() {
		return gpsTcpPort;
	}

	public void setGpsTcpPort(int gpsTcpPort) {
		this.gpsTcpPort = gpsTcpPort;
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
	
	public double getAisSensorRange() {
		return aisSensorRange;
	}
	
	public void setAisSensorRange(double aisSensorRange) {
		this.aisSensorRange = aisSensorRange;
	}
	
	public String getAisFilename() {
		return aisFilename;
	}
	
	public void setAisFilename(String aisFilename) {
		this.aisFilename = aisFilename;
	}
	
	public String getGpsFilename() {
		return gpsFilename;
	}
	
	public void setGpsFilename(String gpsFilename) {
		this.gpsFilename = gpsFilename;
	}
	
	public int getReplaySpeedup() {
		return replaySpeedup;
	}
	
	public void setReplaySpeedup(int replaySpeedup) {
		this.replaySpeedup = replaySpeedup;
	}
	
	public Date getReplayStartDate() {
		return replayStartDate;
	}

	public void setReplayStartDate(Date replayStartDate) {
		this.replayStartDate = replayStartDate;
	}
	
}
