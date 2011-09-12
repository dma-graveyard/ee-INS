package dk.frv.enav.ins.layers.route;

import java.util.Date;

import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.gui.InfoPanel;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteWaypoint;

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
		if (ttg != null) {
			str.append("TTG: " + Formatter.formatTime(ttg) + "<br/>");
		}
		if (dtg != null) {
			str.append("DTG: " + Formatter.formatDistNM(dtg, 2) + "<br/>");
		}
		str.append("ETA: " + Formatter.formatShortDateTime(eta) + "<br/>");
		
		if (wp.getOutLeg() != null) {
			str.append("SPD: " + Formatter.formatSpeed(wp.getOutLeg().getSpeed()));
		}
		
		str.append("</html>");
		showText(str.toString());
	}
	
}
