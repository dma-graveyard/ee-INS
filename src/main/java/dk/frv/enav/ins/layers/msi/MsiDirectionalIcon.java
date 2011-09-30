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
package dk.frv.enav.ins.layers.msi;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.common.graphics.CenterRaster;
import dk.frv.enav.ins.common.util.Calculator;
import dk.frv.enav.ins.msi.MsiHandler.MsiMessageExtended;

/**
 * Graphic for MSI icon showing relevant off chart MSI
 */
public class MsiDirectionalIcon extends OMGraphicList implements ProjectionListener {
	private static final long serialVersionUID = -6808339529053676255L;
	private static final int IMAGE_SIZE = 42;
	private static final ImageIcon directionImage = new ImageIcon(EeINS.class.getResource("/images/msi/msi_direction_arrow_transparent_42.png"));
	private static final ImageIcon markerImage = new ImageIcon(EeINS.class.getResource("/images/msi/msi_direction_transparent_42.png"));
	private Point2D intersection;
	private MapBean mapBean;
	private CenterRaster directionRaster;
	private CenterRaster markerRaster;
	private GeoLocation msiLocation;
	private MsiMessageExtended message;
	
	MsiDirectionalIcon(MapBean mapBean) {
		super();
		setVague(true);
		this.mapBean = mapBean;
		mapBean.addProjectionListener(this);
	}
	
	public void setMarker(MsiMessageExtended message) {
		this.message = message;
		this.msiLocation = message.msiMessage.getLocation().getCenter();
		LatLonPoint center = (LatLonPoint) mapBean.getCenter();
		GeoLocation geoCenter = new GeoLocation(center.getLatitude(), center.getLongitude());
		double bearing = Calculator.bearing(geoCenter, msiLocation, Heading.RL);
		
		Projection projection = mapBean.getProjection();
		Point2D projectedMSI = projection.forward(msiLocation.getLatitude(), msiLocation.getLongitude());
		
		Point2D origin = new Point2D.Double(mapBean.getWidth()*0.5f, mapBean.getHeight()*0.5f);
		Line2D direction = new Line2D.Double(origin, projectedMSI);
		
		double boxWidth = mapBean.getWidth()-IMAGE_SIZE/2;
		double boxHeight = mapBean.getHeight()-IMAGE_SIZE/2;
		Line2D topFrame = new Line2D.Double(IMAGE_SIZE/2,IMAGE_SIZE/2,boxWidth,IMAGE_SIZE/2);
		Line2D rightFrame = new Line2D.Double(boxWidth,IMAGE_SIZE/2,boxWidth,boxHeight);
		Line2D bottomFrame = new Line2D.Double(IMAGE_SIZE/2,boxHeight,boxWidth,boxHeight);
		Line2D leftFrame = new Line2D.Double(IMAGE_SIZE/2,IMAGE_SIZE/2,IMAGE_SIZE/2,boxHeight); 
		
		boolean intersects = false;
		
		if(intersects(direction,topFrame))
			intersects = true;
		if(intersects(direction,rightFrame))
			intersects = true;
		if(intersects(direction,bottomFrame))
			intersects = true;
		if(intersects(direction,leftFrame))
			intersects = true;
		
		if(!intersects)
			return;

		int x = Math.round((float) intersection.getX());
		int y = Math.round((float) intersection.getY());
		
		directionRaster = new CenterRaster(x,y, directionImage);
		directionRaster.setRotationAngle(Math.toRadians(bearing));
		
		markerRaster = new CenterRaster(x,y, markerImage);
		
		add(markerRaster);
		add(directionRaster);
	}
	
	public boolean intersects(Line2D direction, Line2D frame) {
		double d = (frame.getY2() - frame.getY1()) * (direction.getX2() - direction.getX1()) - 
				   (frame.getX2() - frame.getX1()) * (direction.getY2() - direction.getY1());
		
		double n_a = (frame.getX2() - frame.getX1()) * (direction.getY1() - frame.getY1()) - 
		   			 (frame.getY2() - frame.getY1()) * (direction.getX1() - frame.getX1()); 
		
		double n_b = (direction.getX2() - direction.getX1()) * (direction.getY1() - frame.getY1()) - 
		   			 (direction.getY2() - direction.getY1()) * (direction.getX1() - frame.getX1());
		
		if(d == 0)
			return false;
		
		double ua = n_a / d;
		double ub = n_b / d;
		
		if(ua >= 0d && ua <= 1d && ub >= 0d && ub <= 1d) {
			intersection = new Point2D.Double();
			intersection.setLocation(
					direction.getX1() + (ua * (direction.getX2() - direction.getX1())),
					direction.getY1() + (ua * (direction.getY2() - direction.getY1())));
			return true;
		}
		return false;
	}

	@Override
	public void projectionChanged(ProjectionEvent e) {
		clear();
		setMarker(message);
	}
	
	@Override
	public void render(Graphics gr) {
		Graphics2D image = (Graphics2D) gr;
		image.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		super.render(image);
	}
	
	public MsiMessageExtended getMessage() {
		return message;
	}
}
