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
package dk.frv.enav.ins.ais;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.binary.RouteInformation;
import dk.frv.ais.message.binary.RouteMessage;
import dk.frv.enav.ins.gps.GnssTime;

/**
 * Abstract base class for AIS route data
 */
public abstract class AisRouteData implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected Date received;
	protected long duration;
	protected Date etaFirst = null;
	protected Date etaLast = null;
	protected List<GeoLocation> waypoints = new ArrayList<GeoLocation>();
	protected List<Double> ranges = new ArrayList<Double>();
	protected Double routeRange = null;
	protected long sender;
	protected int msgLinkId;
	protected int routeType;
	protected int senderClassification;
	
	/**
	 * Copy constructor
	 * @param routeData
	 */
	public AisRouteData(AisRouteData routeData) {
		this.waypoints = routeData.waypoints;
		this.received = routeData.received;
		this.duration = routeData.duration;
		this.ranges = routeData.ranges;
		this.routeRange = routeData.routeRange;
		this.etaFirst = routeData.etaFirst;
		this.etaLast = routeData.etaLast;
	}
	
	public AisRouteData(RouteMessage routeMessage) {
		received = GnssTime.getInstance().getDate();
		
		for (int i=0; i < routeMessage.getWaypoints().size(); i++) {
			GeoLocation wp = routeMessage.getWaypoints().get(i).getGeoLocation();
			
			if (wp.getLatitude() < 54 || wp.getLatitude() > 60 || wp.getLongitude() < 8 || wp.getLongitude() > 14) {
				System.out.println("ERROR: Wrong wp in AIS broadcast");				
			} else {
				waypoints.add(wp);
			}
		}		
		
		if (routeMessage.getStartMonth() > 0 && routeMessage.getStartDay() > 0) {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0000"));
			cal.set(Calendar.MONTH, routeMessage.getStartMonth() - 1);
			cal.set(Calendar.DAY_OF_MONTH, routeMessage.getStartDay());
			cal.set(Calendar.HOUR_OF_DAY, routeMessage.getStartHour());
			cal.set(Calendar.MINUTE, routeMessage.getStartMin());
			cal.set(Calendar.MILLISECOND, 0);
			etaFirst = cal.getTime();
		}
		
		duration = routeMessage.getDuration() * 60 * 1000;
		if (duration > 0 && etaFirst != null) {
			etaLast = new Date(etaFirst.getTime() + duration);
		}
		
		// Find ranges on each leg
		routeRange = 0.0;
		ranges.add(routeRange);
		for (int i=0; i < waypoints.size() - 1; i++) {
			double dist = waypoints.get(i).getRhumbLineDistance(waypoints.get(i + 1)) / 1852.0;
			routeRange += dist;
			ranges.add(routeRange);
		}
	}
	
	/**
	 * Constructor given AIS route information
	 * @param routeInformation
	 */
	public AisRouteData(RouteInformation routeInformation) {
		this((RouteMessage)routeInformation);		
		msgLinkId = routeInformation.getMsgLinkId();
		routeType = routeInformation.getRouteType();
		senderClassification = routeInformation.getSenderClassification();
	}
	
	public boolean isCancel() {
		return (routeType == 31);
	}

	public Date getReceived() {
		return received;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public boolean hasRoute() {
		return (waypoints != null && waypoints.size() > 0);
	}

	public Date getEtaFirst() {
		return etaFirst;
	}
	
	public Date getEtaLast() {
		return etaLast;
	}
	
	public List<GeoLocation> getWaypoints() {
		return waypoints;
	}
	
	public long getSender() {
		return sender;
	}
	
	public void setSender(long sender) {
		this.sender = sender;
	}
	
	public int getMsgLinkId() {
		return msgLinkId;
	}
	
	public int getRouteType() {
		return routeType;
	}
	
	public int getSenderClassification() {
		return senderClassification;
	}

}
