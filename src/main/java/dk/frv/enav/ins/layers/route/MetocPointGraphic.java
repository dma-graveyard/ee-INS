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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.common.xml.metoc.MetocDataTypes;
import dk.frv.enav.common.xml.metoc.MetocForecastPoint;
import dk.frv.enav.common.xml.metoc.MetocForecastTriplet;
import dk.frv.enav.ins.route.RouteMetocSettings;

/**
 * Graphic for a metoc point. Combining graphics for different metocs. 
 */
public class MetocPointGraphic extends OMGraphicList {

	private static final long serialVersionUID = 1L;
	
	private MetocWindGraphic 	windMarker;
	private MetocCurrentGraphic currentMarker;
	private MetocWaveGraphic	waveMarker;
	private MetocForecastPoint	metocPoint;
	private MetocGraphic metocGraphic;
	private double lat;
	private double lon;
	
	public MetocPointGraphic(MetocForecastPoint metocPoint, MetocGraphic metocGraphic) {
		this.metocGraphic = metocGraphic;
		this.metocPoint = metocPoint;
		RouteMetocSettings metocSettings = metocGraphic.getRoute().getRouteMetocSettings();
		this.setVague(true);
		double lat = metocPoint.getLat();
		this.lat = lat;
		double lon = metocPoint.getLon();
		this.lon = lon;
		// Get wind speed in m/s
		MetocForecastTriplet windSpeed = metocPoint.getWindSpeed();
		// Wind from direction in degrees clockwise from north
		MetocForecastTriplet windDirection = metocPoint.getWindDirection();
		// Add wind marker
		if(windSpeed != null && windDirection != null && metocSettings.getDataTypes().contains(MetocDataTypes.WI)){
			double windForecastDirection = windDirection.getForecast();
			double windForecastMs = windSpeed.getForecast();
			double windForecastDirectionRadian = Math.toRadians(windForecastDirection);
			windMarker = new MetocWindGraphic(lat, lon, windForecastDirectionRadian, windForecastMs, metocSettings.getWindWarnLimit());
			add(windMarker);
		}
		
		// Current speed in m/s
		MetocForecastTriplet currentSpeed = metocPoint.getCurrentSpeed();
		// Current towards direction in degrees from north
		MetocForecastTriplet currentDirection = metocPoint.getCurrentDirection();
		// Add current marker
		if(currentSpeed != null && currentDirection != null && metocSettings.getDataTypes().contains(MetocDataTypes.CU)){
			double currentForecastMs = currentSpeed.getForecast();
			double currentForecastDirection = currentDirection.getForecast();
			double currentForecastDirectionRadian = Math.toRadians(currentForecastDirection);
			currentMarker = new MetocCurrentGraphic(lat, lon, currentForecastDirectionRadian, currentForecastMs, metocSettings.getCurrentWarnLimit());
			add(currentMarker);
		}
		
		// Mean wave height in meters
		MetocForecastTriplet waveHeight = metocPoint.getMeanWaveHeight();
		// Mean wave from direction in degrees from north
		MetocForecastTriplet waveDirection = metocPoint.getMeanWaveDirection();
		// Add wave marker
		if(waveHeight != null && waveDirection != null && metocSettings.getDataTypes().contains(MetocDataTypes.WA)){
			double waveForecastDirection = waveDirection.getForecast();
			waveForecastDirection += 180;
			double waveForecastHeight = waveHeight.getForecast();
			double waveForecastDirectionRadian = Math.toRadians(waveForecastDirection);
			waveMarker = new MetocWaveGraphic(lat, lon, waveForecastDirectionRadian, waveForecastHeight, metocSettings.getWaveWarnLimit());
			add(waveMarker);
		}
	}
	
	public MetocForecastPoint getMetocPoint() {
		return metocPoint;
	}
	
	@Override
	public void render(Graphics gr) {
		Graphics2D image = (Graphics2D) gr;
		image.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.render(image);
	}
	
	public MetocGraphic getMetocGraphic() {
		return metocGraphic;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
}
