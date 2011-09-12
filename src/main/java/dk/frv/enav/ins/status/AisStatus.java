package dk.frv.enav.ins.status;

import java.util.Date;

import dk.frv.enav.ins.common.text.Formatter;

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
