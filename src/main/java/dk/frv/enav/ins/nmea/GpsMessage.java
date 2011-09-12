package dk.frv.enav.ins.nmea;

import dk.frv.ais.geo.GeoLocation;

public class GpsMessage {
	
	private GeoLocation pos;
	private Double sog;
	private Double cog;
	
	public GpsMessage() {		
	}
	
	public GeoLocation getPos() {
		return pos;
	}
	
	public void validateFields() {
		if (cog != null && cog >= 360) {
			cog = null;
		}
		if (sog != null && sog >= 102.2) {
			sog = null;
		}
	}
	
	public void setPos(GeoLocation pos) {
		this.pos = pos;
	}
	
	public boolean isValidPosition() {
		return (pos != null && pos.getLatitude() <= 90 && pos.getLongitude() <= 180);
	}
	
	public Double getSog() {
		return sog;
	}

	public void setSog(Double sog) {
		this.sog = sog;
	}

	public Double getCog() {
		return cog;
	}

	public void setCog(Double cog) {
		this.cog = cog;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GpsMessage [cog=");
		builder.append(cog);
		builder.append(", pos=");
		builder.append(pos);
		builder.append(", sog=");
		builder.append(sog);
		builder.append(", time=");
		builder.append("]");
		return builder.toString();
	}
	
	

}
