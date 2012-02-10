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
package dk.frv.enav.ins.services.ais;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.ais.message.AisMessage6;
import dk.frv.ais.message.AisMessage8;
import dk.frv.ais.message.AisPosition;
import dk.frv.ais.message.binary.AddressedRouteInformation;
import dk.frv.ais.message.binary.AsmAcknowledge;
import dk.frv.ais.message.binary.BroadcastIntendedRoute;
import dk.frv.ais.message.binary.RouteSuggestionReply;
import dk.frv.ais.reader.SendRequest;
import dk.frv.enav.ins.ais.AisAdressedRouteSuggestion;
import dk.frv.enav.ins.ais.VesselAisHandler;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.nmea.NmeaSensor;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.settings.AisSettings;
import dk.frv.enav.ins.settings.Settings;

/**
 * AIS service component providing an AIS link interface.
 */
public class AisServices extends MapHandlerChild {
	
	private static final Logger LOG = Logger.getLogger(AisServices.class);
	
	private static final long INTENDED_ROUTE_BROADCAST_INTERVAL = 6 * 60 * 1000; // 6 min
	
	private Integer sequence = 0;
	private NmeaSensor nmeaSensor;
	private Settings settings;
	private VesselAisHandler vesselAisHandler;
	
	private Date lastIntendedRouteBroadcast = new Date(0);
	
	public AisServices() {
		
	}
	
	/**
	 * Acknowledge the reception of a route suggestion
	 * @param routeInformation
	 */
	public void acknowledgeRouteSuggestion(AisMessage6 receivedMsg6, AddressedRouteInformation routeInformation) {
		LOG.debug("In acknowledgeRouteSuggestion()");
		if (!allowSend()) {
			return;
		}
		// Create acknowledge message
		AsmAcknowledge acknowledge = new AsmAcknowledge();
		acknowledge.setReceivedFi(routeInformation.getFi());
		acknowledge.setReceivedDac(routeInformation.getDac());
		acknowledge.setAiAvailable(1);
		acknowledge.setAiResponse(1);
		acknowledge.setTextSequenceNum(routeInformation.getMsgLinkId());
		
		// Create AIS msg 6
		AisMessage6 msg6 = new AisMessage6();
		msg6.setDestination(receivedMsg6.getUserId());
		msg6.setAppMessage(acknowledge);
		msg6.setRetransmit(0);
		
		// Create a send request
		SendRequest sendRequest = new SendRequest(msg6, nextSeq(), (int)receivedMsg6.getUserId());
		
		// Create a send thread
		AisSendThread aisSendThread = new AisSendThread(sendRequest, this);
		
		// Start send thread
		aisSendThread.start();
	}
	
	/**
	 * Reply on a route suggestion
	 * @param routeSuggestion
	 */
	public void routeSuggestionReply(AisAdressedRouteSuggestion routeSuggestion) {
		if (!allowSend()) {
			return;
		}
		// Create reply message
		RouteSuggestionReply routeSuggestionReply = new RouteSuggestionReply();
		routeSuggestionReply.setRefMsgLinkId(routeSuggestion.getMsgLinkId());
		switch (routeSuggestion.getStatus()) {
		case ACCEPTED:
			routeSuggestionReply.setResponse(0);
			break;
		case NOTED:
			routeSuggestionReply.setResponse(2);
			break;
		default:
			routeSuggestionReply.setResponse(1);
			break;
		}
		// Create AIS msg 6
		AisMessage6 msg6 = new AisMessage6();
		msg6.setDestination(routeSuggestion.getSender());
		msg6.setAppMessage(routeSuggestionReply);
		msg6.setRetransmit(0);
		
		// Create a send request
		SendRequest sendRequest = new SendRequest(msg6, nextSeq(), (int)routeSuggestion.getSender());
		
		// Create a send thread
		AisSendThread aisSendThread = new AisSendThread(sendRequest, this);
		
		// Start send thread
		aisSendThread.start();
	}
	
	public void intendedRouteBroadcast(ActiveRoute activeRoute) {
		LOG.debug("In intendedRouteBroadcast()");
		if (!doBroadcastIntented()) {
			return;
		}
		
		// Create intended route ASM
		BroadcastIntendedRoute intendedRoute;
		if (activeRoute == null) {
			intendedRoute = noIntendedRoute();
		} else {
			intendedRoute = intendedRouteFromActiveRoute(activeRoute, settings.getAisSettings());
		}
		
		// Create AIS message 8
		AisMessage8 msg8 = new AisMessage8();
		msg8.setAppMessage(intendedRoute);
		
		// Create a send request
		SendRequest sendRequest = new SendRequest(msg8, nextSeq());
		
		// Create a send thread
		AisIntendedRouteSendThread aisSendThread = new AisIntendedRouteSendThread(sendRequest, this);
		
		// Start send thread
		aisSendThread.start();
	}
	
	private static BroadcastIntendedRoute intendedRouteFromActiveRoute(ActiveRoute activeRoute, AisSettings aisSettings) {
		BroadcastIntendedRoute intendedRoute = new BroadcastIntendedRoute();
		
		// Recalculate all remaining ETA's
		if (!activeRoute.reCalcRemainingWpEta()) {
			// No valid ETA
			return noIntendedRoute();
		}		 
		
		int maxWps = aisSettings.getIntendedRouteMaxWps();
		if (maxWps == 0) {
			maxWps = 8;
		}
		long maxTimeLen = aisSettings.getIntendedRouteMaxTime() * 60 * 1000;
		if (maxTimeLen == 0) {
			maxTimeLen = Long.MAX_VALUE;
		}
		
		// Get first and last wp
		int startWp = activeRoute.getActiveWaypointIndex();
		// Find last wp if no time limit
		int lastWp = startWp;
		int maxWp = startWp + maxWps - 1;
		while (lastWp < maxWp && lastWp < activeRoute.getWaypoints().size() - 1) {
			long timeLen = activeRoute.getWpEta(lastWp).getTime() - activeRoute.getWpEta(startWp).getTime(); 
			if (lastWp > startWp + 1 && timeLen >= maxTimeLen) {
				lastWp--;
				break;
			}
			lastWp++;
		}
				
		// Find start and duration
		Date start = activeRoute.getWpEta(startWp);
		Date end = activeRoute.getWpEta(lastWp);
		int duration = (int)(end.getTime() - start.getTime()) / 1000 / 60;
		intendedRoute.setDuration(duration);
		
		// Set start time
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		cal.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
		intendedRoute.setStartMonth(cal.get(Calendar.MONTH) + 1);
		intendedRoute.setStartDay(cal.get(Calendar.DAY_OF_MONTH));
		intendedRoute.setStartHour(cal.get(Calendar.HOUR_OF_DAY));
		intendedRoute.setStartMin(cal.get(Calendar.MINUTE));
		
		// Add waypoints
		for (int i = startWp; i <= lastWp; i++) {
			intendedRoute.addWaypoint(new AisPosition(activeRoute.getWaypoints().get(i).getPos()));
		}
		
		return intendedRoute;
	}
	
	private static BroadcastIntendedRoute noIntendedRoute() {
		BroadcastIntendedRoute intendedRoute = new BroadcastIntendedRoute();
		
		// Use start as now
		Calendar cal = Calendar.getInstance();
		cal.setTime(GnssTime.getInstance().getDate());
		cal.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
		
		intendedRoute.setStartMonth(cal.get(Calendar.MONTH) + 1);
		intendedRoute.setStartDay(cal.get(Calendar.DAY_OF_MONTH));
		intendedRoute.setStartHour(cal.get(Calendar.HOUR_OF_DAY));
		intendedRoute.setStartMin(cal.get(Calendar.MINUTE));
		
		// Set no duration and 
		intendedRoute.setDuration(0);
		intendedRoute.setWaypointCount(0);		
		
		return intendedRoute;
	}
	
	private boolean doBroadcastIntented() {
		return allowSend() && settings.getAisSettings().isBroadcastIntendedRoute();
	}
	
	private boolean allowSend() {
		return settings.getAisSettings().isAllowSending();
	}

	public void setLastIntendedRouteBroadcast() {
		synchronized (lastIntendedRouteBroadcast) {
			lastIntendedRouteBroadcast = GnssTime.getInstance().getDate();
		}
	}
	
	public long getLastIntendedRouteBroadcast() {
		synchronized (lastIntendedRouteBroadcast) {
			return lastIntendedRouteBroadcast.getTime();
		}
	}
	
	public void periodicIntendedRouteBroadcast(ActiveRoute activeRoute) {
		if (!doBroadcastIntented()) {
			return;
		}
		long elapsed = (GnssTime.getInstance().getDate().getTime() - getLastIntendedRouteBroadcast());
		if (elapsed >= INTENDED_ROUTE_BROADCAST_INTERVAL) {
			intendedRouteBroadcast(activeRoute);
		}		
	}
	
	public NmeaSensor getNmeaSensor() {
		return nmeaSensor;
	}
	
	private int nextSeq() {
		synchronized (sequence) {
			int seq = sequence;
			sequence = (sequence + 1) % 4;
			return seq;
		}
	}
	
	public void sendResult(boolean sendOk) {
		if (vesselAisHandler == null) return;
		if (sendOk) {
			vesselAisHandler.getAisStatus().markSuccesfullSend();
		} else {
			vesselAisHandler.getAisStatus().markFailedSend();
		}
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (nmeaSensor == null && obj instanceof NmeaSensor) {
			nmeaSensor = (NmeaSensor)obj;
		}
		else if (settings == null && obj instanceof Settings) {
			settings = (Settings)obj;
		}
		else if (vesselAisHandler == null && obj instanceof VesselAisHandler) {
			vesselAisHandler = (VesselAisHandler)obj;
		}
	}

}
