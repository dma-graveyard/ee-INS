package dk.frv.enav.ins.layers.route;

import dk.frv.enav.ins.layers.common.WpCircle;
import dk.frv.enav.ins.route.Route;

public class WaypointCircle extends WpCircle {
	private static final long serialVersionUID = 1L;
	
	private Route route;
	private int wpIndex;

	private int routeIndex;

	public WaypointCircle(Route route, int routeIndex, int wpIndex) {
		super();
		this.routeIndex = routeIndex;
		this.route = route;
		this.wpIndex = wpIndex;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public int getWpIndex() {
		return wpIndex;
	}

	public void setWpIndex(int wpIndex) {
		this.wpIndex = wpIndex;
	}
	
	public int getRouteIndex() {
		return routeIndex;
	}
}
