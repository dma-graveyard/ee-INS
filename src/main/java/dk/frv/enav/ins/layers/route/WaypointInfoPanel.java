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

import java.util.Date;

import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.gui.InfoPanel;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteWaypoint;

/**
 * Mouse over info for waypoint. 
 */
public class WaypointInfoPanel extends InfoPanel {
	private static final long serialVersionUID = 1L;
	
	public WaypointInfoPanel() {
		super();
	}
	
	public void showWpInfo(Route route, int wpIndex) {
		RouteWaypoint wp = route.getWaypoints().get(wpIndex);
		
		ActiveRoute activeRoute = null;
		if (route instanceof ActiveRoute) {
			activeRoute = (ActiveRoute)route;
		} else {
			route.adjustStartTime();
		}
		
		Date eta = null;
		Long ttg = null;
		if (activeRoute != null) {
			activeRoute.reCalcRemainingWpEta();
			eta = activeRoute.getWpEta(wpIndex);
		} else {
			eta = route.getWpEta(wpIndex);
		}
		
		if (eta != null) {
			ttg = eta.getTime() - GnssTime.getInstance().getDate().getTime();
			if (ttg < 0) {
				ttg = null;
			}
		}
		
		Double dtg = null;
		if (activeRoute == null) {
			dtg = route.getWpRngSum(wpIndex);
		} else {
			if (activeRoute.getActiveWaypointIndex() <= wpIndex) {
				dtg = activeRoute.getActiveWpRng();
				if (dtg != null) {
					for (int i = activeRoute.getActiveWaypointIndex(); i < wpIndex; i++) {
						dtg += activeRoute.getWpRng(i);
					}
				}
			}
		}
		
		
		StringBuilder str = new StringBuilder();
		str.append("<html>");
		str.append(wp.getName() + "<br/>");
		str.append(Formatter.latToPrintable(wp.getPos().getLatitude()) + " - " + Formatter.lonToPrintable(wp.getPos().getLongitude()) + "<br/>");
		str.append("<table border='0' cellpadding='2'>");
		if (ttg != null) {
			str.append("<tr><td>TTG:</td><td>" + Formatter.formatTime(ttg) + "</td></tr>");
		}
		if (dtg != null) {
			str.append("<tr><td>DTG:</td><td>" + Formatter.formatDistNM(dtg, 2) + "</td></tr>");
		}
		str.append("<tr><td>ETA:</td><td>" + Formatter.formatShortDateTime(eta) + "</td></tr>");
		
		if (wp.getOutLeg() != null) {
			str.append("<tr><td>SPD:</td><td>" + Formatter.formatSpeed(wp.getOutLeg().getSpeed()) + "</td></tr>");
		}
		str.append("</table>");
		str.append("</html>");
		showText(str.toString());
	}
	
}
