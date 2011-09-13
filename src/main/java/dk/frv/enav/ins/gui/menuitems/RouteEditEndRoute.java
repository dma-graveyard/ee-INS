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
