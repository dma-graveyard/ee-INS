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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dk.frv.ais.message.binary.RouteInformation;

/**
 * Class representing an AIS intended route
 */
public class AisIntendedRoute extends AisRouteData {
	private static final long serialVersionUID = 1L;
			
	protected List<Date> etas = new ArrayList<Date>();	
	protected Double speed = null;
	protected Double activeWpRange = null;
	
	/**
	 * Copy constructor
	 * @param routeData
	 */
	public AisIntendedRoute(AisIntendedRoute routeData) {
		super(routeData);
		this.etas = routeData.etas;
		this.speed = routeData.speed;
		this.activeWpRange = routeData.activeWpRange;
	}
	
	/**
	 * Constructor given AIS route information
	 * @param routeInformation
	 */
	public AisIntendedRoute(RouteInformation routeInformation) {
		super(routeInformation);
		if (duration == 0) {
			// Cancel route
			return;
		}
		
		if (waypoints.size() == 0) {
			return;
		}
		
		// Calculate avg speed
		speed = ranges.get(waypoints.size() - 1) / ((double)routeInformation.getDuration() / 60.0);
		
		// ETA's
		long start = etaFirst.getTime();
		etas.add(etaFirst);
		for (int i=0; i < waypoints.size() - 1; i++) {
			double dist = ranges.get(i + 1) - ranges.get(i);
			double dur = (dist / speed) * 60 * 60 * 1000;
			start += dur;
			etas.add(new Date(start));
		}
		
	}
		
	/**
	 * Update range to active WP given the targets new position
	 * @param posData
	 */
	public void update(VesselPositionData posData) {
		if (posData == null || posData.getPos() == null || waypoints.size() == 0) {
			return;
		}
		
		// Range to first wp
		activeWpRange = posData.getPos().getRhumbLineDistance(waypoints.get(0)) / 1852.0;
	}
	
	public Double getRange(int index) {
		if (activeWpRange == null) {
			return null;
		}
		return activeWpRange + ranges.get(index);
	}
	
	public Date getEta(int index) {
		if (index >= etas.size()) {
			return null;
		}
		return etas.get(index);
	}
		
	public Double getSpeed() {
		return speed;
	}
	
}
