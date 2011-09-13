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
package dk.frv.enav.ins.services.shore;

import java.util.Date;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.metoc.MetocDataTypes;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequest;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequestWp;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteMetocSettings;
import dk.frv.enav.ins.route.RouteWaypoint;

public class Metoc {
	
	private static final long MAX_FORECAST_FUTURE = 60;

	public static MetocForecastRequest generateMetocRequest(Route route, GeoLocation pos)
			throws ShoreServiceException {
		MetocForecastRequest req = new MetocForecastRequest();
		RouteMetocSettings settings = route.getRouteMetocSettings();
		req.setDt(settings.getInterval());
		
		// Set all datatypes (could also just be settings.getDataTypes())
		MetocDataTypes.allTypes();
		for (MetocDataTypes dataType : MetocDataTypes.allTypes()) {
			req.getDataTypes().add(dataType);
		}

		// Special handling for active waypoint. Add one special wp and offset
		// all etas
		// Start at active waypoint
		int startWpIndex = 0;
		if (route instanceof ActiveRoute) {
			ActiveRoute activeRoute = (ActiveRoute) route;

			// Recalculate all remaining ETA's
			if (!activeRoute.reCalcRemainingWpEta()) {
				throw new ShoreServiceException(ShoreServiceErrorCode.NO_VALID_GPS_DATA);
			}
			
			startWpIndex = activeRoute.getActiveWaypointIndex();

			// Insert current location
			MetocForecastRequestWp reqWp = new MetocForecastRequestWp();
			reqWp.setEta(GnssTime.getInstance().getDate());
			reqWp.setHeading(activeRoute.getCurrentLeg().getHeading().name());
			reqWp.setLat(pos.getLatitude());
			reqWp.setLon(pos.getLongitude());

			req.getWaypoints().add(reqWp);
			//System.out.println("First     wp: " + reqWp);


		} else {
			route.adjustStartTime();
		}
		
		Date now = GnssTime.getInstance().getDate();

		for (int i = startWpIndex; i < route.getWaypoints().size(); i++) {
			Date eta = route.getWpEta(i);
			
			// Stop if ETA is too far in the future
			double inFutureHours = (eta.getTime() - now.getTime()) / 1000.0 / 3600.0;
			if (inFutureHours > MAX_FORECAST_FUTURE) {
				break;
			}
			
			// If not last waypoint and eta in past, leave out if next also in the past.
			if (i < route.getWaypoints().size() - 1) {
				Date nextEta = route.getWpEta(i + 1);
				if (eta.before(now) && nextEta.before(now)) {
					continue;
				}
			}
						
			RouteWaypoint wp = route.getWaypoints().get(i);
			MetocForecastRequestWp reqWp = new MetocForecastRequestWp();
			reqWp.setEta(eta);
			if (wp.getOutLeg() != null) {
				reqWp.setHeading(wp.getOutLeg().getHeading().name());
			}
			reqWp.setLat(wp.getPos().getLatitude());
			reqWp.setLon(wp.getPos().getLongitude());

			req.getWaypoints().add(reqWp);			
		}
		

		return req;
	}

}
