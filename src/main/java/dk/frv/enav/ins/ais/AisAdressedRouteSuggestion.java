package dk.frv.enav.ins.ais;

import java.util.Date;

import dk.frv.ais.message.binary.RouteInformation;
import dk.frv.enav.ins.gps.GnssTime;

public class AisAdressedRouteSuggestion extends AisIntendedRoute {
	private static final long serialVersionUID = 1L;
	
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

	// Copy construct
	public AisAdressedRouteSuggestion(AisAdressedRouteSuggestion routeSuggestion) {
		super(routeSuggestion);
	}
	

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
