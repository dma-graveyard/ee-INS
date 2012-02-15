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
package dk.frv.enav.ins.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;

import com.bbn.openmap.omGraphics.OMArrowHead;
import com.bbn.openmap.omGraphics.OMLine;

import dk.frv.ais.geo.GeoLocation;

/**
 * Graphic for intended route leg graphic
 */
public class IntendedRouteLegGraphic extends OMLine {
	
	private static final long serialVersionUID = 1L;
	
	private IntendedRouteGraphic intendedRouteGraphic;
	private OMArrowHead arrow = new OMArrowHead(OMArrowHead.ARROWHEAD_DIRECTION_FORWARD, 55, 5, 15);
	private int index;

	public IntendedRouteLegGraphic(int index, IntendedRouteGraphic intendedRouteGraphic, boolean activeWaypoint, GeoLocation start,
			GeoLocation end, Color legColor) {
		
		super(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude(), LINETYPE_RHUMB);
		this.index = index;
		this.intendedRouteGraphic = intendedRouteGraphic;
		if(activeWaypoint){
			setStroke(new BasicStroke(2.0f, // Width
					BasicStroke.CAP_SQUARE, // End cap
					BasicStroke.JOIN_MITER, // Join style
					10.0f, // Miter limit
					new float[] { 3.0f, 10.0f }, // Dash pattern
					0.0f)); // Dash phase)
		} else {
			setStroke(new BasicStroke(3.0f, // Width
					BasicStroke.CAP_SQUARE, // End cap
					BasicStroke.JOIN_MITER, // Join style
					10.0f, // Miter limit
					new float[] { 10.0f, 8.0f }, // Dash pattern
					0.0f)); // Dash phase)
		}
		setLinePaint(legColor);		
	}

	public IntendedRouteGraphic getIntendedRouteGraphic() {
		return intendedRouteGraphic;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setArrows(boolean arrowsVisible){
		if(!arrowsVisible)
			this.setArrowHead(null);
		else
			this.setArrowHead(arrow);
	}
}
