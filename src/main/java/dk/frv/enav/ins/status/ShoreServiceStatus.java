package dk.frv.enav.ins.status;

import java.util.Date;

import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.services.shore.ShoreServiceException;

public class ShoreServiceStatus extends ComponentStatus {

	private Date lastContact = null;
	private Date lastFailed = null;
	private ShoreServiceException lastException = null;

	public ShoreServiceStatus() {
		super("Shore services");
		shortStatusText = "No services performed yet";
	}

	public synchronized void markContactSuccess() {
		lastContact = new Date();
		status = Status.OK;
		shortStatusText = "Last shore contact: " + lastContact;
	}

	public synchronized void markContactError(ShoreServiceException e) {		
		lastFailed = new Date();
		status = Status.ERROR;
		this.lastException = e;
		shortStatusText = "Last failed shore contact: " + Formatter.formatLongDateTime(lastFailed);
	}

	public Date getLastContact() {
		return lastContact;
	}

	public Date getLastFailed() {
		return lastFailed;
	}
	
	@Override
	public String getStatusHtml() {
		StringBuilder buf = new StringBuilder();
		buf.append("Contact: " + status.name() + "<br/>");
		if (status == Status.ERROR) {
			buf.append("Last error: " + Formatter.formatLongDateTime(lastFailed) + "<br/>");
			buf.append("Error message: " + lastException.getMessage());
			if (lastException.getExtraMessage() != null) {
				 buf.append(": " + lastException.getExtraMessage());
			}
		} else {
			buf.append("Last contact: " + Formatter.formatLongDateTime(lastContact));
		}
		
		
		return buf.toString();
	}

}