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
package dk.frv.enav.ins.gps;

import java.io.Serializable;
import java.util.Date;

import dk.frv.ais.geo.GeoLocation;

/**
 * Class representing GPS data position, speed over ground and course over ground. 
 */
public class GpsData implements Serializable {
		
	private static final long serialVersionUID = 1L;
	
	private Date lastUpdated = new Date(0);
	private GeoLocation position = null;
	private Double cog = null;
	private Double sog = null;
	private boolean badPosition = false;
	
	public GpsData() {
		
	}
	
	/**
	 * Copy constructor
	 */
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
	
	/**
	 * Is the current position valid
	 * @return
	 */
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
