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
package dk.frv.enav.ins.route;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.common.graphics.CenterRaster;
import dk.frv.enav.ins.common.util.Calculator;

public class SafeHavenArea extends OMGraphicList {
	private static final long serialVersionUID = 1L;

	CenterRaster selectionGraphics;

	ImageIcon targetImage;
	int imageWidth;
	int imageHeight;

	private List<GeoLocation> polygon;
	private Rectangle hatchFillRectangle;
	private BufferedImage hatchFill;
	OMPoly poly;

	public SafeHavenArea() {
		super();

		hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D big = hatchFill.createGraphics();
		Composite originalComposite = big.getComposite();
		big.setComposite(makeComposite(0.7f));
		big.setColor(Color.green);
		big.drawLine(0, 0, 10, 10);

		hatchFillRectangle = new Rectangle(0, 0, 10, 10);
		big.setComposite(originalComposite);

		this.polygon = new ArrayList<GeoLocation>();

	}

	private void drawPolygon() {
		// space for lat-lon points plus first lat-lon pair to close the polygon
		double[] polyPoints = new double[polygon.size() * 2 + 2];
		int j = 0;
		for (int i = 0; i < polygon.size(); i++) {
			polyPoints[j] = polygon.get(i).getLatitude();
			polyPoints[j + 1] = polygon.get(i).getLongitude();
			j += 2;
		}
		polyPoints[j] = polyPoints[0];
		polyPoints[j + 1] = polyPoints[1];
		poly = new OMPoly(polyPoints, OMGraphic.DECIMAL_DEGREES,
				OMGraphic.LINETYPE_RHUMB, 1);
		// poly.setLinePaint(clear);
		poly.setFillPaint(new Color(0, 0, 0, 1));
		poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

		
		Stroke activeStroke = new BasicStroke(1.0f, // Width
				BasicStroke.CAP_SQUARE, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f, // Miter limit
				new float[] { 10.0f, 8.0f }, // Dash pattern
				0.0f); // Dash phase
		
		poly.setStroke(activeStroke);
		
		add(poly);
	}

	private AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}

	public void moveSymbol(GeoLocation pos, double bearing) {
		
		remove(poly);

		int width = 100;
		int height = 50;
		
		// Create the polygon around the position.
		calculatePolygon(pos, bearing, width, height);

		// createGraphics();
		drawPolygon();

	}

	private void calculatePolygon(GeoLocation position, double bearing,
			int width, int height) {
//		double withNm = Converter.nmToMeters(width/2);
//		double heightNm = Converter.nmToMeters(height/2);

		double angle = 90 + bearing;
		double oppositeBearing = 180 + bearing;
		
		
		GeoLocation topLinePt = Calculator
				.findPosition(position, bearing,
						width/2);
		

		if (angle < 360){
			angle = angle + 360;
		}
		
		if (oppositeBearing > 360){
			oppositeBearing = oppositeBearing - 360;
		}
		
		
		GeoLocation bottomLinePt = Calculator
				.findPosition(position, oppositeBearing,
						width/2);

//		System.out.println("Top pnt: " + topLinePt);
//		System.out.println("Btm pnt: " + bottomLinePt);
		
		
		GeoLocation point1 = Calculator
				.findPosition(bottomLinePt, angle,
						height/2);
		
		GeoLocation point2 = Calculator
				.findPosition(topLinePt, angle,
						height/2);
		
		GeoLocation point3 = Calculator
				.findPosition(bottomLinePt, angle + 180,
						height/2);
		

		
		GeoLocation point4 = Calculator
				.findPosition(topLinePt, angle + 180,
						height/2);
	
		polygon.clear();

//		polygon.add(topLinePt);
//		polygon.add(bottomLinePt);
		
		
		
		polygon.add(point1);

		polygon.add(point2);

		polygon.add(point4);
		polygon.add(point3);	

		
		
	}

	public void removeSymbol() {
		remove(selectionGraphics);
	}

}
