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
package dk.frv.enav.ins.gps;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.nmea.GpsMessage;
import dk.frv.enav.ins.nmea.IGpsListener;
import dk.frv.enav.ins.nmea.NmeaSensor;
import dk.frv.enav.ins.nmea.SensorType;
import dk.frv.enav.ins.status.GpsStatus;
import dk.frv.enav.ins.status.IStatusComponent;

/**
 * Component class for handling received GPS messages. 
 */
public class GpsHandler extends MapHandlerChild implements IGpsListener, IStatusComponent, Runnable {

	private static final long GPS_TIMEOUT = 60 * 1000; // 1 min

	private Set<IGpsDataListener> listeners = new HashSet<IGpsDataListener>();
	private GpsData currentData = new GpsData();
	private NmeaSensor nmeaSensor = null;

	public GpsHandler() {
		EeINS.startThread(this, "GpsHandler");
	}
	
	@Override
	public GpsStatus getStatus() {
		return new GpsStatus(getCurrentData());
	}

	/**
	 * Receive GPS message
	 */
	@Override
	public synchronized void receive(GpsMessage gpsMessage) {
		Date now = new Date();
		long elapsed = now.getTime() - currentData.getLastUpdated().getTime();
		if (elapsed < 900) {
			return;
		}
		
		currentData.setLastUpdated(now);
		if (gpsMessage.getPos() == null || !gpsMessage.isValidPosition()) {
			currentData.setBadPosition(true);
		} else {
			currentData.setPosition(gpsMessage.getPos());			
			currentData.setBadPosition(false);
		}
		if (gpsMessage.getCog() != null) {
			currentData.setCog(gpsMessage.getCog());
		}
		if (gpsMessage.getSog() != null) {
			currentData.setSog(gpsMessage.getSog());
		}
		distributeUpdate();
	}

	/**
	 * Mark the current data as invalid
	 */
	private synchronized void markBadPos() {
		currentData.setBadPosition(true);
	}

	/**
	 * Distribute update to all listeners
	 */
	private void distributeUpdate() {
		synchronized (listeners) {
			for (IGpsDataListener listener : listeners) {
				GpsData currentCopy = getCurrentData();
				listener.gpsDataUpdate(currentCopy);
			}
		}
	}
	
	/**
	 * Return if the current data has timed out
	 */
	public synchronized boolean gpsTimedOut() {
		Date now = new Date();
		return now.getTime() - currentData.getLastUpdated().getTime() > GPS_TIMEOUT;
	}

	/**
	 * Routine for monitoring timeout
	 */
	@Override
	public void run() {
		while (true) {
			if (gpsTimedOut()) {
				markBadPos();
				distributeUpdate();
			}
			EeINS.sleep(10000);
		}
	}

	public synchronized GpsData getCurrentData() {
		return new GpsData(currentData);
	}

	public void addListener(IGpsDataListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void removeListener(IGpsDataListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override
	public void findAndInit(Object obj) {
		if (nmeaSensor != null) {
			return;
		}
		if (obj instanceof NmeaSensor) {
			NmeaSensor sensor = (NmeaSensor) obj;
			if (sensor.isSensorType(SensorType.GPS)) {
				nmeaSensor = sensor;
				nmeaSensor.addGpsListener(this);
			}
		}
	}

	@Override
	public void findAndUndo(Object obj) {
		if (obj == nmeaSensor) {
			nmeaSensor.removeGpsListener(this);
		}
	}

}
