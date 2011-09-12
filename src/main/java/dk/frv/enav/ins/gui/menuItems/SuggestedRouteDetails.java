package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.ais.AisAdressedRouteSuggestion;
import dk.frv.enav.ins.gui.route.RouteSuggestionDialog;

public class SuggestedRouteDetails extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AisAdressedRouteSuggestion suggestedRoute;
	RouteSuggestionDialog routeSuggestionDialog;
	
	public SuggestedRouteDetails(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		routeSuggestionDialog.showSuggestion(suggestedRoute);
	}
	
	public void setSuggestedRoute(AisAdressedRouteSuggestion suggestedRoute) {
		this.suggestedRoute = suggestedRoute;
	}
	
	public void setRouteSuggestionDialog(
			RouteSuggestionDialog routeSuggestionDialog) {
		this.routeSuggestionDialog = routeSuggestionDialog;
	}

}
