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
