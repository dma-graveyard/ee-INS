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

	private Color nogoColor = Color.red;

	private Rectangle hatchFillRectangle;
	private BufferedImage hatchFill;

	public NogoLocationGraphic(NogoPolygon polygon, Date validFrom,
			Date validTo, Double draught, String message) {
		super();
		this.polygon = polygon;
		this.validFrom = validFrom;
		this.validTo = validTo;
		this.draught = Math.abs(draught.intValue());
		this.message = message;

		System.out.println(message);
		if (polygon != null && message.equals("")) {
			hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			Graphics2D big = hatchFill.createGraphics();
			Composite originalComposite = big.getComposite();
			big.setComposite(makeComposite(0.2f));
			big.setColor(nogoColor);
			big.drawLine(0, 0, 10, 10);

			hatchFillRectangle = new Rectangle(0, 0, 10, 10);
			big.setComposite(originalComposite);

			// drawPolyline();
			drawPolygon();
			// drawPoints();

		}
		if (!message.equals("")) {
			OMPoint polyPoint = new OMPoint(0, 0);
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
		OMPoly poly = new OMPoly(polyPoints, OMGraphic.DECIMAL_DEGREES,
				OMGraphic.LINETYPE_RHUMB, 1);

		poly.setLinePaint(nogoColor);
		poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

		poly.setIsPolygon(true);

		add(poly);

	}

	@SuppressWarnings("unused")
	private void drawPoints() {
		for (int i = 0; i < polygon.getPolygon().size(); i++) {
			OMPoint polyPoint = new OMPoint(polygon.getPolygon().get(i)
					.getLat(), polygon.getPolygon().get(i).getLon());

			polyPoint.setLinePaint(nogoColor);
			polyPoint.setFillPaint(new Color(0, 0, 0, 10));
			polyPoint.setTextureMask(new TexturePaint(hatchFill,
					hatchFillRectangle));
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
		OMPoly poly = new OMPoly(polyPoints, OMGraphic.DECIMAL_DEGREES,
				OMGraphic.LINETYPE_RHUMB, 1);
		poly.setLinePaint(clear);
		poly.setFillPaint(new Color(0, 0, 0, 1));
		poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

		add(poly);

	}

	@Override
	public void render(Graphics gr) {

		Graphics2D image = (Graphics2D) gr;
		image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		super.render(image);
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

		String message0 = "NoGo Active, only valid from " + validFrom + " to "
				+ validTo;
		String message1 = "Do not use this for navigational purposes!";
		String message2 = "Only valid for draughts at " + draught + " and below";

		gr.setFont(font);
		gr.setColor(Color.red);
		

		if (message.equals("")) {
			gr.drawString(message0, 5, 20);
			gr.drawString(message1, 5, 40);
			gr.drawString(message2, 5, 60);
		} else {
			gr.drawString(message, 5, 20);

		}

	}

}
