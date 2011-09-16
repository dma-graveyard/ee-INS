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
package dk.frv.enav.ins.status;

import java.util.Date;

import dk.frv.enav.ins.common.text.Formatter;

/**
 * AIS status
 */
public class AisStatus extends ComponentStatus {
	
	private static final long RECEPTION_INTERVAL = 30000; // 30 secs
	
	private Date lastReceived = new Date(0);
	private Date lastSent = null;
	private Date lastSendError = null;
	private Boolean sendOk = null;
	private Status sendStatus = Status.UNKNOWN;
	private Status receiveStatus = Status.UNKNOWN;
	
	public AisStatus() {
		super("AIS");
	}
	
	public synchronized void markAisReception() {
		lastReceived = new Date();
	}
	
	public synchronized void markSuccesfullSend() {
		lastSent = new Date();
		sendOk = true;
	}
	
	public synchronized void markFailedSend() {
		lastSendError = new Date();
		sendOk = false;
	}
	
	@Override
	public Status getStatus() {
		shortStatusText = "Reception ";
		// Set status based on times
		
		// Base firstly on reception
		long elapsed = System.currentTimeMillis() - lastReceived.getTime();
		status = (elapsed > RECEPTION_INTERVAL) ? Status.ERROR : Status.OK;
		shortStatusText += status.name() + " - Sending ";
		receiveStatus = status;
		
		if (sendOk != null) {
			sendStatus = sendOk.booleanValue() ? Status.OK : Status.ERROR;
		}
		shortStatusText += sendStatus.name();
		
		// Adjust overall status with sending status
		if (sendStatus == Status.ERROR) {
			if (status == Status.UNKNOWN) {
				status = Status.ERROR;
			}
			if (status == Status.OK) {
				status = Status.PARTIAL;
			}
		}
		
		return status;
	}

	@Override
	public String getStatusHtml() {
		getStatus();
		StringBuilder buf = new StringBuilder();
		buf.append("Reception: " + receiveStatus.name() + "<br/>");
		buf.append("Sending: " + sendStatus.name() + "<br/>");
		buf.append("Last reception: " + Formatter.formatLongDateTime(getLastReceived()) + "<br/>");
		if (sendStatus == Status.ERROR) {
			buf.append("Last send error: " + Formatter.formatLongDateTime(lastSendError));
		} else {
			buf.append("Last send: " + Formatter.formatLongDateTime(lastSent));
		}
		return buf.toString();
	}
	
	public Date getLastReceived() {
		return lastReceived;
	}
	
	public Date getLastSendError() {
		return lastSendError;
	}
	
	public Date getLastSent() {
		return lastSent;
	}

}
