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

import dk.frv.enav.ins.EeINS;

public class MetocCurrentGraphic extends MetocRaster {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String markerDir = "/images/metoc/current/mark";
	
	public MetocCurrentGraphic(double lat, double lon, double angle, double currentSpeedMs, double currentWarnLimit) {
		super();
		
		double defaultCurrentLow = EeINS.getSettings().getEnavSettings().getDefaultCurrentLow();
		double defaultCurrentMedium = EeINS.getSettings().getEnavSettings().getDefaultCurrentMedium();
		
		double currentSpeedKn = currentSpeedMs * (3.6d/1.852d);
		
		if(currentSpeedKn >= 0 && currentSpeedKn <= defaultCurrentLow){
			markerDir += "01";
		} else if (currentSpeedKn > 1 && currentSpeedKn <= defaultCurrentMedium){
			markerDir += "02";
		} else if (currentSpeedKn > defaultCurrentMedium){
			markerDir += "03";
		}
		
		if(currentSpeedKn >= currentWarnLimit){
			markerDir += "red.png";
		} else {
			markerDir += ".png";
		}
		
		addRaster(markerDir, lat, lon, angle);
	}
}
