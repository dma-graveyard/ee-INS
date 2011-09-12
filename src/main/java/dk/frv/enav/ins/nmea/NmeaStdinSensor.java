package dk.frv.enav.ins.nmea;

import java.io.IOException;

import org.apache.log4j.Logger;

import dk.frv.ais.reader.ISendResultListener;
import dk.frv.ais.reader.SendException;
import dk.frv.ais.reader.SendRequest;

public class NmeaStdinSensor extends NmeaSensor {
	
	private static final Logger LOG = Logger.getLogger(NmeaStdinSensor.class);
	
	public NmeaStdinSensor() {
		
	}
	
	@Override
	public void run() {
		try {
			readLoop(System.in);
		} catch (IOException e) {
			LOG.error("Failed to open stdin");
		}
	}
	
	@Override
	public void send(SendRequest sendRequest, ISendResultListener resultListener) throws SendException {
		throw new SendException("Cannot send to stdin sensor");
	}

}
