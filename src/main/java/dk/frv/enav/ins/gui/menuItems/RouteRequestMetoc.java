package dk.frv.enav.ins.gui.menuItems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gui.route.MetocRequestDialog;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;

public class RouteRequestMetoc extends JMenuItem implements IMapMenuAction {

	private static final long serialVersionUID = 1L;
	private int routeIndex;
	private RouteManager routeManager;

	public RouteRequestMetoc(String text) {
		super();
		setText(text);
	}

	@Override
	public void doAction() {
		Route route = routeManager.getRoute(routeIndex);
		if (routeManager.isRouteActive()) {
			route = routeManager.getActiveRoute();
		}
		MetocRequestDialog.requestMetoc(EeINS.getMainFrame(), routeManager, route);		
	}

	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}

	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

}
