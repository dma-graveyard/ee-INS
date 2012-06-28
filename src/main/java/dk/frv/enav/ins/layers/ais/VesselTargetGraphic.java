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
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage;
import dk.frv.enav.common.xml.risk.response.Risk;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.AisIntendedRoute;
import dk.frv.enav.ins.ais.AisTarget;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselStaticData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.ais.VesselTargetSettings;
import dk.frv.enav.ins.common.graphics.RotationalPoly;
import dk.frv.enav.ins.common.math.Vector2D;

/**
 * Graphic for vessel target
 */
public class VesselTargetGraphic extends TargetGraphic {

	private static final long serialVersionUID = 1L;

	public static final float STROKE_WIDTH = 1.5f;

	private VesselTarget vesselTarget;

	private OMLine speedVector;
	private Font font = null;
	private OMText label = null;
	private double[] speedLL = new double[4];
	private LatLonPoint startPos = null;
	private LatLonPoint endPos = null;
	// private RotationalPoly vessel;
	private VesselTargetTriangle vessel;
	private RotationalPoly heading;
	private OMGraphicList marks = new OMGraphicList();
	private Vector2D pixelDist = new Vector2D();
	private Boolean marksVisible = false;
	private int[] markX = { -5, 5 };
	private int[] markY = { 0, 0 };
	private Paint paint;
	private Stroke stroke;
	private IntendedRouteGraphic routeGraphic = new IntendedRouteGraphic();
	private boolean showNameLabel = true;
	private boolean selected = false;

	public VesselTargetGraphic() {
		super();
		this.showNameLabel = EeINS.getSettings().getAisSettings()
				.isShowNameLabels();
	}

	private void createGraphics() {
		speedVector = new OMLine(0, 0, 0, 0, OMLine.LINETYPE_STRAIGHT);
		speedVector.setStroke(new BasicStroke(STROKE_WIDTH, // Width
				BasicStroke.CAP_SQUARE, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f, // Miter limit
				new float[] { 10.0f, 8.0f }, // Dash pattern
				0.0f) // Dash phase
				);

		speedVector.setLinePaint(new Color(74, 97, 205, 255));
		stroke = new BasicStroke(STROKE_WIDTH);
		paint = new Color(74, 97, 205, 255);
		/*
		 * int[] vesselX = {0,5,-5,0}; int[] vesselY = {-10,5,5,-10}; vessel =
		 * new RotationalPoly(vesselX, vesselY, stroke, paint);
		 */
		vessel = new VesselTargetTriangle();

		int[] headingX = { 0, 0 };
		int[] headingY = { 0, -100 };
		heading = new RotationalPoly(headingX, headingY, null, paint);

		font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
		label = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);

		add(label);
		add(0, vessel);
		add(speedVector);
		add(heading);
		add(marks);
		add(routeGraphic);
	}

	@Override
	public void update(AisTarget aisTarget) {
		vesselTarget = (VesselTarget) aisTarget;
		VesselPositionData posData = vesselTarget.getPositionData();
		VesselStaticData staticData = vesselTarget.getStaticData();
		VesselTargetSettings targetSettings = vesselTarget.getSettings();
		AisIntendedRoute aisIntendedRoute = vesselTarget.getAisRouteData();

		GeoLocation pos = posData.getPos();
		double trueHeading = posData.getTrueHeading();
		boolean noHeading = false;
		if (trueHeading == 511) {
			trueHeading = vesselTarget.getPositionData().getCog();
			noHeading = true;
		}

		double lat = pos.getLatitude();
		double lon = pos.getLongitude();

		if (size() == 0) {
			createGraphics();
		}

		// Set color based on risk index

		Risk risk = EeINS.getRiskHandler().getRiskLevel(vesselTarget.getMmsi());

		if (risk != null) {

			if (risk.getRiskNorm() < 0.002) {
				vessel.setLinePaint(Color.GREEN);
			} else if (risk.getRiskNorm() > 0.01) {
				vessel.setLinePaint(Color.RED);
			} else {
				System.out.println("risk :" + risk);
				int green = (int) ((0.01 - risk.getRiskNorm()) / 0.01 * 255);
				System.out.println(green);
				vessel.setLinePaint(new Color(255, green < 0 ? 255 : green, 51));
			}
		}
		

		if (selected){
			System.out.println("Selected target?");
			vessel.setLinePaint(Color.red);
		}
		
		double sog = vesselTarget.getPositionData().getSog();
		double cogR = Math.toRadians(vesselTarget.getPositionData().getCog());
		double hdgR = Math.toRadians(trueHeading);

		// vessel.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, hdgR);
		vessel.update(lat, lon, OMGraphic.DECIMAL_DEGREES, hdgR, this);
		heading.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, hdgR);
		if (noHeading) {
			heading.setVisible(false);
		}

		speedLL[0] = (float) pos.getLatitude();
		speedLL[1] = (float) pos.getLongitude();
		this.startPos = new LatLonPoint.Double(lat, lon);

		float length = (float) Length.NM.toRadians(EeINS.getSettings()
				.getAisSettings().getCogVectorLength()
				* (sog / 60.0));

		this.endPos = startPos.getPoint(length, cogR);
		speedLL[2] = endPos.getLatitude();
		speedLL[3] = endPos.getLongitude();
		speedVector.setLL(speedLL);

		// Do not show speed vector if moored
		// speedVector.setVisible(posData.getNavStatus() != 5);

		// Add minute marks
		marks.clear();
		for (int i = 1; i < 6; i++) {
			float newMarker = (float) Length.NM.toRadians(EeINS.getSettings()
					.getNavSettings().getCogVectorLength()
					/ 6 * i * (sog / 60.0));
			LatLonPoint marker = startPos.getPoint(newMarker, (float) cogR);
			RotationalPoly vtm = new RotationalPoly(markX, markY, stroke, paint);
			vtm.setLocation(marker.getLatitude(), marker.getLongitude(),
					OMGraphic.DECIMAL_DEGREES, cogR);
			marks.add(vtm);
		}

		if (!marksVisible)
			marks.setVisible(false);

		// Set label
		label.setLat(lat);
		label.setLon(lon);
		if (trueHeading > 90 && trueHeading < 270) {
			label.setY(-10);
		} else {
			label.setY(20);
		}

		// Determine name
		String name;
		if (staticData != null) {
			name = AisMessage.trimText(staticData.getName());
		} else {
			Long mmsi = vesselTarget.getMmsi();
			name = "ID:" + mmsi.toString();
		}
		label.setData(name);

		if (showNameLabel) {
			label.setVisible(true);
		} else {
			label.setVisible(false);
		}
		// Intended route graphic
		routeGraphic.update(vesselTarget, name, aisIntendedRoute, pos);
		if (!targetSettings.isShowRoute()) {
			routeGraphic.setVisible(false);
		}
	}

	public void setMarksVisible(Projection projection) {
		if (startPos != null && endPos != null) {
			Point2D start = projection.forward(startPos);
			Point2D end = projection.forward(endPos);
			pixelDist.setValues(start.getX(), start.getY(), end.getX(),
					end.getY());
			if (pixelDist.norm() < EeINS.getSettings().getAisSettings()
					.getShowMinuteMarksAISTarget()) {
				marksVisible = false;
				marks.setVisible(false);
			} else {
				marksVisible = true;
				marks.setVisible(true);
			}
		}
	}

	public VesselTarget getVesselTarget() {
		return vesselTarget;
	}

	public void setShowNameLabel(boolean showNameLabel) {
		this.showNameLabel = showNameLabel;
	}

	public boolean getShowNameLabel() {
		return showNameLabel;
	}

	public IntendedRouteGraphic getRouteGraphic() {
		return routeGraphic;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
