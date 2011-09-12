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

	private synchronized void markBadPos() {
		currentData.setBadPosition(true);
	}

	private void distributeUpdate() {
		synchronized (listeners) {
			for (IGpsDataListener listener : listeners) {
				GpsData currentCopy = getCurrentData();
				listener.gpsDataUpdate(currentCopy);
			}
		}
	}
	
	public synchronized boolean gpsTimedOut() {
		Date now = new Date();
		return now.getTime() - currentData.getLastUpdated().getTime() > GPS_TIMEOUT;
	}

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
