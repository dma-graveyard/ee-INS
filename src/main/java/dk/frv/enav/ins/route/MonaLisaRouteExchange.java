package dk.frv.enav.ins.route;

/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
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
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.route.monalisa.fi.navielektro.ns.formats.vessel_waypoint_exchange.LeginfoType;
import dk.frv.enav.ins.route.monalisa.fi.navielektro.ns.formats.vessel_waypoint_exchange.PositionType;
import dk.frv.enav.ins.route.monalisa.fi.navielektro.ns.formats.vessel_waypoint_exchange.RouteType;
import dk.frv.enav.ins.route.monalisa.fi.navielektro.ns.formats.vessel_waypoint_exchange.WaypointType;
import dk.frv.enav.ins.route.monalisa.fi.navielektro.ns.formats.vessel_waypoint_exchange.WaypointsType;
import dk.frv.enav.ins.route.monalisa.se.sspa.optiroute.CurrentShipDataType;
import dk.frv.enav.ins.route.monalisa.se.sspa.optiroute.DepthPointsType;
import dk.frv.enav.ins.route.monalisa.se.sspa.optiroute.RouteRequest;
import dk.frv.enav.ins.route.monalisa.se.sspa.optiroute.RouteResponse;
import dk.frv.enav.ins.route.monalisa.se.sspa.optiroute.WeatherPointsType;
import dk.frv.enav.ins.services.shore.RouteHttp;
import dk.frv.enav.ins.status.ComponentStatus;
import dk.frv.enav.ins.status.IStatusComponent;
import dk.frv.enav.ins.status.ShoreServiceStatus;

//import dk.frv.enav.ins.route.monalisa.se.sspa.optiroute.Routerequest;

/**
 * Shore service component providing the functional link to shore.
 */
public class MonaLisaRouteExchange extends MapHandlerChild implements
		IStatusComponent, Runnable {

	// private static final Logger LOG = Logger
	// .getLogger(MonaLisaRouteExchange.class);

	private AisHandler aisHandler;
	private GpsHandler gpsHandler;
	private ShoreServiceStatus status = new ShoreServiceStatus();

	private static final String ENCODING = "UTF-8";

	private Route route;

	public void setRoute(Route route) {
		this.route = route;
	}

	public MonaLisaRouteExchange() {

	}

	public RouteRequest convertRoute(Route route) {

		float trim = 6.0f;

		// Create the route request
		RouteRequest monaLisaRoute = new RouteRequest();

		// Create the ship data
		CurrentShipDataType currentShipData = new CurrentShipDataType();

		if (aisHandler.getOwnShip().getStaticData() != null) {
			trim = aisHandler.getOwnShip().getStaticData().getDraught();
		}

		currentShipData.setAfttrim(trim);
		currentShipData.setForwardtrim(trim);
		currentShipData.setImoid("1234567");
		currentShipData.setMmsi("123456789");

		monaLisaRoute.setCurrentShipData(currentShipData);

		// Create the depthPoints
		DepthPointsType depthPoints = new DepthPointsType();
		monaLisaRoute.setDepthPoints(depthPoints);

		// Create the weather points
		WeatherPointsType weatherPoints = new WeatherPointsType();
		monaLisaRoute.setWeatherPoints(weatherPoints);

		RouteType monaLisaRouteType = new RouteType();

		WaypointsType waypoints = new WaypointsType();

		// Convert the existing waypoints into the Mona Lisa Format
		List<WaypointType> monaLisaWaypoints = waypoints.getWaypoint();
		LinkedList<RouteWaypoint> eeinsWaypoints = route.getWaypoints();

		for (int i = 0; i < eeinsWaypoints.size(); i++) {
			RouteWaypoint routeWaypoint = eeinsWaypoints.get(i);
			WaypointType waypoint = new WaypointType();

			// Set date
			GregorianCalendar c = new GregorianCalendar();
			Date today = new Date();
			c.setTime(today);
			XMLGregorianCalendar date2 = null;
			try {
				date2 = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(c);
			} catch (DatatypeConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			c.add(Calendar.DAY_OF_YEAR, 1);
			XMLGregorianCalendar tomorrow2 = null;
			try {
				tomorrow2 = DatatypeFactory.newInstance()
						.newXMLGregorianCalendar(c);
			} catch (DatatypeConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (i == 0) {
				waypoint.setETA(date2);
			}
			if (i == 1) {
				waypoint.setETA(tomorrow2);
			}

			// Set leg info
			LeginfoType legInfo = new LeginfoType();
			// legInfo.setLegtype(value)
			// legInfo.setLhsXte(value)
			// legInfo.setRhsXte(value)

			if (routeWaypoint.outLeg != null) {
				legInfo.setPlannedSpeed((float) routeWaypoint.outLeg.speed);
			} else {
				legInfo.setPlannedSpeed(0.0f);
			}

			// Rate of turn not needed
			// if (routeWaypoint.getRot() != null) {
			// legInfo.setTurnRadius(Double.valueOf(routeWaypoint.getRot())
			// .intValue());
			// } else {
			// legInfo.setTurnRadius(99);
			// }
			//
			// //Hardcoded to 99
			// legInfo.setTurnRadius(99);
			//
			//

			waypoint.setLegInfo(legInfo);

			// Set positon
			PositionType position = new PositionType();

			position.setLatitude(routeWaypoint.getPos().getLatitude());
			position.setLongitude(routeWaypoint.getPos().getLongitude());

			waypoint.setPosition(position);

			// Set ID
			waypoint.setWptId(i + 1);

			// Set name
			waypoint.setWptName(routeWaypoint.name);

			monaLisaWaypoints.add(waypoint);

		}

		monaLisaRouteType.setWaypoints(waypoints);
		monaLisaRoute.setRoute(monaLisaRouteType);

		return monaLisaRoute;
	}

	public Route convertRoute(RouteResponse response) {
		Route route = new Route();

		route.setName("Optimized Mona Lisa Route");

		WaypointsType waypointsType = response.getRoute().getWaypoints();
		List<WaypointType> responseWaypoints = waypointsType.getWaypoint();

		LinkedList<RouteWaypoint> routeWaypoints = route.getWaypoints();

		for (int i = 0; i < responseWaypoints.size(); i++) {

			RouteWaypoint waypoint = new RouteWaypoint();
			WaypointType responseWaypoint = responseWaypoints.get(i);

			waypoint.setName(responseWaypoint.getWptName());

			if (i != 0) {
				RouteLeg inLeg = new RouteLeg();
				inLeg.setHeading(Heading.RL);
				waypoint.setInLeg(inLeg);

				// RouteWaypoint prevWaypoint =
				// routeWaypoints.get(routeWaypoints
				// .size() - 2);
				// System.out.println("For waypoint" + i + " creating in leg");
			}

			// Outleg always has next
			if (i != responseWaypoints.size() - 1) {
				RouteLeg outLeg = new RouteLeg();
				outLeg.setHeading(Heading.RL);
				waypoint.setOutLeg(outLeg);
				// System.out.println("For waypoint" + i + " creating out leg");
			}

			// if (waypoint.getInLeg() != null) {
			// waypoint.getInLeg().setSpeed(5.0);
			// }

			// if (waypoint.getOutLeg() != null) {
			// System.out.println("SEtting stuff?");
			// waypoint.getOutLeg().setSpeed(5.0);
			// // System.out.println(waypoint.getOutLeg().getSpeed());
			// }

			GeoLocation position = new GeoLocation(responseWaypoint
					.getPosition().getLatitude(), responseWaypoint
					.getPosition().getLongitude());
			waypoint.setPos(position);

			if (responseWaypoint.getLegInfo() != null) {

				if (responseWaypoint.getLegInfo().getTurnRadius() != null) {
					waypoint.setRot((double) responseWaypoint.getLegInfo()
							.getTurnRadius());
				}

				if (responseWaypoint.getLegInfo().getPlannedSpeed() != null) {

					waypoint.setSpeed(responseWaypoint.getLegInfo()
							.getPlannedSpeed());

				}

				if (responseWaypoint.getLegInfo().getTurnRadius() != null) {
					waypoint.setTurnRad((double) responseWaypoint.getLegInfo()
							.getTurnRadius());
				}
			}

			routeWaypoints.add(waypoint);

		}

		if (routeWaypoints.size() > 1) {
			for (int i = 0; i < routeWaypoints.size(); i++) {

				// System.out.println("Looking at waypoint:" + i);
				RouteWaypoint waypoint = routeWaypoints.get(i);

				// Waypoint 0 has no in leg, one out leg... no previous
				if (i != 0) {
					RouteWaypoint prevWaypoint = routeWaypoints.get(i - 1);

					if (waypoint.getInLeg() != null) {
						// System.out.println("Setting inleg prev for waypoint:"
						// + i);
						waypoint.getInLeg().setStartWp(prevWaypoint);
						waypoint.getInLeg().setEndWp(waypoint);
					}

					if (prevWaypoint.getOutLeg() != null) {
						// System.out.println("Setting outleg prev for waypoint:"
						// + i);
						prevWaypoint.getOutLeg().setStartWp(prevWaypoint);
						prevWaypoint.getOutLeg().setEndWp(waypoint);

					}
				}

			}
		}

		return route;
	}

	public void makeRouteRequest(Route route) {

		this.route = route;

		new Thread(this).start();

		// return newRoute;

	}

	@Override
	public void findAndInit(Object obj) {
		if (aisHandler == null && obj instanceof AisHandler) {
			aisHandler = (AisHandler) obj;
		}
		if (gpsHandler == null && obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler) obj;
		}
	}

	@Override
	public void findAndUndo(Object obj) {
		if (obj == aisHandler) {
			aisHandler = null;
		} else if (obj == gpsHandler) {
			gpsHandler = null;
		}
	}

	@Override
	public ComponentStatus getStatus() {
		return status;
	}

	@Override
	public void run() {

		RouteRequest monaLisaRoute = convertRoute(route);

		RouteResponse routeResponse = EeINS.getShoreServices()
				.makeMonaLisaRouteRequest(monaLisaRoute);

		Route newRoute = null;

		if (routeResponse != null) {
			newRoute = convertRoute(routeResponse);
		}

		if (newRoute != null) {

			EeINS.getRouteManager().addRoute(newRoute);

		}
	}

}
