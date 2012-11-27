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
package dk.frv.enav.ins.service.communication.ais;

import org.apache.log4j.Logger;

import dk.frv.ais.reader.ISendResultListener;
import dk.frv.ais.reader.SendException;
import dk.frv.ais.reader.SendRequest;
import dk.frv.ais.sentence.Abk;

/**
 * Thread for sending AIS messages
 */
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
			aisServices.sendResult(false);
			return;
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
