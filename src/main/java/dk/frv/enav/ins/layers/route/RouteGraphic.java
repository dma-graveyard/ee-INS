/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
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
