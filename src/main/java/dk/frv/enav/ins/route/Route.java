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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gps.GnssTime;

/**
 * Route class
 */
public class Route implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * List of waypoints for route
	 */
	protected LinkedList<RouteWaypoint> waypoints = new LinkedList<RouteWaypoint>();
	/**
	 * Optional name for route
	 */
	protected String name;
	/**
	 * Name of departure
	 */
	protected String departure;
	/**
	 * Name of destination
	 */
	protected String destination;
	/**
	 * Route visible
	 */
	protected boolean visible = true;
	/**
	 * Start time for route
	 */
	protected Date starttime = null;
	/**
	 * List of calculated TTG's for each leg
	 */
	protected long[] ttgs = null;
	/**
	 * List of calculated DTG's for each leg
	 */
	protected double[] dtgs = null;
	/**
	 * Total TTG
	 */
	protected Long totalTtg = null;
	/**
	 * Total DTG
	 */
	protected Double totalDtg = null;
	/**
	 * 
	 */
	protected List<Date> etas = null;
	/**
	 * METOC forecast for the route
	 */
	protected MetocForecast metocForecast;
	/**
	 * The starttime used for the current metoc
	 */
	protected Date metocStarttime = null;
	/**
	 * The eta used for the current metoc
	 */
	protected Date metocEta = null;
	
	/**
	 * Settings for the route metoc
	 */
	protected RouteMetocSettings routeMetocSettings;

	public Route() {

	}
	
	/**
	 * Copy constructor, performs a shallow copy.
	 * @param orig Original route to copy
	 */
	public Route(Route orig) {
		this.waypoints = new LinkedList<RouteWaypoint>(orig.waypoints);
		this.name = orig.name;
		this.departure = orig.departure;
		this.destination = orig.destination;
		this.visible = orig.visible;
		this.starttime = orig.starttime;
		this.ttgs = orig.ttgs;
		this.dtgs = orig.dtgs;
		this.totalTtg = orig.totalTtg;
		this.totalDtg = orig.totalDtg;
		this.etas = orig.etas;
		this.metocForecast = orig.metocForecast;
		this.metocStarttime = orig.metocStarttime;
		this.metocEta = orig.metocEta;
		this.routeMetocSettings = orig.routeMetocSettings;
	}

	// Methods
	
	/**
	 * Performs a deep copy of a route.
	 */
	public Route copy() {
		Route newRoute = new Route();
		LinkedList<RouteWaypoint> waypoints = new LinkedList<RouteWaypoint>();
		for (RouteWaypoint routeWaypoint : this.waypoints) {
			RouteWaypoint newRouteWaypoint = routeWaypoint.copy();
			waypoints.add(newRouteWaypoint);
		}
		// Iterates through the list of waypoints beginning at the second waypoint in the route, while
		// creating route legs in a backward fashion. The values of the original leg is copied also.
		// Perhaps this can be done more efficiently with a copy constructor (or method) for route legs, but 
		// forward referencing a waypoint which has not been created has to be solved in some way...
		for (int i = 1; i < waypoints.size(); i++) {
			RouteWaypoint currWaypoint = waypoints.get(i);
			RouteWaypoint prevWaypoint = waypoints.get(i-1);
			RouteLeg routeLeg = this.waypoints.get(i).getInLeg();
			
			RouteLeg newRouteLeg = new RouteLeg();
			newRouteLeg.setSpeed(routeLeg.getSpeed());
			newRouteLeg.setHeading(routeLeg.getHeading());
			newRouteLeg.setXtdStarboard(routeLeg.getXtdStarboard());
			newRouteLeg.setXtdPort(routeLeg.getXtdPort());
			
			newRouteLeg.setStartWp(prevWaypoint);
			newRouteLeg.setEndWp(currWaypoint);
			
			prevWaypoint.setOutLeg(newRouteLeg);
			currWaypoint.setInLeg(newRouteLeg);
		}
		newRoute.setWaypoints(waypoints);
		// Immutable objects are safe to copy this way?
		newRoute.name = this.name;
		newRoute.departure = this.departure;
		newRoute.destination = this.destination;
		newRoute.visible = this.visible;
		newRoute.starttime = this.starttime;
		newRoute.ttgs = this.ttgs;
		newRoute.dtgs = this.dtgs;
		newRoute.totalTtg = this.totalTtg;
		newRoute.totalDtg = this.totalDtg;
		newRoute.etas = this.etas;
		newRoute.metocForecast = this.metocForecast;
		newRoute.metocStarttime = this.metocStarttime;
		newRoute.metocEta = this.metocEta;
		newRoute.routeMetocSettings = this.routeMetocSettings;
		
		return newRoute;
	}
	
	/**
	 * Performs a deep reverse of a route.
	 */
	public Route reverse() {
		Route newRoute = new Route();
		LinkedList<RouteWaypoint> waypoints = new LinkedList<RouteWaypoint>();
		
		int routeSize = this.waypoints.size() -1;
		int j = 0;

		for (int i = routeSize ; i > -1 ; i--){
			RouteWaypoint newRouteWaypoint = this.waypoints.get(i).copy();
			newRouteWaypoint.setName(this.waypoints.get(j).getName()); //Do we want to reverse the name too?
			waypoints.add(newRouteWaypoint);
			j++;
		}
		
		// Iterates through the list of waypoints beginning at the second waypoint in the route, while
		// creating route legs in a backward fashion. The values of the original leg is copied also.
		// Perhaps this can be done more efficiently with a copy constructor (or method) for route legs, but 
		// forward referencing a waypoint which has not been created has to be solved in some way...
		for (int i = 1; i < waypoints.size(); i++) {
			RouteWaypoint currWaypoint = waypoints.get(i);
			RouteWaypoint prevWaypoint = waypoints.get(i-1);
			RouteLeg routeLeg = this.waypoints.get(i).getInLeg();
			
			RouteLeg newRouteLeg = new RouteLeg();
			newRouteLeg.setSpeed(routeLeg.getSpeed());
			newRouteLeg.setHeading(routeLeg.getHeading());
			newRouteLeg.setXtdStarboard(routeLeg.getXtdStarboard());
			newRouteLeg.setXtdPort(routeLeg.getXtdPort());
			
			newRouteLeg.setStartWp(prevWaypoint);
			newRouteLeg.setEndWp(currWaypoint);
			
			prevWaypoint.setOutLeg(newRouteLeg);
			currWaypoint.setInLeg(newRouteLeg);
		}
		newRoute.setWaypoints(waypoints);

		// Immutable objects are safe to copy this way?
		newRoute.name = this.name;
		newRoute.departure = this.departure;
		newRoute.destination = this.destination;
		newRoute.visible = this.visible;
		newRoute.starttime = this.starttime;
		newRoute.ttgs = this.ttgs;
		newRoute.dtgs = this.dtgs;
		newRoute.totalTtg = this.totalTtg;
		newRoute.totalDtg = this.totalDtg;
		newRoute.etas = this.etas;
		newRoute.metocForecast = this.metocForecast;
		newRoute.metocStarttime = this.metocStarttime;
		newRoute.metocEta = this.metocEta;
		newRoute.routeMetocSettings = this.routeMetocSettings;
		

		return newRoute;
	}
		
	
	

	// Calculated measures

	public Double getWpRng(int index) {
		calcValues();
		if (isLastWaypoint(index))
			return null;

		return dtgs[index];
	}
	
	public double getWpRngSum(int index) {
		double sum = 0;
		for (int i=0; i < index && i < dtgs.length; i++) {
			sum += dtgs[i];
		}		
		return sum;
	}

	public Double getWpBrg(RouteWaypoint routeWaypoint) {
		return routeWaypoint.calcBrg();
	}

	public Long getWpTtg(int index) {
		calcValues();
		if (index <= 0)
			return null;
		return ttgs[index - 1];
	}

	public Date getWpEta(int index) {
		calcValues();
		return etas.get(index);
	}
	
	// Getters and setters from here

	public LinkedList<RouteWaypoint> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(LinkedList<RouteWaypoint> waypoints) {
		this.waypoints = waypoints;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
		calcValues(true);
	}
	
	public void adjustStartTime() {
		Date now = GnssTime.getInstance().getDate();
		if (starttime == null || starttime.before(now)) {
			setStarttime(now);
		}
	}
	
	public MetocForecast getMetocForecast() {
		return metocForecast;
	}
	
	public void setMetocForecast(MetocForecast metocForecast) {
		this.metocForecast = metocForecast;
		this.metocStarttime = getStarttime();
		this.metocEta = getEta();
	}
	
	/**
	 * Returns true if not drifted to far from plan
	 * @param tolerance in minutes
	 * @return
	 */
	public boolean isMetocValid(long tolerance) {
		Date eta = getEta();		
		return isMetocValid(eta, tolerance);
	}
	
	protected boolean isMetocValid(Date eta, long tolerance) {
		//System.out.println("isMetocValid eta: " + eta + " metocEta: " + metocEta + " metocStarttime: " + metocStarttime + " starttime: " + starttime);
		
		if (metocStarttime == null || metocEta == null || starttime == null || eta == null) {
			System.out.println("Missing fields for isMetocValid");
			return false;
		}
		
		// Difference in starttime
		long startimeDiff = Math.abs(starttime.getTime() - metocStarttime.getTime()) / 1000 / 60;
		//System.out.println("startimeDiff: " + startimeDiff);
		if (startimeDiff > tolerance) {
			return false;
		}
		// Difference in eta
		long etaDiff = Math.abs(eta.getTime() - metocEta.getTime()) / 1000 / 60;
		//System.out.println("etaDiff: " + etaDiff);
		if (etaDiff > tolerance) {
			return false;
		}
	
		return true;
	}
	
	public void removeMetoc() {
		this.metocForecast = null;
		if (routeMetocSettings != null) {
			routeMetocSettings.setShowRouteMetoc(false);
		}
		this.metocStarttime = null;
		this.metocEta = null;
	}
	
	public RouteMetocSettings getRouteMetocSettings() {
		return routeMetocSettings;
	}
	
	public void setRouteMetocSettings(RouteMetocSettings routeMetocSettings) {
		this.routeMetocSettings = routeMetocSettings;
	}

	public Long getTtg() {
		calcValues();
		return totalTtg;
	}

	public Date getEta(Date starttime) {
		// Calculate ETA based on given starttime
		Long ttg = getTtg();
		if (ttg == null)
			return null;
		return new Date(starttime.getTime() + ttg);
	}

	/**
	 * Get distance to go i NM
	 * 
	 * @return
	 */
	public Double getDtg() {
		calcValues();
		return totalDtg;
	}

	public Date getEta() {
		if (starttime == null) {
			return null;
		}
		return getEta(starttime);
	}

	protected boolean isLastWaypoint(int index) {
		return (index == waypoints.size() -1);
	}

	public void calcValues(boolean force) {
		if (!force && ttgs != null && etas != null) {
			return;
		}
		totalTtg = 0L;
		totalDtg = 0.0;
		// Create array TTG's and DTG's array
		ttgs = new long[waypoints.size() - 1];
		dtgs = new double[waypoints.size() - 1];
		// Iterate through legs
		for (int i = 0; i < waypoints.size() - 1; i++) {
			ttgs[i] = waypoints.get(i).getOutLeg().calcTtg();
			totalTtg += ttgs[i];
			dtgs[i] = waypoints.get(i).getOutLeg().calcRng();
			totalDtg += dtgs[i];
		}
		calcAllWpEta();
	}
	
	protected void calcValues() {
		calcValues(false);
	}
	
	public void calcAllWpEta() {
		etas = new ArrayList<Date>();
		Date etaStart = starttime;
		if (etaStart == null ) {
			etaStart = GnssTime.getInstance().getDate();
		}
		long eta = etaStart.getTime();
		etas.add(new Date(eta));
		for (int i=0; i < waypoints.size() - 1; i++) {
			eta += ttgs[i];
			etas.add(new Date(eta));
		}
	}
	
	public boolean saveToFile(File file) {
		// TODO
		return false;
	}
	
	public boolean deleteWaypoint(int index){
		if(waypoints.size() > 2){
			for (int i = 0; i < waypoints.size(); i++){
				if(i == index){
					if(isLastWaypoint(i)){
						RouteWaypoint before = waypoints.get(i-1);
						before.setOutLeg(null);
						
						waypoints.remove(i);
					} else if(i == 0) {
						RouteWaypoint after = waypoints.get(i+1);
						after.setInLeg(null);
						
						waypoints.remove(i);
					} else {
						RouteWaypoint before = waypoints.get(i-1);
						RouteWaypoint after = waypoints.get(i+1);
						RouteLeg keeper = before.getOutLeg();
						
						keeper.setEndWp(after);
						after.setInLeg(keeper);
						
						waypoints.remove(i);
					}
				}
			}
			calcValues(true);
		} else {
			int result = JOptionPane.showConfirmDialog(EeINS.getMainFrame(), "A route must have at least two waypoints.\nDo you want to delete the route?", "Delete Route?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(result == JOptionPane.YES_OPTION){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Create a waypoint by splitting a RouteLeg
	 * @param routeLeg Route leg to be split
	 * @param position Geographical position of the new waypoint
	 * @param waypointIndex Index of the legs start waypoint
	 */
	public void createWaypoint(RouteLeg routeLeg, GeoLocation position){
		RouteWaypoint previousWaypoint = routeLeg.getStartWp();
		RouteWaypoint nextWaypoint = routeLeg.getEndWp();
		RouteWaypoint newWaypoint = new RouteWaypoint(previousWaypoint);
		RouteLeg newRouteLeg = new RouteLeg(routeLeg);
		
		// set up legs
		routeLeg.setEndWp(newWaypoint);
		newRouteLeg.setStartWp(newWaypoint);
		
		// set up waypoints
		newWaypoint.setInLeg(routeLeg);
		newWaypoint.setOutLeg(newRouteLeg);
		newWaypoint.setPos(position);
		newWaypoint.calcRot();
		
		nextWaypoint.setInLeg(newRouteLeg);
		
		// find current waypoint index
		RouteWaypoint count = routeLeg.getStartWp();
		int i = 1;
		while(count.getInLeg() != null){
			i++;
			count = count.getInLeg().getStartWp(); 
		}
		
		// add the waypoint to the linked list in the right position
		waypoints.add(i, newWaypoint);
		calcValues(true);
	}
	
	/**
	 * Create a waypoint by appending the waypoint to current waypoint
	 * @param waypoint Waypoint being appended
	 * @param position Geographical position of the new waypoint
	 * @return New appended waypoint
	 */
	public RouteWaypoint createWaypoint(RouteWaypoint waypoint, GeoLocation position){
		// Is the last waypoint
		RouteWaypoint wp = null;
		RouteLeg leg = null;
		if(waypoint.getOutLeg() == null){
			wp = new RouteWaypoint(waypoint);
			leg = new RouteLeg(waypoint.getInLeg());
			leg.setStartWp(waypoint);
			leg.setEndWp(wp);
			waypoint.setOutLeg(leg);
			wp.setInLeg(leg);
			wp.setOutLeg(null);
		} else if(waypoint.getInLeg() == null) {
			// TODO: Maybe a prepend functionality?
		} else {
			// TODO: Add new waypoint between two waypoints
		}
		
		wp.setPos(position);
		
		// Set name
		//wp.setName(name);
		// Get position
		
		
		// Get turn radius
		/*wp.setTurnRad(turnRad);
		
		// Get speed
		leg.setSpeed(speed);
		
		// Get heading
		leg.setHeading(heading);
		
		// Get XTD
		leg.setXtdStarboard(xtdStarboard);
		leg.setXtdPort(xtdPort);*/
		
		// Calculate rot
		wp.calcRot();
		
		return wp;
	}
	
	public void appendWaypoint() {
		RouteWaypoint lastWaypoint = waypoints.get(waypoints.size()-1);
		RouteWaypoint nextLastWaypoint = waypoints.get(waypoints.size()-2);
		GeoLocation startPoint = nextLastWaypoint.getPos();
		GeoLocation endPoint = lastWaypoint.getPos();
		
		//System.out.println("stalon :" + startPoint.getLongitude());
		//System.out.println("stalon :" + startPoint.getLatitude());
		
		//System.out.println("endlon :" + endPoint.getLongitude());
		//System.out.println("endlon :" + endPoint.getLatitude());
		
		double slope = (endPoint.getLatitude() - startPoint.getLatitude())/(endPoint.getLongitude() - startPoint.getLongitude());
		double dx = endPoint.getLongitude() -  startPoint.getLongitude();
		
		//System.out.println("slope: " + slope);
		//System.out.println("dx:    " + dx);
		
		double newX = endPoint.getLongitude() + dx;
		double newY = endPoint.getLatitude() + dx * slope;
		
		//System.out.println("newx:  " + newX);
		//System.out.println("newy:  " + newY);
		
		RouteWaypoint newWaypoint = createWaypoint(lastWaypoint, new GeoLocation(newY, newX));
		waypoints.add(newWaypoint);
		calcValues(true);
	}
	
	public boolean isPointWithingBBox(GeoLocation point) {
		if(waypoints == null || waypoints.size() == 0) {
			return false;
		}
		double minLat = 90;
		double maxLat = -90;
		double minLon = 180;
		double maxLon = -180;
		for (RouteWaypoint waypoint : waypoints) {
			GeoLocation location = waypoint.getPos();
			if(location.getLatitude() < minLat) {
				minLat = location.getLatitude();
			}
			if(location.getLatitude() > maxLat) {
				maxLat = location.getLatitude();
			}
			if(location.getLongitude() < minLon) {
				minLon = location.getLongitude();
			}
			if(location.getLongitude() > maxLon) {
				maxLon = location.getLongitude();
			}
		}
		
//		System.out.println("minLat: "+Formatter.latToPrintable(minLat)+"  maxLat: "+Formatter.latToPrintable(maxLat)+
//				" minLon: "+Formatter.lonToPrintable(minLon)+" maxLon: "+Formatter.lonToPrintable(maxLon));
		
		double pointLongitude = point.getLongitude();
		double pointLatitude = point.getLatitude();
		if(pointLongitude >= minLon && pointLongitude <= maxLon
				&& pointLatitude >= minLat && pointLatitude <= maxLat){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Route [departure=");
		builder.append(departure);
		builder.append(", destination=");
		builder.append(destination);
		builder.append(", name=");
		builder.append(name);
		builder.append(", visible=");
		builder.append(visible);
		builder.append(", waypoints=");
		builder.append(waypoints);
		builder.append("]");
		return builder.toString();
	}

}
