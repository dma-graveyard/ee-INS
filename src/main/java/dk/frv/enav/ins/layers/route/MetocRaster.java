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
package dk.frv.enav.ins.layers.route;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.common.graphics.CenterRaster;

/**
 * Abstract base class for metoc raster images
 */
public abstract class MetocRaster extends OMGraphicList {

	private static final long serialVersionUID = 1L;

	public MetocRaster() {
		super();
		setVague(true);
	}
	
	/**
	 * Places the raster correctly.
	 * Requirements for the raster:
	 * -Width and height must be uneven
	 * -If image is asymmetric (eg. an arrow with origin in center of image), 
	 * an equal amount of empty pixels (+1) must exist on the opposite side of the image
	 * @param rasterURI Location of the raster
	 * @param lat GeoLocation of the raster's vertical center
	 * @param lon GeoLocation of the raster's horizontal center
	 * @param angle Rotational angle in radians
	 */
	public void addRaster(String rasterURI, double lat, double lon, double angle){
		ImageIcon imageIcon = new ImageIcon(EeINS.class.getResource(rasterURI));
		int imageWidth = imageIcon.getIconWidth();
		int imageHeight = imageIcon.getIconHeight();
		CenterRaster rasterMark = new CenterRaster(lat, lon, imageWidth, imageHeight, imageIcon);
//		rasterMark.setStroke(new BasicStroke());
//		rasterMark.setSelected(true);
		rasterMark.setRotationAngle(angle);
		add(rasterMark);
	}
	
}
