package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.route.RouteManager;

public class RouteActivateToggle extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RouteManager routeManager;
	private int routeIndex;

	public RouteActivateToggle() {
		super();
	}
	
	@Override
	public void doAction() {
		if(routeManager.getActiveRouteIndex() == routeIndex){
			routeManager.deactivateRoute();
		} else {
			routeManager.activateRoute(routeIndex);
		}
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}
	
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
}
