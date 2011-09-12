package dk.frv.enav.ins.gps;

import java.util.Date;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.enav.ins.nmea.NmeaSensor;
import dk.frv.enav.ins.nmea.SensorType;

public class GnssTime extends MapHandlerChild implements IGnssTimeListener {
	
	private static final Logger LOG = Logger.getLogger(GnssTime.class);
	
	private long offset = 0;
	private NmeaSensor nmeaSensor = null;
	
	private static GnssTime instance = null;
	
	private GnssTime() {
		
	}

	@Override
	public synchronized void receive(GnssTimeMessage gnssTimeMessage) {		
		if (gnssTimeMessage == null) {
			return;
		}
		offset = (new Date()).getTime() - gnssTimeMessage.getTime().getTime();
		LOG.debug("New GPS time offset: " + offset);		
	}
	
	public synchronized Date getDate() {
		return new Date((new Date()).getTime() - offset);		 
	}
	
	public static void init() {
		synchronized (GnssTime.class) {
			if (instance == null) {
				instance = new GnssTime();
			}			
		}
	}
	
	public static GnssTime getInstance() {
		synchronized (GnssTime.class) {
			return instance;
		}
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (nmeaSensor != null) {
			return;
		}
		if (obj instanceof NmeaSensor) {
			NmeaSensor sensor = (NmeaSensor)obj;
			if (sensor.isSensorType(SensorType.GPS)) {
				nmeaSensor = sensor;
				nmeaSensor.addGnssTimeListener(this);
			}
		}
	}
	
	@Override
	public void findAndUndo(Object obj) {
		if (obj == nmeaSensor) {
			nmeaSensor.removeGnssTimeListener(this);
		}
	}

	

}
