package dk.frv.enav.ins.gui.menuItems;

import java.util.LinkedList;

import javax.swing.JMenuItem;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.layers.routeEdit.NewRouteContainerLayer;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteLeg;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RouteWaypoint;

public class RouteEditEndRoute extends JMenuItem implements IMapMenuAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NewRouteContainerLayer newRouteLayer;
	private RouteManager routeManager;

	public RouteEditEndRoute(String text) {
		super();
		setText(text);
	}

	@Override
	public void doAction() {
		if (newRouteLayer.getRoute().getWaypoints().size() > 1) {
			Route route = new Route(newRouteLayer.getRoute());
			route.setName("New route");
			int i = 1;
			LinkedList<RouteWaypoint> waypoints = route.getWaypoints();
			for (RouteWaypoint routeWaypoint : waypoints) {
				if (routeWaypoint.getOutLeg() != null) {
					RouteLeg outLeg = routeWaypoint.getOutLeg();
					double xtd = EeINS.getSettings().getNavSettings().getDefaultXtd();
					outLeg.setXtdPort(xtd);
					outLeg.setXtdStarboard(xtd);
					outLeg.setHeading(Heading.RL);
					outLeg.setSpeed(EeINS.getSettings().getNavSettings().getDefaultSpeed());
				}
				routeWaypoint.setTurnRad(EeINS.getSettings().getNavSettings().getDefaultTurnRad());
				routeWaypoint.setName(String.format("WP_%03d", i));
				i++;
			}
			route.calcValues(true);
			routeManager.addRoute(route);
			routeManager.notifyListeners(null);
		}
		newRouteLayer.getWaypoints().clear();
		newRouteLayer.getRouteGraphics().clear();
		newRouteLayer.doPrepare();
		EeINS.getMainFrame().getChartPanel().editMode(false);
	}

	public void setNewRouteLayer(NewRouteContainerLayer newRouteLayer) {
		this.newRouteLayer = newRouteLayer;
	}

	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}
}
