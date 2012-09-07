package dk.frv.enav.ins.gui.menuitems;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.gui.MainFrame;
import dk.frv.enav.ins.gui.nogo.NogoDialog;
import dk.frv.enav.ins.gui.route.MetocRequestDialog;
import dk.frv.enav.ins.nogo.NogoHandler;
import dk.frv.enav.ins.route.MonaLisaRouteExchange;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.services.shore.ShoreServiceException;

public class MonaLisaRouteRequest extends JMenuItem implements IMapMenuAction {

	private static final long serialVersionUID = 1L;
	private MonaLisaRouteExchange monaLisaRouteExchange = null;
	
	private int routeIndex;
	private RouteManager routeManager;
	
	public MonaLisaRouteRequest(String text) {
		super();
		setText(text);
	}
	
	
	public void setMonaLisaRouteExchange(MonaLisaRouteExchange monaLisaRouteExchange){
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
		
        try {
			Route newRoute = monaLisaRouteExchange.makeRequest(route);
			
			if (newRoute != null){
				routeManager.addRoute(newRoute);
				
//				route = newRoute;
				
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
}