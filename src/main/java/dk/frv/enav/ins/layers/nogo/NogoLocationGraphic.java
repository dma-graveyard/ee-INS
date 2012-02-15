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
package dk.frv.enav.ins.layers.nogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.Date;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.omGraphics.OMPoly;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

/**
 * Graphic for MSI location/area
 */
public class NogoLocationGraphic extends OMGraphicList {
	private static final long serialVersionUID = 1L;

	private NogoPolygon polygon;
	private Date validFrom;
	private Date validTo;
	private int draught;
	private String message;
	private int errorCode;
	private GeoLocation northWest;
	private GeoLocation southEast;

	private Color nogoColor = Color.red;

	private Rectangle hatchFillRectangle;
	private BufferedImage hatchFill;

	public NogoLocationGraphic(NogoPolygon polygon, Date validFrom, Date validTo, Double draught, String message,
			GeoLocation northWest, GeoLocation southEast, int errorCode) {
		super();
		this.polygon = polygon;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.draught = Math.abs(draught.intValue());
		this.message = message;
		this.northWest = northWest;
		this.southEast = southEast;
		this.errorCode = errorCode;

		// System.out.println(message);
		// Draw the data
		if (polygon != null && (errorCode == 18 || errorCode == 0) ) {
			hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			Graphics2D big = hatchFill.createGraphics();
			Composite originalComposite = big.getComposite();
			big.setComposite(makeComposite(0.2f));
			big.setColor(nogoColor);
			big.drawLine(0, 0, 10, 10);

			hatchFillRectangle = new Rectangle(0, 0, 10, 10);
			big.setComposite(originalComposite);

			drawAreaBox();
			// drawPolyline();
			drawPolygon();
			// drawPoints();

		}
		// Draw the message
		if (errorCode == -1 || errorCode == 1 || errorCode == 17) {
			OMPoint polyPoint = new OMPoint(0, 0);

			if (errorCode == 1) {
				// Standby message
				drawAreaBox();
			}

			// polyPoint.setVisible(false);
			add(polyPoint);
		}
		

	}

	private AlphaComposite makeComposite(float alpha) {
		int type = AlphaComposite.SRC_OVER;
		return (AlphaComposite.getInstance(type, alpha));
	}

	@SuppressWarnings("unused")
	private void drawPolyline() {
		// space for lat-lon points plus first lat-lon pair to close the polygon
		double[] polyPoints = new double[polygon.getPolygon().size() * 2];
		int j = 0;
		for (int i = 0; i < polygon.getPolygon().size(); i++) {
			polyPoints[j] = polygon.getPolygon().get(i).getLat();
			polyPoints[j + 1] = polygon.getPolygon().get(i).getLon();
			j += 2;
		}
		OMPoly poly = new OMPoly(polyPoints, OMGraphic.DECIMAL_DEGREES, OMGraphic.LINETYPE_RHUMB, 1);

		poly.setLinePaint(nogoColor);
		poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

		poly.setIsPolygon(true);

		add(poly);

	}

	@SuppressWarnings("unused")
	private void drawPoints() {
		for (int i = 0; i < polygon.getPolygon().size(); i++) {
			OMPoint polyPoint = new OMPoint(polygon.getPolygon().get(i).getLat(), polygon.getPolygon().get(i).getLon());

			polyPoint.setLinePaint(nogoColor);
			polyPoint.setFillPaint(new Color(0, 0, 0, 10));
			polyPoint.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));
			add(polyPoint);
		}

	}

	private void drawPolygon() {
		// space for lat-lon points plus first lat-lon pair to close the polygon
		double[] polyPoints = new double[polygon.getPolygon().size() * 2 + 2];
		int j = 0;
		for (int i = 0; i < polygon.getPolygon().size(); i++) {
			polyPoints[j] = polygon.getPolygon().get(i).getLat();
			polyPoints[j + 1] = polygon.getPolygon().get(i).getLon();
			j += 2;
		}
		polyPoints[j] = polyPoints[0];
		polyPoints[j + 1] = polyPoints[1];
		OMPoly poly = new OMPoly(polyPoints, OMGraphic.DECIMAL_DEGREES, OMGraphic.LINETYPE_RHUMB, 1);
		poly.setLinePaint(clear);
		poly.setFillPaint(new Color(0, 0, 0, 1));
		poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

		add(poly);

	}

	private void drawAreaBox() {
		// space for lat-lon points plus first lat-lon pair to close the polygon

		// Four lines are needed

		double[] westernLine = new double[4];
		westernLine[0] = northWest.getLatitude();
		westernLine[1] = northWest.getLongitude();
		westernLine[2] = southEast.getLatitude();
		westernLine[3] = northWest.getLongitude();

		OMPoly poly = new OMPoly(westernLine, OMGraphic.DECIMAL_DEGREES, OMGraphic.LINETYPE_RHUMB, 1);

		double[] easternLine = new double[4];
		easternLine[0] = northWest.getLatitude();
		easternLine[1] = southEast.getLongitude();
		easternLine[2] = southEast.getLatitude();
		easternLine[3] = southEast.getLongitude();

		OMPoly poly1 = new OMPoly(easternLine, OMGraphic.DECIMAL_DEGREES, OMGraphic.LINETYPE_RHUMB, 1);

		double[] northernLine = new double[4];
		northernLine[0] = northWest.getLatitude();
		northernLine[1] = northWest.getLongitude();
		northernLine[2] = northWest.getLatitude();
		northernLine[3] = southEast.getLongitude();

		OMPoly poly2 = new OMPoly(northernLine, OMGraphic.DECIMAL_DEGREES, OMGraphic.LINETYPE_RHUMB, 1);

		double[] southernLine = new double[4];
		southernLine[0] = southEast.getLatitude();
		southernLine[1] = northWest.getLongitude();
		southernLine[2] = southEast.getLatitude();
		southernLine[3] = southEast.getLongitude();

		OMPoly poly3 = new OMPoly(southernLine, OMGraphic.DECIMAL_DEGREES, OMGraphic.LINETYPE_RHUMB, 1);

		add(poly);
		add(poly1);
		add(poly2);
		add(poly3);

	}

	@Override
	public void render(Graphics gr) {

		Graphics2D image = (Graphics2D) gr;
		image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.render(image);
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

		String message0 = "NoGo Active, only valid from " + validFrom + " to " + validTo;
		String message1 = "Do not use this for navigational purposes!";
		String message2 = "Only valid for draughts at " + draught + " meters and below";

		String messageTide = "NoGo Active, no Tide Data found showing only static depth";
		
		gr.setFont(font);
		gr.setColor(Color.red);

		
		//Errorcode -1 means server experinced a timeout
		//Errorcode 0 means everything went ok
		//Errorcode 1 is the standby message
		//Errorcode 17 means no data
		//Errorcode 18 means no tide data

		
		if (errorCode == 0){
			gr.drawString(message0, 5, 20);
			gr.drawString(message1, 5, 40);
			gr.drawString(message2, 5, 60);
		}
		
		if (errorCode == 18){
			gr.drawString(messageTide, 5, 20);
			gr.drawString(message1, 5, 40);
			gr.drawString(message2, 5, 60);
		}
		
		
		if (errorCode == -1 || errorCode == 1 || errorCode == 17){
			gr.drawString(message, 5, 20);

		}

	}

}
