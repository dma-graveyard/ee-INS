package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

public class RouteShowMetocToggle extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int routeIndex;
	private RouteManager routeManager;

	public RouteShowMetocToggle() {
		super();
	}
	
	@Override
	public void doAction() {
		Route route = routeManager.getRoute(routeIndex);
		if (routeManager.isActiveRoute(routeIndex)) {
			route = routeManager.getActiveRoute();
		}
		if(route.getRouteMetocSettings().isShowRouteMetoc()){
			route.getRouteMetocSettings().setShowRouteMetoc(false);
		} else {
			route.getRouteMetocSettings().setShowRouteMetoc(true);
		}
		routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_METOC_CHANGED);
	}
	
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

}
