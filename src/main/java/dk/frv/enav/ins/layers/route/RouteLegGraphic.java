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
package dk.frv.enav.ins.layers.route;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMArrowHead;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;

import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.route.RouteLeg;
import dk.frv.enav.ins.route.RouteWaypoint;

/**
 * Graphic for a route leg 
 */
public class RouteLegGraphic extends OMGraphicList {

	private static final long serialVersionUID = 1L;
	
	private RouteLeg routeLeg;
	private OMLine line = null;
	private OMArrowHead arrow = new OMArrowHead(OMArrowHead.ARROWHEAD_DIRECTION_FORWARD, 55, 5, 15);
	private Color color;

	private int routeIndex;
	
	
	/**
	 * Creates a route leg
	 * @param routeLeg RouteLeg object containing information about the route leg
	 * @param routeIndex TODO
	 * @param color Color of the route leg
	 * @param stroke Stroke type of the route leg
	 */
	public RouteLegGraphic(RouteLeg routeLeg, int routeIndex, Color color, Stroke stroke) {
		super();
		this.routeIndex = routeIndex;
		this.routeLeg = routeLeg;
		this.color = color;
		this.stroke = stroke;
		this.setVague(true);
		initGraphics();
	}
	
	public void initGraphics() {
		if(routeLeg.getEndWp() != null){
			RouteWaypoint legStart = routeLeg.getStartWp(); 
			RouteWaypoint legEnd = routeLeg.getEndWp();
			
			double startLat = legStart.getPos().getLatitude();
			double startLon = legStart.getPos().getLongitude();
			
			double endLat = legEnd.getPos().getLatitude();
			double endLon = legEnd.getPos().getLongitude();
			
			if(routeLeg.getHeading() == Heading.GC){
				lineType = LINETYPE_GREATCIRCLE;
			} else if (routeLeg.getHeading() == Heading.RL) {
				lineType = LINETYPE_RHUMB;
			}
			
			line = new OMLine(startLat, startLon, endLat, endLon, lineType);
			line.setLinePaint(color);
			line.setStroke(stroke);
			
			add(line);
		}
	}
	
	public void setArrows(boolean arrowsVisible){
		if(!arrowsVisible)
			line.setArrowHead(null);
		else
			line.setArrowHead(arrow);
	}
	
	@Override
	public void render(Graphics gr) {
		Graphics2D image = (Graphics2D) gr;
		image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.render(image);
	}
	
	public int getRouteIndex() {
		return routeIndex;
	}
	
	public RouteLeg getRouteLeg() {
		return routeLeg;
	}
}
