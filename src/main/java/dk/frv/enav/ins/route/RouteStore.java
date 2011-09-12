package dk.frv.enav.ins.route;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dk.frv.enav.ins.ais.AisAdressedRouteSuggestion;

public class RouteStore implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Set<AisAdressedRouteSuggestion> addressedSuggestedRoutes = new HashSet<AisAdressedRouteSuggestion>();
	private List<Route> routes = new LinkedList<Route>();
	private ActiveRoute activeRoute = null;
	private int activeRouteIndex = -1;
	
	public RouteStore(RouteManager routeManager) {
		this.routes = routeManager.getRoutes();
		this.activeRoute = routeManager.getActiveRoute();
		this.activeRouteIndex = routeManager.getActiveRouteIndex();
		this.addressedSuggestedRoutes = routeManager.getAddressedSuggestedRoutes();
	}
	
	public List<Route> getRoutes() {
		return routes;
	}
	
	public ActiveRoute getActiveRoute() {
		return activeRoute;
	}
	
	public int getActiveRouteIndex() {
		return activeRouteIndex;
	}
	
	public Set<AisAdressedRouteSuggestion> getAddressedSuggestedRoutes() {
		return addressedSuggestedRoutes;
	}
	
}
