package dk.frv.enav.ins.gui.menuitems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.route.MonaLisaRouteExchange;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;

public class MonaLisaRouteRequest extends JMenuItem implements IMapMenuAction {

	private static final long serialVersionUID = 1L;
	private MonaLisaRouteExchange monaLisaRouteExchange = null;

	private int routeIndex;
	private RouteManager routeManager;

	public MonaLisaRouteRequest(String text) {
		super();
		setText(text);
	}

	public void setMonaLisaRouteExchange(
			MonaLisaRouteExchange monaLisaRouteExchange) {
		this.monaLisaRouteExchange = monaLisaRouteExchange;
	}

	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}

	@Override
	public void doAction() {
		System.out.println("Request a route optimization?");

		Route route = routeManager.getRoute(routeIndex);
		if (routeManager.isRouteActive()) {
			route = routeManager.getActiveRoute();
		}

		monaLisaRouteExchange.makeRouteRequest(route);
		
		// try {
		//
		// Route newRoute = monaLisaRouteExchange.makeRouteRequest(route);
		//
		// if (newRoute != null) {
		// routeManager.addRoute(newRoute);
		//
		// // route = newRoute;
		//
		// }
		//
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
	}

}