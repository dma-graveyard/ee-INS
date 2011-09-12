package dk.frv.enav.ins.layers.route;

import java.awt.Color;
import java.awt.Stroke;

import dk.frv.enav.ins.route.Route;

public class ActiveRouteGraphic extends RouteGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActiveRouteGraphic(Route route, int routeIndex, boolean arrowsVisible, Stroke stroke, Color color) {
		super(route, routeIndex, arrowsVisible, stroke, color);
	}

}
