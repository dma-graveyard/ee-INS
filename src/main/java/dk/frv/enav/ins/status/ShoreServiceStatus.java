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
package dk.frv.enav.ins.status;

import java.util.Date;

import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.services.shore.ShoreServiceException;

/**
 * Status for shore services
 */
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