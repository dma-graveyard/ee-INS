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
package dk.frv.enav.ins.layers.ais;

import java.awt.geom.Point2D;
import java.util.Date;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.ais.AisIntendedRoute;
import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.common.util.Calculator;
import dk.frv.enav.ins.gui.InfoPanel;

public class IntendedRouteInfoPanel extends InfoPanel {
	private static final long serialVersionUID = 1L;

	public IntendedRouteInfoPanel() {
		super();
	}

	public void showWpInfo(IntendedRouteWpCircle wpCircle) {
		AisIntendedRoute routeData = wpCircle.getIntendedRouteGraphic().getVesselTarget().getAisRouteData();
		if (routeData == null) {
			showText("");
			return;
		}
		GeoLocation wp = wpCircle.getIntendedRouteGraphic().getVesselTarget().getAisRouteData().getWaypoints().get(wpCircle.getIndex());
		StringBuilder str = new StringBuilder();
		str.append("<html>");
		str.append("<b>Intended route waypoint</b><br/>");
		str.append(wpCircle.getIntendedRouteGraphic().getName() + "<br/>");
		str.append(Formatter.latToPrintable(wp.getLatitude()) + " - " + Formatter.lonToPrintable(wp.getLongitude()) + "<br/>");
		str.append("<table border='0' cellpadding='2'>");
		str.append("<tr><td>RNG:</td><td>" + Formatter.formatDistNM(routeData.getRange(wpCircle.getIndex())) + "</td></tr>");
		str.append("<tr><td>ETA:</td><td>" + Formatter.formatShortDateTime(routeData.getEta(wpCircle.getIndex())) + "</td></tr>");
		str.append("<tr><td>AVG SPD:</td><td>" + Formatter.formatSpeed(routeData.getSpeed()) + "</td></tr>");
		str.append("</table>");
		str.append("</html>");

		showText(str.toString());

	}

	public void showLegInfo(IntendedRouteLegGraphic legGraphic, Point2D worldLocation) {
		int legIndex = legGraphic.getIndex();
		if (legIndex == 0) {
			return;
		}
		AisIntendedRoute routeData = legGraphic.getIntendedRouteGraphic().getVesselTarget().getAisRouteData();
		GeoLocation startPos = routeData.getWaypoints().get(legIndex - 1);
		GeoLocation midPos = new GeoLocation(worldLocation.getY(), worldLocation.getX());
		GeoLocation endPos = routeData.getWaypoints().get(legIndex);
		double range = Calculator.range(startPos,endPos, Heading.RL);
		double midRange = Calculator.range(startPos, midPos, Heading.RL);
		double hdg = Calculator.bearing(startPos, endPos, Heading.RL);
		Date startEta = routeData.getEta(legIndex - 1);
		//TODO: google says 1 nautical miles / knots = 1 hour
		Date midEta = new Date((long)((midRange / routeData.getSpeed()) * 3600000 + startEta.getTime()));
		Date endEta = routeData.getEta(legIndex);
		
		StringBuilder str = new StringBuilder();
		str.append("<html>");
		str.append("<b>Intended route leg</b><br/>");
		str.append(legGraphic.getIntendedRouteGraphic().getName() + "<br/>");
		str.append("<table border='0' cellpadding='2'>");
		str.append("<tr><td>DST:</td><td>" + Formatter.formatDistNM(range) + " HDG " + Formatter.formatDegrees(hdg, 0) + "</td></tr>");
		str.append("<tr><td>START:</td><td>" + Formatter.formatShortDateTime(startEta) + "</td></tr>");
		str.append("<tr><td>ETA Mid:</td><td>" + Formatter.formatShortDateTime(midEta) + "</td></tr>");
		str.append("<tr><td>END:</td><td>" + Formatter.formatShortDateTime(endEta) + "");
		str.append("<tr><td>AVG SPD:</td><td>" + Formatter.formatSpeed(routeData.getSpeed()) + "</td></tr>");
		str.append("</table>");
		str.append("</html>");

		showText(str.toString());
	}

}
