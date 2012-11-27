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
package dk.frv.enav.ins.layers.msi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoly;
import com.bbn.openmap.proj.Length;

import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.MsiPoint;

/**
 * Graphic for MSI location/area 
 */
public class MsiLocationGraphic extends OMGraphicList {
	private static final long serialVersionUID = 1L;
	
	private static final int LOWER_RADIUS_LIMIT = 100; // meters
	
	private MsiMessage msiMessage;
	private Color msiColor = new Color(183, 68, 237, 150);

	private Rectangle hatchFillRectangle;
	private BufferedImage hatchFill;
	
	public MsiLocationGraphic(MsiMessage msiMessage) {
		super();
		this.msiMessage = msiMessage;
		MsiLocation msiLocation = msiMessage.getLocation();
		
		hatchFill = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		Graphics2D big = hatchFill.createGraphics();
		big.setColor(msiColor);
	    big.drawLine(0, 0, 10, 10);
		hatchFillRectangle = new Rectangle(0, 0, 10, 10);
		
		switch (msiLocation.getLocationType()) {
		case POINT:
		case POINTS:
			/*
			 * For each point that has radius > minlimit
			 * Draw pink shaded circle with radius   
			 */
			for (MsiPoint point : msiLocation.getPoints()) {
				drawCircle(point);
			}
			break;
		case POLYGON:
			/*
			 * Draw pink shaded polygon
			 */
			drawPolygon();
			break;
		case POLYLINE:
			/*
			 * Draw pink shaded polygon defined by polyline points and 
			 * radius  
			 */
			drawPolyline();
			break;
		default:
			break;
		}
	}
	
	private void drawCircle(MsiPoint point) {
		if (point.getRadius() < LOWER_RADIUS_LIMIT) {
			return;
		}
		OMCircle radiusCircle = new OMCircle(point.getLatitude(), point.getLongitude(), point.getRadius(), Length.METER);
		radiusCircle.setLinePaint(msiColor);
		radiusCircle.setFillPaint(new Color(0, 0, 0, 1));
		radiusCircle.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));
		add(radiusCircle);
	}
	
	private void drawPolygon() {
		MsiLocation msiLocation = msiMessage.getLocation();
		// space for lat-lon points plus first lat-lon pair to close the polygon
		double[] polyPoints = new double[msiLocation.getPoints().size() * 2 + 2];
		int i = 0;
		for (MsiPoint point : msiLocation.getPoints()) {
			polyPoints[i] = point.getLatitude();
			polyPoints[i+1] = point.getLongitude();
			i+=2;
		}
		polyPoints[i] = polyPoints[0];
		polyPoints[i+1] = polyPoints[1];
		OMPoly poly = new OMPoly(polyPoints, OMGraphic.DECIMAL_DEGREES, OMGraphic.LINETYPE_RHUMB, 1);
		poly.setLinePaint(msiColor);
		poly.setFillPaint(new Color(0, 0, 0, 1));
		poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));
		add(poly);
	}
	
	private void drawPolyline() {
		MsiLocation msiLocation = msiMessage.getLocation();
		double[] polyPoints = new double[msiLocation.getPoints().size() * 2];
		int i = 0;
		for (MsiPoint point : msiLocation.getPoints()) {
			drawCircle(point);
			polyPoints[i] = point.getLatitude();
			polyPoints[i+1] = point.getLongitude();
			i+=2;
		}
		OMPoly poly = new OMPoly(polyPoints, OMGraphic.DECIMAL_DEGREES, OMGraphic.LINETYPE_RHUMB, 1);
		poly.setLinePaint(msiColor);
		add(poly);
	}
	
	@Override
	public void render(Graphics gr) {
		Graphics2D image = (Graphics2D) gr;
		image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.render(image);
	}
}
