/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
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
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.ins.nmea;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import org.apache.log4j.Logger;

import dk.frv.ais.reader.ISendResultListener;
import dk.frv.ais.reader.SendException;
import dk.frv.ais.reader.SendRequest;

/**
 * Serial port NMEA sensor 
 */
public class NmeaSerialSensor extends NmeaSensor implements SerialPortEventListener{
	
	private static Logger LOG = Logger.getLogger(NmeaSerialSensor.class);
	
	private String serialPortName;
	private int portSpeed = 38400;
	private int dataBits = SerialPort.DATABITS_8;
	private int stopBits = SerialPort.STOPBITS_1;
	private int parity = SerialPort.PARITY_NONE;
	private long reconnectInterval = 30000; // Default 30 sec
	private SerialPort serialPort = null;
	CommPortIdentifier portId = null;
	private InputStream inputStream;
	private OutputStream outputStream;
	private StringBuffer buffer = new StringBuffer();
	private Boolean connected = false;
	

	public NmeaSerialSensor(String serialPortName) {
		this.serialPortName = serialPortName;
	}
	
	@Override
	public void run() {
		
		while (true) {
			if (!isConnected()) {
				try {
					connect();
				} catch (Exception e) {
					LOG.error("Failed to open serial port");
				}
			}
			
			
			try {
				Thread.sleep(reconnectInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
	
	private void connect() throws IOException, UnsupportedCommOperationException, PortInUseException, TooManyListenersException {
		// Find port
		findPort();		
		// Open port
		serialPort = (SerialPort) portId.open("SerialSource", 2000);
		// Settings
		serialPort.setSerialPortParams(portSpeed, dataBits, stopBits, parity);
		// Get streams
		inputStream = serialPort.getInputStream();
		outputStream = serialPort.getOutputStream();
		// Add event listener
		serialPort.addEventListener(this);
		serialPort.notifyOnDataAvailable(true);
		serialPort.notifyOnOutputEmpty(true);
		setConnected(true);
	}
	
	public boolean isConnected() {
		synchronized (connected) {
			return connected;
		}
	}
	
	public void setConnected(Boolean connected) {
		synchronized (this.connected) {
			this.connected = connected;
		}		
	}
	
	private void findPort() throws IOException {
		LOG.debug("Searching for port " + serialPortName);
		Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {				
				LOG.debug("portId: " + portId.getName());
				if (portId.getName().equals(serialPortName) || serialPortName.equals("AUTO")) {
					serialPortName = portId.getName();
					break;
				}
			}
			portId = null;
		}
		if (portId == null) {
			throw new IOException("Unable to find serial port " + serialPortName);
		}
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			LOG.debug("Output buffer empty");
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[1024];
			try {
				while (inputStream.available() > 0) {
					int count = inputStream.read(readBuffer);
					for (int i = 0; i < count; i++) {
						buffer.append((char) readBuffer[i]);
						// If line feed we have a whole line
						if (readBuffer[i] == '\n') {
							String msg = buffer.toString();
							handleSentence(msg);
							buffer = new StringBuffer();
						}
					}
				}
			} catch (IOException e) {
				LOG.error("Failed to read serial data: " + e.getMessage());
				serialPort.removeEventListener();
				serialPort.close();
				serialPort = null;
				portId = null;
				setConnected(false);
			}
			break;
		}		
	}

	@Override
	public void send(SendRequest sendRequest, ISendResultListener resultListener) throws SendException {
		doSend(sendRequest, resultListener, outputStream);		
	}
		
}
