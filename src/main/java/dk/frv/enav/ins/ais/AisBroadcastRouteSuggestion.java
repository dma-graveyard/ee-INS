package dk.frv.enav.ins.ais;

import java.util.Date;

import dk.frv.ais.message.binary.RouteInformation;

public class AisBroadcastRouteSuggestion extends AisRouteData {
	private static final long serialVersionUID = 1L;
	
	private Date validFrom;
	private Date validTo;
	
	// Copy contructor
	public AisBroadcastRouteSuggestion(AisBroadcastRouteSuggestion broadcastRouteSuggestion) {
		super(broadcastRouteSuggestion);
	}
	
	public AisBroadcastRouteSuggestion(RouteInformation routeInformation) {
		super(routeInformation);
		validFrom = etaFirst;
		validTo = etaLast;		
	}
	
	public Date getValidFrom() {
		return validFrom;
	}
	
	public Date getValidTo() {
		return validTo;
	}

}
