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

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.ais.geo.GeoLocation;
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
		IStatusComponent {

	private static final Logger LOG = Logger
			.getLogger(MonaLisaRouteExchange.class);

	private AisHandler aisHandler;
	private GpsHandler gpsHandler;
	private ShoreServiceStatus status = new ShoreServiceStatus();

	private static final String ENCODING = "UTF-8";

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

			// waypoint.setInLeg(inLeg)
			// waypoint.setOutLeg(leg)

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

			if (routeWaypoints.size() > 1) {
				RouteLeg newLeg = new RouteLeg();
				newLeg.setHeading(Heading.RL);
				RouteWaypoint prevWaypoint = routeWaypoints.get(routeWaypoints
						.size() - 2);
				prevWaypoint.setOutLeg(newLeg);
				waypoint.setInLeg(newLeg);
				newLeg.setStartWp(prevWaypoint);
				newLeg.setEndWp(waypoint);
			}

		}

		return route;
	}

	// public Route makeRequest(Route route) throws ShoreServiceException {
	public Route makeRequest(Route route) throws Exception {
		System.out.println("Recieved route for Mona Lisa Exchange");
		// A request for a route has come in

		// Convert the route to MonaLisa Format
		RouteRequest monaLisaRoute = convertRoute(route);

		JAXBContext context = null;
		String xmlReturnRoute = "";

		String xml = "";

		try {
			context = JAXBContext.newInstance(RouteRequest.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_ENCODING, ENCODING);

			// Convert the generated xml route to a String
			StringWriter st = new StringWriter();
			m.marshal(monaLisaRoute, st);
			xml = st.toString();

			// xml = xml.replace("routerequest", "RouteRequest");

			System.out.println(xml);

			// STATIC ROUTE INPUT START
			// FileInputStream stream = null;
			// try {
			// // stream = new FileInputStream(new File("C:\\route02.xml"));
			// } catch (FileNotFoundException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			// try {
			// FileChannel fc = stream.getChannel();
			// MappedByteBuffer bb = null;
			// try {
			// bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			// } catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			/* Instead of using default, pass in a decoder. */
			// staticXML = Charset.defaultCharset().decode(bb).toString();
			// } finally {
			// try {
			// // stream.close();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			// STATIC ROUTE INPUT STOP

			// Create HTTP request
			RouteHttp routeHttp = new RouteHttp();
			// Init HTTP
			routeHttp.init();
			// Set content
			routeHttp.setRequestBody(xml);
			// Make request
			try {
				routeHttp.makeRequest();
				xmlReturnRoute = routeHttp.getResponseBody();
			} catch (Exception e) {
				// status.markContactError(e);
				// throw e;
				System.out.println(e.getMessage());
			}

			// Do we want to save the generated route to a file?

			// try {
			// m.marshal(
			// monaLisaRoute,
			// new FileOutputStream(
			// "C:\\Dropbox\\Mona Lisa Route XML\\Example\\generatedRequest.xml"));
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Unmarshall the recieved route and parse it

		// xmlReturnRoute = xmlReturnRoute.replace("RouteResponse",
		// "routeresponseType");

		System.out.println(xmlReturnRoute);

		Unmarshaller u;
		JAXBContext jc;
		RouteResponse routeResponse = null;

		xmlReturnRoute = xmlReturnRoute.replace("RouteResponse", "routeresponseType");
		
		StringReader sr = new StringReader(xmlReturnRoute);
		try {
			jc = JAXBContext
					.newInstance("dk.frv.enav.ins.route.monalisa.se.sspa.optiroute");
			u = jc.createUnmarshaller();
			routeResponse = (RouteResponse) u.unmarshal(sr);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Route newRoute = null;

		if (routeResponse != null) {
			System.out.println("Route Recieved");

			// Convert the route to one we can paint
			newRoute = convertRoute(routeResponse);
		}
		return newRoute;
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

	private static String monaLisaResExample = "<?xml version=\"1.0\" ?> \n"
			+ "<ns1:routeresponseType xmlns:ns1=\"http://www.sspa.se/optiroute\" xmlns:ns2=\"http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange\"> \n"
			+ "    <ns1:FuelRequested>1.25023531914</ns1:FuelRequested> \n"
			+ "    <ns1:FuelFinal>1.25023531914</ns1:FuelFinal> \n"
			+ "    <ns1:Route> \n"
			+ "        <ns2:waypoints> \n"
			+ "            <ns2:waypoint> \n"
			+ "                <ns2:wpt-id>1</ns2:wpt-id> \n"
			+ "                <ns2:ETA>2012-04-18T08:00:00Z</ns2:ETA> \n"
			+ "                <ns2:wpt-name>Trubaduren</ns2:wpt-name> \n"
			+ "                <ns2:position> \n"
			+ "                    <ns2:latitude>57.5947494507</ns2:latitude> \n"
			+ "                    <ns2:longitude>11.6329803467</ns2:longitude> \n"
			+ "                </ns2:position> \n"
			+ "                <ns2:leg-info> \n"
			+ "                    <ns2:planned-speed>15.745013237</ns2:planned-speed> \n"
			+ "                </ns2:leg-info> \n"
			+ "            </ns2:waypoint> \n"
			+ "            <ns2:waypoint> \n"
			+ "                <ns2:wpt-id>2</ns2:wpt-id> \n"
			+ "                <ns2:ETA>2012-04-18T08:15:47.362122</ns2:ETA> \n"
			+ "                <ns2:wpt-name>x1</ns2:wpt-name> \n"
			+ "                <ns2:position> \n"
			+ "                    <ns2:latitude>57.6495399475</ns2:latitude> \n"
			+ "                    <ns2:longitude>11.7113399506</ns2:longitude> \n"
			+ "                </ns2:position> \n"
			+ "                <ns2:leg-info> \n"
			+ "                    <ns2:planned-speed>15.9477205276</ns2:planned-speed> \n"
			+ "                </ns2:leg-info> \n"
			+ "            </ns2:waypoint> \n"
			+ "            <ns2:waypoint> \n"
			+ "                <ns2:wpt-id>3</ns2:wpt-id> \n"
			+ "                <ns2:ETA>2012-04-18T08:23:38.74704</ns2:ETA> \n"
			+ "                <ns2:wpt-name>x2</ns2:wpt-name> \n"
			+ "                <ns2:position> \n"
			+ "                    <ns2:latitude>57.6597290039</ns2:latitude> \n"
			+ "                    <ns2:longitude>11.7734003067</ns2:longitude> \n"
			+ "                </ns2:position> \n"
			+ "                <ns2:leg-info> \n"
			+ "                    <ns2:planned-speed>15.7729797363</ns2:planned-speed> \n"
			+ "                </ns2:leg-info> \n"
			+ "            </ns2:waypoint> \n"
			+ "            <ns2:waypoint> \n"
			+ "                <ns2:wpt-id>4</ns2:wpt-id> \n"
			+ "                <ns2:ETA>2012-04-18T08:30:22.65152</ns2:ETA> \n"
			+ "                <ns2:wpt-name>x3</ns2:wpt-name> \n"
			+ "                <ns2:position> \n"
			+ "                    <ns2:latitude>57.6791992188</ns2:latitude> \n"
			+ "                    <ns2:longitude>11.8147697449</ns2:longitude> \n"
			+ "                </ns2:position> \n"
			+ "                <ns2:leg-info> \n"
			+ "                    <ns2:planned-speed>14.8269224167</ns2:planned-speed> \n"
			+ "                </ns2:leg-info> \n"
			+ "            </ns2:waypoint> \n"
			+ "            <ns2:waypoint> \n"
			+ "                <ns2:wpt-id>5</ns2:wpt-id> \n"
			+ "                <ns2:ETA>2012-04-18T08:41:59.999909</ns2:ETA> \n"
			+ "                <ns2:wpt-name>Alvsborgsbron</ns2:wpt-name> \n"
			+ "                <ns2:position> \n"
			+ "                    <ns2:latitude>57.6908988953</ns2:latitude> \n"
			+ "                    <ns2:longitude>11.9015398026</ns2:longitude> \n"
			+ "                </ns2:position> \n"
			+ "            </ns2:waypoint> \n" + "        </ns2:waypoints> \n"
			+ "    </ns1:Route> \n" + "</ns1:routeresponseType> \n";

}
