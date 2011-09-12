package dk.frv.enav.ins.layers.ais;

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
		str.append("RNG " + Formatter.formatDistNM(routeData.getRange(wpCircle.getIndex())) + "<br/>");
		str.append("ETA " + Formatter.formatShortDateTime(routeData.getEta(wpCircle.getIndex())) + "<br/>");
		str.append("AVG SPD " + Formatter.formatSpeed(routeData.getSpeed()));
		str.append("</html>");

		showText(str.toString());

	}

	public void showLegInfo(IntendedRouteLegGraphic legGraphic) {
		int legIndex = legGraphic.getIndex();
		if (legIndex == 0) {
			return;
		}
		AisIntendedRoute routeData = legGraphic.getIntendedRouteGraphic().getVesselTarget().getAisRouteData();
		GeoLocation startPos = routeData.getWaypoints().get(legIndex - 1);
		GeoLocation endPos = routeData.getWaypoints().get(legIndex);
		double range = Calculator.range(startPos,endPos, Heading.RL);
		double hdg = Calculator.bearing(startPos, endPos, Heading.RL);
		Date startEta = routeData.getEta(legIndex - 1);
		Date endEta = routeData.getEta(legIndex);
		
		StringBuilder str = new StringBuilder();
		str.append("<html>");
		str.append("<b>Intended route leg</b><br/>");
		str.append(legGraphic.getIntendedRouteGraphic().getName() + "<br/>");
		str.append("DST " + Formatter.formatDistNM(range) + " HDG " + Formatter.formatDegrees(hdg, 0) + "<br/>");
		str.append("START " + Formatter.formatShortDateTime(startEta) + "<br/>");
		str.append("END " + Formatter.formatShortDateTime(endEta) + "<br/>");
		str.append("AVG SPD " + Formatter.formatSpeed(routeData.getSpeed()));
		str.append("</html>");

		showText(str.toString());
	}

}
