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
package dk.frv.enav.ins.common.graphics;

import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMRaster;
import com.bbn.openmap.proj.Projection;

/**
 * A raster graphic that is centered
 */
public class CenterRaster extends OMRaster {
	private static final long serialVersionUID = 1L;

	private Point2D center = null;
	private Boolean notGeolocation = false;
	private float radius = 5;
	private int x;
	private int y;

	/**
	 * Position in lat,lon with width i and height j
	 * @param lat
	 * @param lon
	 * @param i
	 * @param j
	 * @param imageIcon
	 */
	public CenterRaster(double lat, double lon, int i, int j, ImageIcon imageIcon) {
		super(lat, lon, -(i / 2), -(j / 2), imageIcon);
	}
	
	/**
	 * Position in x,y
	 * @param x
	 * @param y
	 * @param imageIcon
	 */
	public CenterRaster(int x, int y, ImageIcon imageIcon) {
		super(x-(imageIcon.getIconWidth() / 2), y-(imageIcon.getIconHeight() / 2), imageIcon);
		this.x = x;
		this.y = y;
		notGeolocation = true;
	}

	/**
	 * Set the radius to be used in distance calculations
	 * @param radius
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public boolean generate(Projection proj) {
		if(notGeolocation) {
			center = new Point2D.Double(x, y);
		} else {
			center = proj.forward(getLat(), getLon());
		}
		return super.generate(proj);
	}

	@Override
	public float distance(double mx, double my) {
		if (center == null || getNeedToRegenerate() || shape == null) {
			return Float.MAX_VALUE;
		}

		float dist = (float) Math.sqrt(Math.pow(mx - center.getX(), 2) + Math.pow(my - center.getY(), 2)) - radius;
		if (dist < 0) {
			dist = 0;
		}

		return dist;
	}

	@Override
	public boolean shouldRenderFill() {
		return false;
	}

}
