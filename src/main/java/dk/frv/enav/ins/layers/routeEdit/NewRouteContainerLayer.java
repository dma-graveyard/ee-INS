package dk.frv.enav.ins.layers.routeEdit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.LinkedList;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.ins.layers.route.RouteGraphic;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteWaypoint;

public class NewRouteContainerLayer extends OMGraphicHandlerLayer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedList<RouteWaypoint> waypoints = new LinkedList<RouteWaypoint>();
	private Route route;
	private OMGraphicList graphics = new OMGraphicList();
	private RouteGraphic routeGraphics;

	public NewRouteContainerLayer() {
		route = new Route();
		route.setWaypoints(waypoints);
		routeGraphics = new RouteGraphic(true, new BasicStroke(2), Color.black);
		graphics.add(routeGraphics);
	}
	
	@Override
	public synchronized OMGraphicList prepare() {
		routeGraphics.setRoute(route);
		graphics.project(getProjection());
		return graphics;
	}
	
	@Override
	public void findAndInit(Object obj) {
		super.findAndInit(obj);
	}
	
	@Override
	public void findAndUndo(Object obj) {
		// TODO Auto-generated method stub
		super.findAndUndo(obj);
	}
	
	public Route getRoute() {
		return route;
	}
	
	public LinkedList<RouteWaypoint> getWaypoints() {
		return waypoints;
	}
	
	public RouteGraphic getRouteGraphics() {
		return routeGraphics;
	}
}
