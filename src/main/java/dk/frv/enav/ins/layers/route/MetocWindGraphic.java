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

public class MetocWindGraphic extends MetocRaster {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String markerDir = "/images/metoc/wind/mark";
	
	public MetocWindGraphic(double lat, double lon, double angle, double windSpeed, double windWarnLimit) {
		super();
		
		double windSpeedKnots = windSpeed * (3.6d/1.852d);

		if(windSpeedKnots >= 0 && windSpeedKnots <= 5){
			markerDir += "005";
		} else if (windSpeedKnots > 5 && windSpeedKnots <= 10){
			markerDir += "010";
		} else if (windSpeedKnots > 10 && windSpeedKnots <= 15){
			markerDir += "015";
		} else if (windSpeedKnots > 15 && windSpeedKnots <= 20){
			markerDir += "020";
		} else if (windSpeedKnots > 20 && windSpeedKnots <= 25){
			markerDir += "025";
		} else if (windSpeedKnots > 25 && windSpeedKnots <= 30){
			markerDir += "030";
		} else if (windSpeedKnots > 30 && windSpeedKnots <= 35){
			markerDir += "035";
		} else if (windSpeedKnots > 35 && windSpeedKnots <= 40){
			markerDir += "040";
		} else if (windSpeedKnots > 40 && windSpeedKnots <= 45){
			markerDir += "045";
		} else if (windSpeedKnots > 45 && windSpeedKnots <= 50){
			markerDir += "050";
		} else if (windSpeedKnots > 50 && windSpeedKnots <= 55){
			markerDir += "055";
		} else if (windSpeedKnots > 55 && windSpeedKnots <= 60){
			markerDir += "060";
		} else if (windSpeedKnots > 60 && windSpeedKnots <= 65){
			markerDir += "065";
		} else if (windSpeedKnots > 65 && windSpeedKnots <= 70){
			markerDir += "070";
		} else if (windSpeedKnots > 70 && windSpeedKnots <= 75){
			markerDir += "075";
		} else if (windSpeedKnots > 75 && windSpeedKnots <= 80){
			markerDir += "080";
		} else if (windSpeedKnots > 80 && windSpeedKnots <= 85){
			markerDir += "085";
		} else if (windSpeedKnots > 85 && windSpeedKnots <= 90){
			markerDir += "090";
		} else if (windSpeedKnots > 90 && windSpeedKnots <= 95){
			markerDir += "095";
		} else if (windSpeedKnots > 95 && windSpeedKnots <= 100){
			markerDir += "100";
		} else if (windSpeedKnots > 100 && windSpeedKnots <= 105){
			markerDir += "105";
		}

		if(windSpeed >= windWarnLimit){
			markerDir += "red.png";
		} else {
			markerDir += ".png";
		}
		
		addRaster(markerDir, lat, lon, angle);
	}
}
