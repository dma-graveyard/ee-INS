package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

public class RouteAppendWaypoint extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RouteManager routeManager;
	private int routeIndex;

	public RouteAppendWaypoint(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		Route route = routeManager.getRoute(routeIndex);
		route.appendWaypoint();
		routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_APPENDED);
	}
	
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

}
