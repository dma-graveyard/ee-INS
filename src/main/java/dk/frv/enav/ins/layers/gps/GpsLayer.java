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
package dk.frv.enav.ins.layers.gps;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Date;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.VesselAisHandler;
import dk.frv.enav.ins.ais.VesselPositionData;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.common.graphics.RotationalPoly;
import dk.frv.enav.ins.common.math.Vector2D;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gps.IGpsDataListener;

public class GpsLayer extends OMGraphicHandlerLayer implements IGpsDataListener {
	
	private static final long serialVersionUID = 1L;
	
	private static final float STROKE_WIDTH = 1.5f;
	
	private GpsHandler gpsHandler = null;
	private VesselAisHandler vesselAisHandler = null;
	
	private long minRedrawInterval = 5 * 1000; // 5 sec
	
	private Date lastRedraw = null;
	private GpsData gpsData = null;
	private OMGraphicList graphics = new OMGraphicList();
	private OMCircle circle1; 
	private OMCircle circle2;
	private OMLine speedVector;
	private RotationalPoly angularVector;
	private RotationalPoly directionVector;
	private RotationalPoly frontShipArrow;
	private RotationalPoly backShipArrow;
	private OMGraphicList marks;
	private LatLonPoint endPos;
	private LatLonPoint startPos;
	private Vector2D vector = new Vector2D();
	private int[] markX = {-5,5};
	private int[] markY = {0,0};

	private double headingRadian;
	private GeoLocation lastPos;
	private GeoLocation currentPos;

	public GpsLayer() {
		graphics.setVague(true);
		Stroke stroke = new BasicStroke(STROKE_WIDTH);
		circle1 = new OMCircle(0, 0, 0, 0, 18, 18);
		circle2 = new OMCircle(0, 0, 0, 0, 8, 8);
		circle1.setStroke(stroke);
		circle2.setStroke(stroke);
		speedVector = new OMLine(0d, 0d, 0d, 0d, OMLine.LINETYPE_STRAIGHT);
		speedVector.setStroke(new BasicStroke(
				STROKE_WIDTH,                      // Width
                BasicStroke.CAP_SQUARE,    // End cap
                BasicStroke.JOIN_MITER,    // Join style
                10.0f,                     // Miter limit
                new float[] { 10.0f, 8.0f }, // Dash pattern
                0.0f)                     // Dash phase
		);
		marks = new OMGraphicList();
		
		int[] angularX = {-20,20};
		int[] angularY = {0,0};
		angularVector = new RotationalPoly(angularX, angularY, null, null);
		int[] directionX = {0,0};
		int[] directionY = {0,-200};
		directionVector = new RotationalPoly(directionX, directionY, stroke, null);
		int[] frontArrowX = {5,0,-5};
		int[] frontArrowY = {10,0,10};
		frontShipArrow = new RotationalPoly(frontArrowX, frontArrowY, stroke, null);
		int[] backArrowX = {5,0,-5};
		int[] backArrowY = {20,10,20};
		backShipArrow = new RotationalPoly(backArrowX, backArrowY, stroke, null);
	}
	
	private synchronized boolean doUpdate() {
		if (this.gpsData == null || lastRedraw == null || lastPos == null) {
			return true;
		}
		
		long elapsed = (new Date()).getTime() - lastRedraw.getTime();
		if (elapsed > minRedrawInterval) {
			return true;
		}
		
		// Check distance moved
		double dist = currentPos.getRhumbLineDistance(lastPos);
		//System.out.println("dist: " + dist);
		if (dist > 10) { // 10 m
			return true;
		}
		
		return false;
	}
	
	@Override
	public synchronized void gpsDataUpdate(GpsData gpsData) {
		if (gpsData == null || gpsData.getPosition() == null) {
			return;
		}
		if (this.gpsData == null) {
			graphics.add(circle1);
			graphics.add(circle2);
			graphics.add(speedVector);
			graphics.add(marks);
			graphics.add(backShipArrow);
			graphics.add(frontShipArrow);
			graphics.add(angularVector);
			graphics.add(directionVector);
		}
		
		this.gpsData = gpsData;
		
		double heading = 0;
		if (gpsData.getCog() != null) {
			heading = gpsData.getCog();
		}
		
		VesselTarget ownShip = null;
		VesselPositionData ownShipData = null;
		if (vesselAisHandler != null) {
			ownShip = vesselAisHandler.getOwnShip();
		}
		
		if (ownShip != null) {
			ownShipData = ownShip.getPositionData();
			if(ownShipData != null && ownShipData.getTrueHeading() <= 360){
				heading = ownShipData.getTrueHeading();
			}
		}
		
		headingRadian = Math.toRadians(heading);
		
		// Set location of ship
		currentPos = gpsData.getPosition();		
		circle1.setLatLon(currentPos.getLatitude(), currentPos.getLongitude());
		circle2.setLatLon(currentPos.getLatitude(), currentPos.getLongitude());
		
		// Calculate speed vector
		if (gpsData.getCog() != null && gpsData.getSog() != null) {
			startPos = new LatLonPoint.Double(currentPos.getLatitude(), currentPos.getLongitude());
			float length = (float) Length.NM.toRadians(EeINS.getSettings().getNavSettings().getCogVectorLength() * (gpsData.getSog() / 60.0));
			endPos = startPos.getPoint(length, (float) ProjMath.degToRad(gpsData.getCog()));
			double[] newLLPos = {startPos.getLatitude(), startPos.getLongitude(), endPos.getLatitude(), endPos.getLongitude()};
			Double cogRadian = Math.toRadians(gpsData.getCog());
			
			speedVector.setLL(newLLPos);
			angularVector.setLocation(startPos.getLatitude(), startPos.getLongitude(), OMGraphic.DECIMAL_DEGREES,headingRadian);
			directionVector.setLocation(startPos.getLatitude(), startPos.getLongitude(), OMGraphic.DECIMAL_DEGREES, headingRadian);
			frontShipArrow.setLocation(endPos.getLatitude(), endPos.getLongitude(), OMGraphic.DECIMAL_DEGREES, cogRadian);
			backShipArrow.setLocation(endPos.getLatitude(), endPos.getLongitude(), OMGraphic.DECIMAL_DEGREES, cogRadian);
			
			marks.clear();
			for (int i = 0; i < 6; i++) {
				float markLength = (float) Length.NM.toRadians(EeINS.getSettings().getNavSettings().getCogVectorLength()/6 * i * (gpsData.getSog() / 60.0));
				LatLonPoint marker = startPos.getPoint(markLength, cogRadian);
				RotationalPoly polyMark = new RotationalPoly(markX, markY, new BasicStroke(STROKE_WIDTH), null);
				polyMark.setLocation(marker.getLatitude(), marker.getLongitude(), OMGraphic.DECIMAL_DEGREES, cogRadian);
				marks.add(polyMark);
			}
			
		}
		
		// Redraw	
		if (!doUpdate()) {
			graphics.project(getProjection(), true);
			//System.out.println("Dropping update");
			return;
		}
		
		//System.out.println("Doing redraw");
		lastPos = new GeoLocation(currentPos.getLatitude(), currentPos.getLongitude());
		lastRedraw = new Date();				
		doPrepare();
	}

	public double[] calculateMinuteMarker(LatLonPoint startPoint, int minute){
		float length = (float) Length.NM.toRadians(EeINS.getSettings().getNavSettings().getCogVectorLength()/6 * minute * (gpsData.getSog() / 60.0));
		LatLonPoint marker = startPos.getPoint(length, (float) ProjMath.degToRad(gpsData.getCog()));
		double[] newMarker = {marker.getLatitude(), marker.getLongitude(), 0, 0};
		return newMarker;
	}
	
	
	@Override
	public synchronized OMGraphicList prepare() {
		if(startPos != null && endPos != null){
			Point2D start = getProjection().forward(startPos);
			Point2D end = getProjection().forward(endPos);
			vector.setX1(start.getX());
			vector.setY1(start.getY());
			vector.setX2(end.getX());
			vector.setY2(end.getY());
			if(vector.norm() < EeINS.getSettings().getNavSettings().getShowMinuteMarksSelf()){
				marks.setVisible(false);
			} else {
				marks.setVisible(true);
			}
		}
		if(gpsData != null && gpsData.getSog() != null && gpsData.getSog() < 0.1){
			backShipArrow.setVisible(false);
			frontShipArrow.setVisible(false);
		} else {
			backShipArrow.setVisible(true);
			frontShipArrow.setVisible(true);
		}
		graphics.project(getProjection(), true);
		return graphics;
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (gpsHandler == null && obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler)obj;
			gpsHandler.addListener(this);
		}
		if (vesselAisHandler == null && obj instanceof VesselAisHandler) {
			vesselAisHandler = (VesselAisHandler)obj;
		}
	}
	
	@Override
	public void findAndUndo(Object obj) {
		if (gpsHandler == obj) {
			gpsHandler.removeListener(this);
			gpsHandler = null;
		}
		if (vesselAisHandler == obj) {
			vesselAisHandler = null;
		}
	}
	
//	@Override
//	public void paint(Graphics g) {
//		System.out.println("Entering GpsLayer.paint)");
//		long start = System.nanoTime();
//		super.paint(g);
//		System.out.println("Finished GpsLayer.paint() in " + EeINS.elapsed(start) + " ms\n---");
//	}

	
}
