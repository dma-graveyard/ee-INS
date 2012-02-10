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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.PositionReport;
import dk.frv.enav.common.xml.ShoreServiceRequest;
import dk.frv.enav.common.xml.ShoreServiceResponse;
import dk.frv.enav.common.xml.Waypoint;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequest;
import dk.frv.enav.common.xml.metoc.response.MetocForecastResponse;
import dk.frv.enav.common.xml.msi.request.MsiPollRequest;
import dk.frv.enav.common.xml.msi.response.MsiResponse;
import dk.frv.enav.common.xml.nogo.request.NogoRequest;
import dk.frv.enav.common.xml.nogo.response.NogoResponse;
import dk.frv.enav.common.xml.risk.request.RiskRequest;
import dk.frv.enav.common.xml.risk.response.RiskList;
import dk.frv.enav.common.xml.risk.response.RiskResponse;
import dk.frv.enav.ins.ais.VesselAisHandler;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.Route;
import dk.frv.enav.ins.route.RouteWaypoint;
import dk.frv.enav.ins.settings.EnavSettings;
import dk.frv.enav.ins.status.ComponentStatus;
import dk.frv.enav.ins.status.IStatusComponent;
import dk.frv.enav.ins.status.ShoreServiceStatus;

/**
 * Shore service component providing the functional link to shore.
 */
public class ShoreServices extends MapHandlerChild implements IStatusComponent {
	
	private static final Logger LOG = Logger.getLogger(ShoreServices.class);

	private VesselAisHandler vesselAisHandler;
	private GpsHandler gpsHandler;
	private EnavSettings enavSettings;
	private ShoreServiceStatus status = new ShoreServiceStatus();
	
	public ShoreServices(EnavSettings enavSettings) {
		this.enavSettings = enavSettings; 
	}

	public static double floatToDouble (float converThisNumberToFloat) {

		String floatNumberInString = String.valueOf(converThisNumberToFloat);
		double floatNumberInDouble = Double.parseDouble(floatNumberInString);
		return floatNumberInDouble;

		}	
	
	public static dk.frv.enav.common.xml.Route convertRoute(Route route){
		//TO DO
		
		dk.frv.enav.common.xml.Route xmlRoute = new dk.frv.enav.common.xml.Route();
		LinkedList<RouteWaypoint> waypoint = route.getWaypoints();
		List<Waypoint> waypoints = new ArrayList<Waypoint>();;
		int i = 0;
		
	    for (RouteWaypoint waypointEeins : waypoint)
	    {
			Waypoint waypointEnavshore = new Waypoint();
			
			//ETA
			waypointEnavshore.setEta(route.getWpEta(i));
			
			//Heading
			//	Heading headingEeins = waypointEeins.getHeading();
			dk.frv.enav.common.xml.Waypoint.Heading headingEnavshore = null;
			waypointEnavshore.setHeading(headingEnavshore);
				
			//Latitude
			waypointEnavshore.setLat(waypointEeins.getPos().getLatitude());

			//Longitude
			waypointEnavshore.setLon(waypointEeins.getPos().getLongitude());
			
			//Rate of turn
			waypointEnavshore.setRot(waypointEeins.getRot());

			//Speed
			//waypointEnavshore.setSpeed(waypointEeins.getOutLeg().getSpeed());
			
			//Turn radius
			waypointEnavshore.setTurnRad(waypointEeins.getTurnRad());		
			
			//Port XTD
			//waypointEnavshore.setXtdPort(waypointEeins.getOutLeg().getXtdPort());
			
			//Starboard XTD
			//waypointEnavshore.setXtdStarboard(waypointEeins.getOutLeg().getXtdStarboard());
			
			waypoints.add(waypointEnavshore);
	
			i++;
	    
	    }		
	    xmlRoute.setWaypoints(waypoints);
	    xmlRoute.setActiveWaypoint(0);
	    
		return xmlRoute;
	}
	
	public static PositionReport convertPositionReport(VesselPositionData position){
		PositionReport enavshorePos = new PositionReport();
		
		enavshorePos.setCog(floatToDouble(position.getCog()));
		enavshorePos.setHeading(floatToDouble(position.getTrueHeading()));
		enavshorePos.setLatitude(position.getPos().getLatitude());
		enavshorePos.setLongitude(position.getPos().getLongitude());
		enavshorePos.setRot(floatToDouble(position.getRot()));
		enavshorePos.setSog(floatToDouble(position.getSog()));
		return enavshorePos;
	}
	
	public NogoResponse nogoPoll(double draught, GeoLocation northWestPoint, GeoLocation southEastPoint, Date startDate, Date endDate) throws ShoreServiceException {
		// Create request
		NogoRequest nogoRequest = new NogoRequest();

		// Set request parameters
		nogoRequest.setDraught(draught);
		nogoRequest.setNorthWestPointLat(northWestPoint.getLatitude());
		nogoRequest.setNorthWestPointLon(northWestPoint.getLongitude());
		nogoRequest.setSouthEastPointLat(southEastPoint.getLatitude());
		nogoRequest.setSouthEastPointLon(southEastPoint.getLongitude());
		nogoRequest.setStartDate(startDate);
		nogoRequest.setEndDate(endDate);
		
		// Add request parameters
		addRequestParameters(nogoRequest);
		
		NogoResponse nogoResponse = (NogoResponse)makeRequest("/api/xml/nogo", "dk.frv.enav.common.xml.nogo.request", "dk.frv.enav.common.xml.nogo.response", nogoRequest);
		return nogoResponse;
	}
	
	
	public MsiResponse msiPoll(int lastMessage) throws ShoreServiceException {
		// Create request
		MsiPollRequest msiPollRequest = new MsiPollRequest();
		msiPollRequest.setLastMessage(lastMessage);
		
		// Add request parameters
		addRequestParameters(msiPollRequest);
		
		MsiResponse msiResponse = (MsiResponse)makeRequest("/api/xml/msi", "dk.frv.enav.common.xml.msi.request", "dk.frv.enav.common.xml.msi.response", msiPollRequest); 
		
		return msiResponse;
	}
	
	public List<RiskList> getRiskIndexes(double southWestLat, double northEastLat, double southWestLon, double northEastLon) throws ShoreServiceException {
		// Create request
		RiskRequest req= new RiskRequest();
		req.setLatMin(southWestLat);
		req.setLonMin(southWestLon);
		req.setLatMax(northEastLat);
		req.setLonMax(northEastLon);
		//req.setMmsiList(list);
		// Add request parameters
		addRequestParameters(req);
		
		RiskResponse resp = (RiskResponse) makeRequest("/api/xml/risk", "dk.frv.enav.common.xml.risk.request", "dk.frv.enav.common.xml.risk.response", req); 
		
		return resp.getList();
	}
		
	public MetocForecast routeMetoc(Route route) throws ShoreServiceException {
		// Get current position if active route
		GeoLocation pos = null;
		if (route instanceof ActiveRoute) {
			GpsData gpsData = gpsHandler.getCurrentData();
			if (gpsData.isBadPosition()) {
				throw new ShoreServiceException(ShoreServiceErrorCode.NO_VALID_GPS_DATA);
			}
			pos = gpsData.getPosition();
		}
		// Create request
		MetocForecastRequest request = Metoc.generateMetocRequest(route, pos);
		
		// Add request parameters
		addRequestParameters(request);
		
		// Make request
		MetocForecastResponse res = (MetocForecastResponse)makeRequest("/api/xml/routeMetoc", "dk.frv.enav.common.xml.metoc.request", "dk.frv.enav.common.xml.metoc.response", request);
		
		return res.getMetocForecast();
	}
	
	private void addRequestParameters(ShoreServiceRequest request) throws ShoreServiceException {		
		if (vesselAisHandler != null && vesselAisHandler.getOwnShip() != null) {
		    request.setMmsi(vesselAisHandler.getOwnShip().getMmsi());
		    if (vesselAisHandler.getOwnShip().getPositionData() != null)
		    request.setPositionReport(convertPositionReport(vesselAisHandler.getOwnShip().getPositionData()));
		}
	
	}
	
	private ShoreServiceResponse makeRequest(String uri, String reqContextPath, String resContextPath, Object request) throws ShoreServiceException {
		// Create HTTP request
		ShoreHttp shoreHttp = new ShoreHttp(uri, enavSettings);
		// Init HTTP
		shoreHttp.init();		
		// Set content
		try {
			shoreHttp.setXmlMarshalContent(reqContextPath, request);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Failed to make XML request: " + e.getMessage());
			throw new ShoreServiceException(ShoreServiceErrorCode.INTERNAL_ERROR);
		}
		
		// Make request
		try {
			shoreHttp.makeRequest();
		} catch (ShoreServiceException e) {
			status.markContactError(e);
			throw e;
		}
		
		ShoreServiceResponse res;
		try {
			Object resObj = shoreHttp.getXmlUnmarshalledContent(resContextPath);
			res = (ShoreServiceResponse)resObj;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Failed to unmarshal XML response: " + e.getMessage());
			throw new ShoreServiceException(ShoreServiceErrorCode.INVALID_RESPONSE);
		}
				
		// Set last fail/contact
		status.markContactSuccess();
		
		// Report if an error response  
		if (res.getErrorCode() != 0) {
			throw new ShoreServiceException(ShoreServiceErrorCode.SERVICE_ERROR, res.getErrorMessage());
		}
		
		return res;
	}
		
	@Override
	public void findAndInit(Object obj) {
		if (vesselAisHandler == null && obj instanceof VesselAisHandler) {
			vesselAisHandler = (VesselAisHandler)obj;
		}
		if (gpsHandler == null && obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler)obj;
		}
	}
	
	@Override
	public void findAndUndo(Object obj) {
		if (obj == vesselAisHandler) {
			vesselAisHandler = null;
		} else if (obj == gpsHandler) {
			gpsHandler = null;
		}		
	}
	
	@Override
	public ComponentStatus getStatus() {
		return status;
	}
	
}
