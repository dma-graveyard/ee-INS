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
package dk.frv.enav.ins.layers.routeEdit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.LinkedList;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.ins.layers.route.RouteGraphic;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteWaypoint;

/**
 * Container layer for new route drawing.
 */
public class NewRouteContainerLayer extends OMGraphicHandlerLayer {

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
