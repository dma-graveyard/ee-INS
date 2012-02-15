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
package dk.frv.enav.ins.util.route;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import dk.frv.ais.binary.SixbitException;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage8;
import dk.frv.ais.message.AisPosition;
import dk.frv.ais.message.binary.AisApplicationMessage;
import dk.frv.ais.message.binary.BroadcastIntendedRoute;
import dk.frv.ais.proprietary.IProprietarySourceTag;
import dk.frv.ais.sentence.Vdm;

/**
 * Receive AIS messages and inject intended route broadcasts
 */
public class AisRouteInject implements IAisHandler {
	
	private List<TimePoint> route;
	private PrintWriter out;
	private int currentWpIndex;
	private long mmsi;
	private int routePoints = 15;
	private Date lastBroadcast = null;	
	
	public AisRouteInject(String outFilename, List<TimePoint> route, long mmsi) throws IOException {
		this.route = route;
		this.mmsi = mmsi;
		FileWriter outFile = new FileWriter(outFilename);
		out = new PrintWriter(outFile);
		currentWpIndex = 0;
	}

	@Override
	public void receive(AisMessage aisMessage) {
		IProprietarySourceTag tag = aisMessage.getSourceTag();
		if (tag != null) {
			out.println(tag.getSentence());
		}
		out.println(aisMessage.getVdm().getOrgLinesJoined());
		
		if (tag == null || tag.getTimestamp() == null) {
			return;
		}
		
		boolean passedWp = false;
		boolean minTimepassed = false;
		int wpToUse = currentWpIndex;
		
		// Determine if feed time has passed current WP time
		TimePoint currentWp = route.get(currentWpIndex);
		if (tag.getTimestamp().after(currentWp.getTime())) {
			passedWp = true;
			wpToUse++;
		} else {
			// Determine if minTime passed
			if (lastBroadcast != null) {
				long elapsed = tag.getTimestamp().getTime() - lastBroadcast.getTime();
				if (elapsed > 6 * 60 * 1000) {
					minTimepassed = true;
				}
			}
		}
		
		if (!minTimepassed && !passedWp) {
			return;
		}
		
		// List of waypoint to use
		List<TimePoint> aisRoute = new ArrayList<TimePoint>();
		for (int i=wpToUse; aisRoute.size() < routePoints && i < route.size(); i++) {
			aisRoute.add(route.get(i));
		}
		
		lastBroadcast = tag.getTimestamp();
		
		if (passedWp) {
			currentWpIndex++;
		}
		
		if (aisRoute.size() < 2) {
			return;
		}
		
		// Find duration in minutes
		Date start = aisRoute.get(0).getTime();
		Date end = aisRoute.get(aisRoute.size() - 1).getTime();
		long duration = end.getTime() - start.getTime();
		duration = (duration / 1000) / 60;
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		cal.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
		
		// Make application specific message
		BroadcastIntendedRoute intendedRoute = new BroadcastIntendedRoute();
		intendedRoute.setStartMonth(cal.get(Calendar.MONTH) + 1);
		intendedRoute.setStartDay(cal.get(Calendar.DAY_OF_MONTH));
		intendedRoute.setStartHour(cal.get(Calendar.HOUR_OF_DAY));
		intendedRoute.setStartMin(cal.get(Calendar.MINUTE));
		intendedRoute.setDuration((int)duration);
		for (TimePoint point : aisRoute) {
			GeoLocation wp = new GeoLocation(point.getLatitude(), point.getLongitude());
			if (wp.getLatitude() < 54 || wp.getLatitude() > 60 || wp.getLongitude() < 8 || wp.getLongitude() > 14) {
				System.out.println("ERROR: Wrong wp in AIS broadcast: " + wp + " TimePoint: " + point);
			}
			AisPosition aisPosition = new AisPosition(wp);
			intendedRoute.addWaypoint(aisPosition);
		}
		intendedRoute.setWaypointCount(intendedRoute.getWaypoints().size());
		intendedRoute.addWaypoint(new AisPosition(new GeoLocation(0, 0)));
		
		AisMessage8 msg8 = new AisMessage8();
		msg8.setUserId(mmsi);
		msg8.setAppMessage(intendedRoute);
		Vdm vdm = new Vdm();
		vdm.setTalker("AI");
		vdm.setTotal(1);
		vdm.setNum(1);
		try {
			vdm.setMessageData(msg8);
		} catch (SixbitException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		vdm.setSequence(0);
		String encoded = vdm.getEncoded();
		out.println(encoded);
		
		// Verify
		try {
			vdm = new Vdm();
			vdm.parse(encoded);
			AisMessage msg = AisMessage.getInstance(vdm);
			msg8 = (AisMessage8) msg;
			AisApplicationMessage appMessage = msg8.getApplicationMessage();
			@SuppressWarnings("unused")
			BroadcastIntendedRoute routeInformation = (BroadcastIntendedRoute) appMessage;
//			System.out.println("encoded: " + encoded);
//			System.out.println("BroadcastRouteInformation: " + routeInformation + "\n---");
		} catch (Exception e) {
			System.err.println("Some exception: " + e.getMessage());
		}

	}

}
