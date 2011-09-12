package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

public class RouteHide extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int routeIndex;
	private RouteManager routeManager;
	
	public RouteHide(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		routeManager.getRoute(routeIndex).setVisible(false);
		routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
	}
	
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

}
