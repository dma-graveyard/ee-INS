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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.ais.AisAdressedRouteSuggestion;

/**
 * Graphic for a suggested route
 */
public class SuggestedRouteGraphic extends OMGraphicList {

	private static final long serialVersionUID = 1L;

	private List<GeoLocation> routeWaypoints;
	private AisAdressedRouteSuggestion routeSuggestion;
	private Stroke stroke;

	public SuggestedRouteGraphic(AisAdressedRouteSuggestion routeSuggestion, Stroke stroke) {
		this.routeSuggestion = routeSuggestion;
		this.stroke = stroke;
		routeWaypoints = routeSuggestion.getWaypoints();
		initGraphics();
		setVague(true);
	}

	public void initGraphics() {

		Stroke backgroundStroke = new BasicStroke(
				10.0f, // Width
				BasicStroke.CAP_ROUND, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f, // Miter limit
				null, // Dash pattern
				0.0f);

		GeoLocation prevPoint = null;
		GeoLocation nextPoint = null;
		for (GeoLocation geoLocation : routeWaypoints) {
			nextPoint = geoLocation;
			if (prevPoint != null) {
				OMLine leg = new OMLine(prevPoint.getLatitude(), prevPoint.getLongitude(), nextPoint.getLatitude(), nextPoint
						.getLongitude(), OMLine.LINETYPE_RHUMB);
				leg.setStroke(stroke);
				leg.setLinePaint(new Color(183, 68, 237, 255));
				add(leg);
				
				if (!routeSuggestion.isReplied()) {
					OMLine legBackground = new OMLine(prevPoint.getLatitude(), prevPoint.getLongitude(), nextPoint.getLatitude(),
							nextPoint.getLongitude(), OMLine.LINETYPE_RHUMB);
					legBackground.setStroke(backgroundStroke);
					legBackground.setLinePaint(new Color(42, 172, 12, 120));				
					add(legBackground);
				}
			}
			prevPoint = nextPoint;
		}
	}

	public AisAdressedRouteSuggestion getRouteSuggestion() {
		return routeSuggestion;
	}

	@Override
	public void render(Graphics gr) {
		Graphics2D image = (Graphics2D) gr;
		image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.render(image);
	}
}
