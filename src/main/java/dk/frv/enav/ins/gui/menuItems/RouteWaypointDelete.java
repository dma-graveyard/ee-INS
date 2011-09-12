package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

public class RouteWaypointDelete extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int routeIndex;
	private int routeWaypointIndex;
	private RouteManager routeManager;

	public RouteWaypointDelete(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		Route route = routeManager.getRoute(routeIndex);
		boolean delete = route.deleteWaypoint(routeWaypointIndex);
		if(delete){
			routeManager.removeRoute(routeIndex);
		}
		routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_DELETED);
	}
	
	public void setRouteWaypointIndex(int routeWaypointIndex) {
		this.routeWaypointIndex = routeWaypointIndex;
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}
	
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
}
