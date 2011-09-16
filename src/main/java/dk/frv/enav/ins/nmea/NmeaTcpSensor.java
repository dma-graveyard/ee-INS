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

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import dk.frv.ais.reader.ISendResultListener;
import dk.frv.ais.reader.SendException;
import dk.frv.ais.reader.SendRequest;

/**
 * TCP NMEA sensor
 */
public class NmeaTcpSensor extends NmeaSensor {

	private static final Logger LOG = Logger.getLogger(NmeaTcpSensor.class);
	
	private static final int TCP_READ_TIMEOUT = 60000; // 1 min
	
	private long reconnectInterval = 5000; // Default 5 sec
	private String hostname;
	private int port;
	private OutputStream outputStream;
	
	private Socket clientSocket = new Socket();
	
	public NmeaTcpSensor() {		
	}
	
	public NmeaTcpSensor(String hostname, int port) {
		this();
		this.hostname = hostname;
		this.port = port;
	}
	
	public NmeaTcpSensor(String hostPort) {
		this();
		String[] parts = StringUtils.split(hostPort, ':');
		this.hostname = parts[0];
		this.port = Integer.parseInt(parts[1]);
	}
	
	@Override
	public void run() {

		while (true) {
			try {
				disconnect();
				connect();
				readLoop(clientSocket.getInputStream());
			} catch (IOException e) {
				LOG.error("TCP NMEA sensor failed: " + e.getMessage() + " retry in " + (reconnectInterval / 1000)
						+ " seconds");
				try {
					Thread.sleep(reconnectInterval);
				} catch (InterruptedException intE) {
				}
			}
		}
	}
	
	private void connect() throws IOException {
		try {			
			clientSocket = new Socket();
			InetSocketAddress address = new InetSocketAddress(hostname, port);
			clientSocket.connect(address);
			clientSocket.setKeepAlive(true);
			clientSocket.setSoTimeout(TCP_READ_TIMEOUT);
			outputStream = clientSocket.getOutputStream();
			LOG.info("NMEA source connected " + hostname + ":" + port);
		} catch (UnknownHostException e) {
			LOG.error("Unknown host: " + hostname + ": " + e.getMessage());
			throw e;
		} catch (IOException e) {
			LOG.error("Could not connect to NMEA source: " + hostname + ": " + e.getMessage());
			throw e;
		}
	}

	private void disconnect() {
		if (clientSocket != null && getStatus() == Status.CONNECTED) {
			try {
				LOG.info("Disconnecting source " + hostname + ":" + port);
				clientSocket.close();
			} catch (IOException e) {
			}
		}
	}
	
	@Override
	public void send(SendRequest sendRequest, ISendResultListener resultListener) throws SendException {
		doSend(sendRequest, resultListener, outputStream);
	}

	public Status getStatus() {
		synchronized (clientSocket) {
			if (clientSocket != null && clientSocket.isConnected()) {
				return Status.CONNECTED;
			}
			return Status.DISCONNECTED;
		}
	}
	
	public long getReconnectInterval() {
		return reconnectInterval;
	}
	
	public void setReconnectInterval(long reconnectInterval) {
		this.reconnectInterval = reconnectInterval;
	}
	
	
	
}
