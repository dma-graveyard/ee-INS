package dk.frv.enav.ins.util.route;

import java.util.Date;

import dk.frv.ais.geo.GeoLocation;

public class TimePoint extends GeoLocation implements Comparable<TimePoint> {
	private static final long serialVersionUID = 1L;
	
	private Date time;
	
	public TimePoint(GeoLocation pos, Date time) {
		super(pos.getLatitude(), pos.getLongitude());
		this.time = time;
	}
	
	public Date getTime() {
		return time;
	}

	@Override
	public int compareTo(TimePoint tp) {
		if (time.getTime() == tp.getTime().getTime()) {
			return 0;
		}
		return ((time.getTime() < tp.getTime().getTime()) ? -1 : 1);
	}

	@Override
	public String toString() {
		return "TimePoint [lat=" + getLatitude() + " lon=" + getLongitude() + " time=" + time + "]";
	}	

}
