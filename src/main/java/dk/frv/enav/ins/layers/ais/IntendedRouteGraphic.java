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
package dk.frv.enav.ins.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.ais.AisIntendedRoute;
import dk.frv.enav.ins.ais.VesselTarget;

/**
 * Graphic for intended route
 */
public class IntendedRouteGraphic extends OMGraphicList {
	private static final long serialVersionUID = 1L;
	
	private AisIntendedRoute previousData = null;
	private IntendedRouteLegGraphic activeWpLine;
	private double[] activeWpLineLL = new double[4];
	private Color legColor = new Color(42, 172, 12, 255);
	private String name;
	private boolean arrowsVisible = false;

	private VesselTarget vesselTarget;
	
	private List<IntendedRouteLegGraphic> routeLegs = new ArrayList<IntendedRouteLegGraphic>();
	
	public IntendedRouteGraphic() {
		super();
		GeoLocation nullGeoLocation = new GeoLocation(0, 0);
		activeWpLine = new IntendedRouteLegGraphic(0, this, true, nullGeoLocation, nullGeoLocation, legColor);
		setVisible(false);
	}
	
	private void makeLegLine(int index, GeoLocation start, GeoLocation end) {
		IntendedRouteLegGraphic leg = new IntendedRouteLegGraphic(index, this, false, start, end, legColor);
		routeLegs.add(leg);
		add(leg);
	}
	
	private void makeWpCircle(int index, GeoLocation wp) {
		IntendedRouteWpCircle wpCircle = new IntendedRouteWpCircle(this, index, wp.getLatitude(), wp.getLongitude(), 0, 0,18, 18);
		wpCircle.setStroke(new BasicStroke(3));
		wpCircle.setLinePaint(legColor);
		add(wpCircle);
	}
	
	public void update(VesselTarget vesselTarget, String label, AisIntendedRoute routeData, GeoLocation pos) {
		this.vesselTarget = vesselTarget;
		this.name = label;
		// Handle no or empty route
		if (routeData == null || routeData.getWaypoints().size() == 0) {
			clear();
			if (isVisible()) {
				setVisible(false);
			}
			previousData = null;
			return;
		}
		
		if (previousData != routeData) {
			// Route has changed, draw new route
			clear();
			add(activeWpLine);
			List<GeoLocation> waypoints = routeData.getWaypoints();
			// Make first WP circle
			makeWpCircle(0, waypoints.get(0));
			for (int i=0; i < waypoints.size() - 1; i++) {
				GeoLocation start = waypoints.get(i);
				GeoLocation end = waypoints.get(i + 1);
				
				// Make wp circle
				makeWpCircle(i + 1, end);
				
				// Make leg line
				makeLegLine(i + 1, start, end);
			}
			previousData = routeData;
		}
		
		// Update leg to first waypoint
		GeoLocation activeWpPos = routeData.getWaypoints().get(0);
		activeWpLineLL[0] = pos.getLatitude();
		activeWpLineLL[1] = pos.getLongitude();
		activeWpLineLL[2] = activeWpPos.getLatitude();
		activeWpLineLL[3] = activeWpPos.getLongitude();
		activeWpLine.setLL(activeWpLineLL);
		
		// Set visible if not visible
		if (!isVisible()) {
			setVisible(true);
		}
		
	}

	public VesselTarget getVesselTarget() {
		return vesselTarget;
	}
	
	public String getName() {
		return name;
	}
	
	public void showArrowHeads(boolean show){
		if(this.arrowsVisible != show){
			for (IntendedRouteLegGraphic routeLeg : routeLegs) {
				routeLeg.setArrows(show);
			}
			this.arrowsVisible = show;
		}
	}
	
}