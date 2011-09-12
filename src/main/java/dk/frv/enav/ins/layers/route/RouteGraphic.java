package dk.frv.enav.ins.layers.route;

import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteLeg;
import dk.frv.enav.ins.route.RouteWaypoint;

public class RouteGraphic extends OMGraphicList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Route route;
	private boolean arrowsVisible;
	private LinkedList<RouteWaypoint> routeWaypoints;
	private List<RouteLegGraphic> routeLegs = new ArrayList<RouteLegGraphic>();
	
	protected Stroke stroke = null;
	protected Color color = null;

	private int routeIndex;
	
	public RouteGraphic(Route route, int routeIndex, boolean arrowsVisible, Stroke stroke, Color color) {
		super();
		this.route = route;
		this.routeIndex = routeIndex;
		this.arrowsVisible = arrowsVisible;
		this.stroke = stroke;
		this.color = color;
		initGraphics();
	}
	
	public RouteGraphic(boolean arrowsVisible, Stroke stroke, Color color) {
		super();
		this.arrowsVisible = arrowsVisible;
		this.stroke = stroke;
		this.color = color;
	}
	
	public void setRoute(Route route) {
		this.route = route;
		initGraphics();
	}
	
	public void initGraphics(){
		routeWaypoints = route.getWaypoints();
		int i = 0;
		for (RouteWaypoint routeWaypoint : routeWaypoints) {
			if(route instanceof ActiveRoute && ((ActiveRoute) route).getActiveWaypointIndex() == i){
				RouteWaypointGraphic routeWaypointGraphicActive = new RouteWaypointGraphic(route, routeIndex, i,routeWaypoint, Color.RED, 30, 30);
				add(0,routeWaypointGraphicActive);
			}			
			if(routeWaypoint.getOutLeg() != null){
				RouteLeg routeLeg = routeWaypoint.getOutLeg();
				RouteLegGraphic routeLegGraphic = new RouteLegGraphic(routeLeg, routeIndex, this.color, this.stroke);
				add(routeLegGraphic);
				routeLegs.add(0,routeLegGraphic);
			}
			RouteWaypointGraphic routeWaypointGraphic = new RouteWaypointGraphic(route, routeIndex, i, routeWaypoint, this.color, 18, 18);
			add(0,routeWaypointGraphic);
			i++;
		}
	}
	
	public void showArrowHeads(boolean show){
		if(this.arrowsVisible != show){
			for (RouteLegGraphic routeLeg : routeLegs) {
				routeLeg.setArrows(show);
			}
			this.arrowsVisible = show;
		}
	}
}
