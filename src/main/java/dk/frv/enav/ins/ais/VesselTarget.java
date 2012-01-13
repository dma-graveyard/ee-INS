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

import java.util.Date;

import dk.frv.enav.ins.gps.GnssTime;

/**
 * Class representing an AIS vessel target
 */
public class VesselTarget extends AisTarget {

	/**
	 * Time an intended route is considered valid without update
	 */
	public static final long ROUTE_TTL = 10 * 60 * 1000; // 10 min
	
	/**
	 * Target class A or B 
	 */
	public enum AisClass {A, B};
	
	private VesselPositionData positionData = null;	
	private VesselStaticData staticData = null;
	private AisIntendedRoute aisIntendedRoute = null;
	private AisClass aisClass; 
	private VesselTargetSettings settings;
	

	/**
	 * Copy constructor
	 * @param vesselTarget
	 */
	public VesselTarget(VesselTarget vesselTarget) {
		super(vesselTarget);
	    this.aisClass = vesselTarget.aisClass;
	    if (vesselTarget.positionData != null) {
	    	this.positionData = new VesselPositionData(vesselTarget.positionData);
	    }
	    if (vesselTarget.staticData != null) {
	    	this.staticData = new VesselStaticData(vesselTarget.staticData);
	    }
	    if (vesselTarget.aisIntendedRoute != null) {
	    	this.aisIntendedRoute = new AisIntendedRoute(vesselTarget.aisIntendedRoute);
	    }
	    if (vesselTarget.settings != null) {
	    	this.settings = new VesselTargetSettings(vesselTarget.settings);
	    }
	}

	/**
	 * Empty constructor
	 */
	public VesselTarget() {
		super();
		settings = new VesselTargetSettings();
	}
	
	public VesselPositionData getPositionData() {
		return positionData;
	}

	public void setPositionData(VesselPositionData positionData) {
		this.positionData = positionData;
		if (aisIntendedRoute != null) {
			aisIntendedRoute.update(positionData);
		}
	}

	public VesselStaticData getStaticData() {
		return staticData;
	}

	public void setStaticData(VesselStaticData staticData) {
		this.staticData = staticData;
	}
	
	public AisIntendedRoute getAisRouteData() {
		return aisIntendedRoute;
	}
	
	public void setAisRouteData(AisIntendedRoute aisIntendedRoute) {
		this.aisIntendedRoute = aisIntendedRoute;
		this.aisIntendedRoute.update(positionData);
	}

	public AisClass getAisClass() {
		return aisClass;
	}

	public void setAisClass(AisClass aisClass) {
		this.aisClass = aisClass;
	}
	
	public VesselTargetSettings getSettings() {
		return settings;
	}
	
	public void setSettings(VesselTargetSettings settings) {
		this.settings = settings;
	}

	
	
	/**
	 * Returns true if route information changes from valid to invalid
	 * @return
	 */
	public boolean checkAisRouteData() {
		if (aisIntendedRoute == null || aisIntendedRoute.getWaypoints().size() == 0 || aisIntendedRoute.getDuration() == 0) {
			return false;
		}
		Date now = GnssTime.getInstance().getDate();
		long elapsed = now.getTime() - aisIntendedRoute.getReceived().getTime();
		if (elapsed > ROUTE_TTL) {
			aisIntendedRoute = null;
			return true;
		}
		return false;		
	}
	
	public boolean hasIntendedRoute() {
		return (aisIntendedRoute != null);
	}

	
	/**
	 * Determine if the target has gone.
	 * @param now will be used as current time
	 * @param strict when strict is false more relaxed rules will used suitable for down sampled data
	 * @return if the target has gone  
	 */
	@Override
	public boolean hasGone(Date now, boolean strict) {
		long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;
		
		// Base gone "loosely" on ITU-R Rec M1371-4 4.2.1
		long tol = 10;
		float sog = positionData.getSog();
		boolean anchorOrMoored = ((positionData.getNavStatus() == 1) || (positionData.getNavStatus() == 5));
		
		if (aisClass == AisClass.A) {
			if (anchorOrMoored) {
				if (sog > 3) {
					tol = 10;
				}
				tol = 180;
			} else if (sog <= 14) {
				tol = 10;
			} else {
				tol = 6;
			}
		} else {
			if (sog <= 2) {
				tol = 180;
			} else if (sog <= 14) {
				tol = 30;
			} else {
				tol = 15;
			}
		}
		
		// Some home made tolerance rules
		if (strict) {
			tol *= 4;
			if (tol < 120) {
				tol = 120;
			}
		} else {
			// Allow for long timeout
			tol = 600; // 10 minutes
		}
		
		return (elapsed > tol);
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VesselTarget [aisClass=");
		builder.append(aisClass);
		builder.append(", positionData=");
		builder.append(positionData);
		builder.append(", staticData=");
		builder.append(staticData);
		builder.append("]");
		return builder.toString();
	}

	

	
	
	
}
