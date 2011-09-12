package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.route.RouteManager;

public class RouteWaypointActivateToggle extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int routeWaypointIndex;
	private RouteManager routeManager;

	public RouteWaypointActivateToggle(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		routeManager.changeActiveWp(routeWaypointIndex);
	}
	
	public void setRouteWaypointIndex(int routeWaypointIndex) {
		this.routeWaypointIndex = routeWaypointIndex;
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

}
