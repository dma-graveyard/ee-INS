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
package dk.frv.enav.ins.ais;

import java.util.Date;

import dk.frv.ais.message.binary.RouteInformation;
import dk.frv.ais.message.binary.RouteSuggestion;
import dk.frv.enav.ins.gps.GnssTime;

/**
 * Class representing an addressed route suggestion
 */
public class AisAdressedRouteSuggestion extends AisIntendedRoute {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Possible status of a suggestion
	 */
	public enum Status {
		PENDING,
		ACCEPTED,
		REJECTED,
		NOTED,
		IGNORED,
		CANCELLED,
	}
	
	private Status status = Status.PENDING;
	private boolean hidden = false;

	/**
	 * Copy constructor
	 * @param routeSuggestion
	 */
	public AisAdressedRouteSuggestion(RouteSuggestion routeSuggestion) {
		super(routeSuggestion);
		this.msgLinkId = routeSuggestion.getMsgLinkId();
	}
	
	/**
	 * Constructor given AIS route information
	 * @param routeInformation
	 */
	public AisAdressedRouteSuggestion(RouteInformation routeInformation) {
		super(routeInformation);
		
		// Check if ETA in the past
		Date now = GnssTime.getInstance().getDate();
		if (etaFirst != null && etaFirst.before(now)) {
			etaFirst = null;
			speed = null;
		}
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		switch (status) {
		case ACCEPTED:
		case NOTED:
			setHidden(false);
			break;
		case REJECTED:		
		case IGNORED:
		case CANCELLED:
			setHidden(true);
			break;
		}
		this.status = status;
	}
	
	public boolean isReplied() {
		return (status == Status.ACCEPTED || status == Status.NOTED || status == Status.REJECTED);
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public boolean isAcceptable() {
		return (status == Status.PENDING || status == Status.IGNORED); 
	}
	
	public boolean isRejectable() {
		return (status == Status.PENDING || status == Status.IGNORED);
	}
	
	public boolean isNoteable() {
		return (status == Status.PENDING || status == Status.IGNORED);
	}
	
	public boolean isIgnorable() {
		return (status == Status.PENDING); 
	}
	
	public boolean isPostponable() {
		return (status == Status.PENDING); 
	}
	
	public void cancel() {
		setStatus(Status.CANCELLED);
	}

}
