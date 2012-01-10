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
package dk.frv.enav.ins.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.ins.common.graphics.RotationalPoly;

/**
 * Graphic for vessel target shown as triangle
 */
public class VesselTargetTriangle extends OMGraphicList {
	private static final long serialVersionUID = 1L;

	private RotationalPoly vessel;
	private Paint paint = new Color(74, 97, 205, 255);
	private Stroke stroke = new BasicStroke(2.0f);
	private VesselTargetGraphic vesselTarget;

	public VesselTargetTriangle() {
		int[] vesselX = { 0, 5, -5, 0 };
		int[] vesselY = { -10, 5, 5, -10 };
		vessel = new RotationalPoly(vesselX, vesselY, stroke, paint);
		add(vessel);
		this.setVague(true);
	}

	public void update(double lat, double lon, int units, double heading, VesselTargetGraphic vesselTarget) {
		this.vesselTarget = vesselTarget;
		vessel.setLocation(lat, lon, units, heading);
		
	}

	public VesselTargetGraphic getVesselTargetGraphic() {
		return vesselTarget;
	}

	public void setLinePaint(Paint paint) {
		vessel.setLinePaint(paint);
	}
	
}
