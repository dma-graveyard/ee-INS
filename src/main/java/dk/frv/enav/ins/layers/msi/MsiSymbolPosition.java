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

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.MsiPoint;

public abstract class MsiSymbolPosition extends OMGraphicList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected MsiMessage msiMessage;
	protected boolean acknowledged;

	public MsiSymbolPosition(MsiMessage msiMessage, boolean acknowledged) {
		super();
		this.msiMessage = msiMessage;
		this.acknowledged = acknowledged;
		
		MsiLocation msiLocation = msiMessage.getLocation();
		
		// Determine where to place MSI symbols
		switch (msiLocation.getLocationType()) {
		case POINT:
		case POINTS:
			/*
			 * Place symbol in each point 
			 */
			for (MsiPoint point : msiLocation.getPoints()) {
				createSymbol(new GeoLocation(point.getLatitude(), point.getLongitude()));
			}			
			break;
		case POLYGON:
			/*
			 * Place symbol in center of polygon
			 */
			createSymbol(msiLocation.getCenter());
			break;
		case POLYLINE:
			/*
			 * Place a symbol in middle point 
			 */
			MsiPoint middle =  msiLocation.getPoints().get(msiLocation.getPoints().size() / 2);
			createSymbol(new GeoLocation(middle.getLatitude(), middle.getLongitude()));
			break;
		default:
			break;
		}
	}

	public abstract void createSymbol(GeoLocation geoLocation);
	
	public MsiMessage getMsiMessage() {
		return msiMessage;
	}
}
