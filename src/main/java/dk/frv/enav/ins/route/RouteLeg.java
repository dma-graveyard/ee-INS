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

import java.io.Serializable;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.common.util.Calculator;
import dk.frv.enav.ins.common.util.Converter;

public class RouteLeg implements Serializable {

	private static final int R = 6371; // earths radius in km
	private static final long serialVersionUID = 1L;
	
	/**
	 * Planned leg speed
	 */
	protected double speed;	
	/**
	 * Sail heading rhumb line or great circle
	 */
	protected Heading heading;	
	/**
	 * XTD starboard nm
	 */
	protected Double xtdStarboard;	
	/**
	 * XTD port nm
	 */
	protected Double xtdPort;
	/**
	 * The starting wp of leg
	 */
	protected RouteWaypoint startWp;
	/**
	 * The end wp of leg
	 */
	protected RouteWaypoint endWp;
		
	public RouteLeg() {
		
	}
	
	public RouteLeg(RouteLeg rll){
		this.speed = rll.getSpeed();
		this.heading = rll.getHeading();
		this.xtdStarboard = rll.getXtdStarboard();
		this.xtdPort = rll.getXtdPort();
		this.startWp = rll.getStartWp();
		this.endWp = rll.getEndWp();
	}
	
	
	public RouteLeg(RouteWaypoint startWp, RouteWaypoint endWp) {
		this.startWp = startWp;
		this.endWp = endWp;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public Heading getHeading() {
		return heading;
	}

	public void setHeading(Heading heading) {
		this.heading = heading;
	}

	public Double getXtdStarboard() {
		return xtdStarboard;
	}
	
	public Double getXtdStarboardMeters() {
		if (xtdStarboard == null) return null;
		return Converter.nmToMeters(xtdStarboard);
	}

	public void setXtdStarboard(Double xtdStarboard) {
		this.xtdStarboard = xtdStarboard;
	}

	public Double getXtdPort() {
		return xtdPort;
	}
	
	public Double getXtdPortMeters() {
		if (xtdPort == null) return null;
		return Converter.nmToMeters(xtdPort);
	}

	public void setXtdPort(Double xtdPort) {
		this.xtdPort = xtdPort;
	}
	
	public Double getMaxXtd() {
		if (xtdPort == null) return xtdStarboard;
		if (xtdStarboard == null) return xtdPort;
		return Math.max(xtdPort, xtdStarboard);
	}
	
	public RouteWaypoint getStartWp() {
		return startWp;
	}
	
	public void setStartWp(RouteWaypoint startWp) {
		this.startWp = startWp;
	}
	
	public RouteWaypoint getEndWp() {
		return endWp;
	}
	
	public void setEndWp(RouteWaypoint endWp) {
		this.endWp = endWp;
	}
	
	public double calcRng() {		
		return Calculator.range(startWp.getPos(), endWp.getPos(), heading);	
	}
	
	public double calcBrg() {
		return Calculator.bearing(startWp.getPos(), endWp.getPos(), heading);
	}
	
	/**
	 * Ttg in milliseconds
	 * @return
	 */
	public long calcTtg() {
		if (speed < 0.1) {
			return -1L;
		}
		return Math.round((calcRng() * 3600.0 / speed) * 1000.0);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RouteLeg [heading=");
		builder.append(heading);
		builder.append(", speed=");
		builder.append(speed);
		builder.append(", xtdPort=");
		builder.append(xtdPort);
		builder.append(", xtdStarboard=");
		builder.append(xtdStarboard);
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Calculate the great circle cross track distance from a route leg to a given geographical location.
	 * Formula from <a href="http://www.movable-type.co.uk/scripts/latlong.html">http://www.movable-type.co.uk/scripts/latlong.html</a>
	 * @param crossTrackPoint Geographical location
	 * @return Distance
	 */
	public double calculateCrossTrackDist(GeoLocation crossTrackPoint) {
		if(heading.equals(Heading.GC)){
			System.out.println("GC");
		} else {
			System.out.println("RL");
		}
		double d13 = Calculator.range(startWp.getPos(), crossTrackPoint, Heading.GC);
		double brng13 = Calculator.bearing(startWp.getPos(), crossTrackPoint, Heading.GC);
		double brng12 = Calculator.bearing(startWp.getPos(), endWp.getPos(), heading);
		
		/*Formula: 	dxt = asin(sin(d13/R)*sin(θ13−θ12)) * R
		
		where 	
		d13 is distance from start point to third point
		θ13 is (initial) bearing from start point to third point
		θ12 is (initial) bearing from start point to end point
		R is the earth’s radius
		
		*/ 	

		double dXt = Math.asin(Math.sin(d13/R)*Math.sin(brng13-brng12)) * R;
		return dXt;
	}
}
