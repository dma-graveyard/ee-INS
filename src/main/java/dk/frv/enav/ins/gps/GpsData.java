package dk.frv.enav.ins.gps;

import java.io.Serializable;
import java.util.Date;

import dk.frv.ais.geo.GeoLocation;

public class GpsData implements Serializable {
		
	private static final long serialVersionUID = 1L;
	
	private Date lastUpdated = new Date(0);
	private GeoLocation position = null;
	private Double cog = null;
	private Double sog = null;
	private boolean badPosition = false;
	
	public GpsData() {
		
	}
	
	public GpsData(GpsData gpsData) {
		this.lastUpdated = new Date(gpsData.lastUpdated.getTime());
		this.position = new GeoLocation(gpsData.position);
		if (gpsData.cog != null) {
			this.cog = new Double(gpsData.cog);
		}
		if (gpsData.sog != null) {
			this.sog = new Double(gpsData.sog);
		}
		this.badPosition = gpsData.badPosition;		
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public GeoLocation getPosition() {
		return position;
	}

	public void setPosition(GeoLocation position) {
		this.position = position;
	}

	public Double getCog() {
		return cog;
	}

	public void setCog(Double cog) {
		this.cog = cog;
	}

	public Double getSog() {
		return sog;
	}

	public void setSog(Double sog) {
		this.sog = sog;
	}
	
	public boolean isBadPosition() {
		return badPosition;
	}
	
	public void setBadPosition(boolean badPosition) {
		this.badPosition = badPosition;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GpsData [badPosition=");
		builder.append(badPosition);
		builder.append(", cog=");
		builder.append(cog);
		builder.append(", lastUpdated=");
		builder.append(lastUpdated);
		builder.append(", position=");
		builder.append(position);
		builder.append(", sog=");
		builder.append(sog);
		builder.append("]");
		return builder.toString();
	}
	
}
