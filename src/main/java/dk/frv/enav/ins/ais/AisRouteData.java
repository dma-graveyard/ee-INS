package dk.frv.enav.ins.ais;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.binary.RouteInformation;
import dk.frv.enav.ins.gps.GnssTime;

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
	
	public AisRouteData(AisRouteData routeData) {
		this.waypoints = routeData.waypoints;
		this.received = routeData.received;
		this.duration = routeData.duration;
		this.ranges = routeData.ranges;
		this.routeRange = routeData.routeRange;
		this.etaFirst = routeData.etaFirst;
		this.etaLast = routeData.etaLast;
	}
	
	public AisRouteData(RouteInformation routeInformation) {
		received = GnssTime.getInstance().getDate();
		
		msgLinkId = routeInformation.getMsgLinkId();
		routeType = routeInformation.getRouteType();
		senderClassification = routeInformation.getSenderClassification();
		
		for (int i=0; i < routeInformation.getWaypoints().size(); i++) {
			GeoLocation wp = routeInformation.getWaypoints().get(i).getGeoLocation();
			
			if (wp.getLatitude() < 54 || wp.getLatitude() > 60 || wp.getLongitude() < 8 || wp.getLongitude() > 14) {
				System.out.println("ERROR: Wrong wp in AIS broadcast");				
			} else {
				waypoints.add(wp);
			}
		}		
		
		if (routeInformation.getStartMonth() > 0 && routeInformation.getStartDay() > 0) {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0000"));
			cal.set(Calendar.MONTH, routeInformation.getStartMonth() - 1);
			cal.set(Calendar.DAY_OF_MONTH, routeInformation.getStartDay());
			cal.set(Calendar.HOUR_OF_DAY, routeInformation.getStartHour());
			cal.set(Calendar.MINUTE, routeInformation.getStartMin());
			cal.set(Calendar.MILLISECOND, 0);
			etaFirst = cal.getTime();
		}
		
		duration = routeInformation.getDuration() * 60 * 1000;
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
