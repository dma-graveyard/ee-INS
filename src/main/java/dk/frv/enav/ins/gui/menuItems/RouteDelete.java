package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

public class RouteDelete extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int routeIndex;
	private RouteManager routeManager;
	
	public RouteDelete(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		if (JOptionPane.showConfirmDialog(this, "Delete route?", "Route dele", JOptionPane.YES_NO_OPTION) == 0) {
			routeManager.removeRoute(routeIndex);
			routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_REMOVED);
		}
	}
	
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

}
