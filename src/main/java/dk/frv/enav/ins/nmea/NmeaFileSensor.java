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
