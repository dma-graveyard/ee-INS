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
package dk.frv.enav.ins.gui.menuItems;

import java.awt.Point;

import javax.swing.JMenuItem;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.common.math.Vector2D;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteLeg;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;

public class RouteLegInsertWaypoint extends JMenuItem implements IMapMenuAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RouteLeg routeLeg;
	private Point point;
	private MapBean mapBean;
	private RouteManager routeManager;
	private int routeIndex;

	public RouteLegInsertWaypoint(String text) {
		super();
		setText(text);
	}
	
	@Override
	public void doAction() {
		GeoLocation startWaypoint = routeLeg.getStartWp().getPos();
		GeoLocation endWaypoint = routeLeg.getEndWp().getPos();
		Projection projection = mapBean.getProjection();
		LatLonPoint newPoint = projection.inverse(point);
		
		Vector2D routeLegVector = new Vector2D(startWaypoint.getLongitude(), 
				startWaypoint.getLatitude(), 
				endWaypoint.getLongitude(), 
				endWaypoint.getLatitude());
		
		Vector2D newVector = new Vector2D(startWaypoint.getLongitude(), 
				startWaypoint.getLatitude(), 
				newPoint.getLongitude(), 
				newPoint.getLatitude());
		
		Vector2D projectedVector = routeLegVector.projection(newVector);
		
		GeoLocation newGeoLocation = new GeoLocation(projectedVector.getY2(), projectedVector.getX2());
		
		Route route = routeManager.getRoute(routeIndex);
		route.createWaypoint(routeLeg, newGeoLocation);
		routeManager.notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_APPENDED);
	}
	
	public void setRouteLeg(RouteLeg routeLeg) {
		this.routeLeg = routeLeg;
	}
	
	public void setPoint(Point point) {
		this.point = point;
	}
	
	public void setMapBean(MapBean mapBean) {
		this.mapBean = mapBean;
	}
	
	public void setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
	}
	
	public void setRouteManager(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

}
