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
package dk.frv.enav.ins.route;

import java.util.Date;
import java.util.LinkedList;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.ins.common.util.Calculator;
import dk.frv.enav.ins.common.util.Converter;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.gps.GpsData;

/**
 * Class representing an active route
 */
public class ActiveRoute extends Route {

	public enum ActiveWpSelectionResult {NO_CHANGE, CHANGED, ROUTE_FINISHED};
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The index of active waypoint
	 */
	protected int activeWaypointIndex = 0;
	/**
	 * The current leg
	 */
	protected RouteLeg currentLeg = null;
	/**
	 * The current GPS data
	 */
	protected GpsData currentGpsData = null;
	/**
	 * Average speed over an appropriate time period
	 */
	protected double avgSpeed = 0;
	/**
	 * Range to active waypoint
	 */
	protected Double activeWpRng;
	/**
	 * Bearing to active waypoint
	 */
	protected Double activeWpBrg;
	/**
	 * TTG to active waypoint
	 */
	protected Long activeWpTtg = null;	
	/**
	 * A special TTG that cannot be null
	 * Assumes that intended speed will be reached soon
	 */
	protected Long niceActiveWpTtg = null;
	/**
	 * Time of original activation
	 */
	protected Date origStarttime = null;
	/**
	 * TTG from the active waypoint
	 */
	protected long remainTtg;
	/**
	 * DTG from the active waypoint
	 */
	protected double remainDtg;
	
	/**
	 * The minimum radius of wp you have to be inside to change active
	 * waypoint
	 */
	protected double wpCircleMin = 0.2; // 0.2 nm = 365 m
	
	/**
	 * Should relaxed WP change be used
	 */
	protected boolean relaxedWpChange = true;

	public ActiveRoute(Route route, GpsData gpsData) {
		super();
		this.waypoints = route.waypoints;
		this.name = route.name;
		this.visible = route.visible;
		this.departure = route.departure;
		this.destination = route.destination;
		this.starttime = route.starttime;
		this.origStarttime = GnssTime.getInstance().getDate();
		this.routeMetocSettings = route.routeMetocSettings;
		this.metocForecast = route.metocForecast;
		calcValues(true);
		changeActiveWaypoint(getBestWaypoint(route, gpsData));
	}
	/*
	 * Get's the most optimal route choice
	 * If speed is lower than 3 we start at point 0, otherwise we take bearing and distance into account and
	 * select the best match.
	 * It will never select a waypoint behind itself.
	 */
	private int getBestWaypoint(Route route, GpsData gpsData) {
	//	LinkedList<Double> weightedDistance = new LinkedList<Double>();
		if (gpsData.isBadPosition() || gpsData.getSog() < 3)
		{
			return 0;
			
		}
		else
		{		
				double smallestDist = 99999999.0;
				int index = 0;
				for (int i = 0; i <= route.getWaypoints().size()-1; i++)
				{
					GeoLocation wpPos = route.getWaypoints().get(i).getPos();
					double distance = gpsData.getPosition().getRhumbLineDistance(wpPos);
					
					double angleToWpDeg = gpsData.getPosition().getRhumbLineBearing(wpPos);

					double weight = 1 - ( Math.toRadians(gpsData.getCog()) - Math.toRadians(angleToWpDeg) );
					double result = ( Math.abs(weight) * (0.5 * Converter.metersToNm(distance)) );
//					double result = Math.abs(Math.toRadians(gpsData.getCog()) - Math.toRadians(angleToWpDeg));
				//	weightedDistance.add(result);

					double upper = gpsData.getCog()+90;
					double lower = gpsData.getCog()-90;
					

					
					if (result < smallestDist && (angleToWpDeg < upper && angleToWpDeg > lower))
					{
						smallestDist = result;
						index = i;
					}

				}
				//System.out.println(smallestDist);
	//			System.out.println(weightedDistance);
				return index;
				
			}
			


	}

	public synchronized void update(GpsData gpsData) {
		if (gpsData.isBadPosition()) {
			return;
		}

		// Get active waypoint
		RouteWaypoint activeWaypoint = waypoints.get(activeWaypointIndex);
		// Set current GPS data
		currentGpsData = gpsData;
		// TODO calculate avg speed
		avgSpeed = gpsData.getSog();

		// Calculate brg and rng
		activeWpRng = Calculator.range(gpsData.getPosition(), activeWaypoint.getPos(), currentLeg.getHeading());
		activeWpBrg = Calculator.bearing(gpsData.getPosition(), activeWaypoint.getPos(), currentLeg.getHeading());
		
		// Calculate nice TTG
		niceActiveWpTtg = Math.round((activeWpRng / currentLeg.getSpeed()) * 60 * 60 * 1000);

		// Calculate TTG to active waypoint
		if (avgSpeed > 0.1) {
			activeWpTtg = Math.round((activeWpRng / avgSpeed) * 60 * 60 * 1000);
			if (activeWpTtg < niceActiveWpTtg) {
				niceActiveWpTtg = activeWpTtg;
			} else {
				double pctOff = ((niceActiveWpTtg - activeWpTtg) / niceActiveWpTtg) * 100.0;
				if (pctOff < 50.0) {
					niceActiveWpTtg = activeWpTtg;
				}
			}			
		} else {
			activeWpTtg = null;
		}
				
	}
	
	@Override
	public void setMetocForecast(MetocForecast metocForecast) {
		this.metocForecast = metocForecast;
		this.metocStarttime = getStarttime();
		this.metocEta = getNiceEta();
	}
	
	@Override
	public boolean isMetocValid(long tolerance) {
		return super.isMetocValid(getNiceEta(), tolerance);
	}

	public synchronized ActiveWpSelectionResult chooseActiveWp() {
		// Calculate if in Wp circle
		boolean inWpCircle = false;
		double xtd = (currentLeg.getMaxXtd() == null) ? 0.0 : currentLeg.getMaxXtd();
		double radius = Math.max(xtd, wpCircleMin);
		if (activeWpRng < radius) {
			inWpCircle = true;
		}
		
		// If heading for last wp and in circle, we finish route
		if (isLastWp()) {
			if (inWpCircle) {
				return ActiveWpSelectionResult.ROUTE_FINISHED;
			} else {
				return ActiveWpSelectionResult.NO_CHANGE;
			}		
		}
		
		// Calculate distance from ship to next waypoint		
		RouteLeg nextLeg = getActiveWp().getOutLeg();
		double nextWpRng = Calculator.range(currentGpsData.getPosition(), nextLeg.getEndWp().getPos(), nextLeg.getHeading());
		
		if (inWpCircle) {
			// If closer to next wp than the dist between wp's, we change
			if (nextWpRng < getWpRng(activeWaypointIndex)) {
				changeActiveWaypoint(activeWaypointIndex + 1);
				return ActiveWpSelectionResult.CHANGED;
			}
		} else {
			// Some temporary fallback when we are really of course
			if (relaxedWpChange) {
				if (2 * nextWpRng < getWpRng(activeWaypointIndex)) {
					changeActiveWaypoint(activeWaypointIndex + 1);
					return ActiveWpSelectionResult.CHANGED;
				}
			}
		}
		
		return ActiveWpSelectionResult.NO_CHANGE;
	}

	public synchronized void changeActiveWaypoint(int index) {
		// Save actual ETA
		etas.set(activeWaypointIndex, GnssTime.getInstance().getDate());
		// Change active waypoint 
		activeWaypointIndex = index;
		// Set current leg
		if (index == 0) {
			// When the first waypoint is active, the first leg is used as
			// current leg
			this.currentLeg = waypoints.get(0).getOutLeg();
		} else {
			this.currentLeg = waypoints.get(index).getInLeg();
		}
		// Calculate remaining DTG, TTG and ETA
		remainDtg = 0;
		remainTtg = 0;
		for (int i = index; i < waypoints.size() - 1; i++) {
			remainDtg += dtgs[i];
			remainTtg += ttgs[i];
		}
		
		reCalcRemainingWpEta();
	}

	@Override
	public synchronized Date getEta() {
		if (activeWpTtg == null) {
			return null;
		}
		return new Date(GnssTime.getInstance().getDate().getTime() + remainTtg + activeWpTtg);
	}
	
	public synchronized Date getActiveWaypointEta() {
		if (activeWpTtg == null) {
			return null;
		}
		return new Date(GnssTime.getInstance().getDate().getTime() + activeWpTtg);
	}
	
	public synchronized Date getNiceActiveWaypointEta() {
	    if (niceActiveWpTtg == null) {
	        return null;
	    }
	    return new Date(GnssTime.getInstance().getDate().getTime() + niceActiveWpTtg);
	}

	@Override
	public synchronized Long getTtg() {
		if (activeWpTtg == null) {
			return null;
		}
		return activeWpTtg + remainTtg;
	}

	@Override
	public synchronized Double getDtg() {
		if (activeWpRng == null) {
			return null;
		}
		return activeWpRng + remainDtg;
	}

	public synchronized int getActiveWaypointIndex() {
		return activeWaypointIndex;
	}
	
	public synchronized RouteWaypoint getActiveWp() {
		return waypoints.get(activeWaypointIndex);
	}

	public synchronized boolean reCalcRemainingWpEta(){
		int aw = getActiveWaypointIndex();
		Date eta = getNiceActiveWaypointEta();
		
		if (eta == null) {
		    return false;
		}
		
		// Set eta at active waypoint
		etas.set(aw, eta);
		long etaTime = eta.getTime();
		for (int i = aw; i < waypoints.size() - 1; i++) {
			etaTime += ttgs[i];
			etas.set(i + 1, new Date(etaTime));
		}
		return true;
	}
	
	public synchronized Date getNiceEta() {
		if (!reCalcRemainingWpEta()) {
			return null;
		}
		return etas.get(etas.size() - 1);
	}

	public synchronized Date getOrigStarttime() {
		return origStarttime;
	}
	
	@Override
	public void adjustStartTime() {
		// Do not change starttime for active route
		
	}
	
	public synchronized boolean isLastWp()  {
		return isLastWaypoint(activeWaypointIndex);
	}

	public synchronized Double getActiveWpRng() {
		return activeWpRng;
	}

	public synchronized Double getActiveWpBrg() {
		return activeWpBrg;
	}

	public synchronized Long getActiveWpTtg() {
		return activeWpTtg;
	}
	
	public synchronized Long getNiceActiveWpTtg() {
		return niceActiveWpTtg;
	}
	
	public synchronized RouteLeg getCurrentLeg() {
		return currentLeg;
	}
	
	public void setWpCircleMin(double wpCircleMin) {
		this.wpCircleMin = wpCircleMin;
	}
	
	public void setRelaxedWpChange(boolean relaxedWpChange) {
		this.relaxedWpChange = relaxedWpChange;
	}
	
}
