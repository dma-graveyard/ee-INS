package dk.frv.enav.ins.nmea;

import java.awt.Frame;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import dk.frv.ais.reader.ISendResultListener;
import dk.frv.ais.reader.SendException;
import dk.frv.ais.reader.SendRequest;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.settings.SensorSettings;

public class NmeaFileSensor extends NmeaSensor {
	
	private static final Logger LOG = Logger.getLogger(NmeaFileSensor.class);
	
	private String filename;
	private Frame frame = null;
	
	public NmeaFileSensor(String filename, SensorSettings sensorSettings) {
		LOG.info("Using AIS replay file: " + filename);
		this.filename = filename;
		setReplay(true);
		setReplaySpeedup(sensorSettings.getReplaySpeedup());
		setReplayStartDate(sensorSettings.getReplayStartDate());
		LOG.info("Replay start date: " + sensorSettings.getReplayStartDate());
	}

	@Override
	public void run() {
		// Open file
		InputStream in = null;
		try {
			in = new FileInputStream(filename);
		} catch (IOException e) {
			LOG.error("Failed to open replay file: " + filename + ": " + e.getMessage());
			return;
		}
		
		// Wait for frame and confirmation
		while (frame == null) {
			EeINS.sleep(1000);
		}
		EeINS.sleep(5000);
		JOptionPane.showMessageDialog(frame, "Start replay");		
		
		
		// Read
		try {
			readLoop(in);
		} catch (IOException e) {
			LOG.error("Error while reading replay file: " + filename + ": " + e.getMessage());
		}
		
		long dataElapsed = getDataEnd().getTime() - getDataStart().getTime();
		long realElapsed = (getReplayEnd().getTime() - getReplayStart().getTime()) * getReplaySpeedup();
				
		LOG.info("Replay data start: " + getDataStart() + " end: " + getDataEnd() + " elapsed: " + (dataElapsed / 1000));
		LOG.info("Replay real start: " + getReplayStart() + " end: " + getReplayEnd() + " elapsed: " + (realElapsed / 1000));
		
		if (frame != null) {
			JOptionPane.showMessageDialog(frame, "Replay finished");
		}

	}
	
	@Override
	public void findAndInit(Object obj) {
		super.findAndInit(obj);
		if (obj instanceof Frame) {
			frame = (Frame)obj;
		}
	}

	@Override
	public void send(SendRequest sendRequest, ISendResultListener resultListener) throws SendException {
		throw new SendException("Cannot send to file sensor");
	}

}
