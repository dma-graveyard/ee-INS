package dk.frv.enav.ins.services.ais;

import org.apache.log4j.Logger;

import dk.frv.ais.reader.ISendResultListener;
import dk.frv.ais.reader.SendException;
import dk.frv.ais.reader.SendRequest;
import dk.frv.ais.sentence.Abk;

public class AisSendThread extends Thread implements ISendResultListener {
	
	private static final Logger LOG = Logger.getLogger(AisSendThread.class);

	protected SendRequest sendRequest;
	protected AisServices aisServices;
	protected Abk abk = null;
	protected Boolean abkReceived = false;
	
	public AisSendThread(SendRequest sendRequest, AisServices aisServices) {
		this.sendRequest = sendRequest;
		this.aisServices = aisServices;
	}
	
	@Override
	public void run() {
		// Send message
		try {
			aisServices.getNmeaSensor().send(sendRequest, this);
		} catch (SendException e) {
			LOG.error("Failed to send AIS message: " + sendRequest + ": " + e.getMessage());
			// TODO log this somehow
		}
		
		// Busy wait
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}		
			synchronized (abkReceived) {
				if (abkReceived) {
					break;
				}
			}			
		}
		
		if (abk != null && abk.isSuccess()) {
			LOG.info("AIS SEND SUCCESS");
			aisServices.sendResult(true);
		} else {
			LOG.info("AIS SEND ERROR");
			aisServices.sendResult(false);
		}
		
		LOG.debug("abk: " + abk);
		
		
	}
	
	@Override
	public void sendResult(Abk abk) {
		synchronized (abkReceived) {
			this.abk = abk;
			this.abkReceived = true;
		}			
	}
	
}
