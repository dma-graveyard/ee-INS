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
package dk.frv.enav.ins.layers.route;

import java.util.Date;

import dk.frv.enav.common.xml.metoc.MetocDataTypes;
import dk.frv.enav.common.xml.metoc.MetocForecastPoint;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gui.InfoPanel;
import dk.frv.enav.ins.route.RouteMetocSettings;

/**
 * Metoc mouse over info
 */
public class MetocInfoPanel extends InfoPanel {
	private static final long serialVersionUID = 1L;

	public MetocInfoPanel() {
		super();		
	}
	
	public void showText(MetocForecastPoint pointForecast, RouteMetocSettings settings){
		Date date = pointForecast.getTime();
		Double meanWaveDirection = null;
		if (pointForecast.getMeanWaveDirection() != null) {
			meanWaveDirection = pointForecast.getMeanWaveDirection().getForecast();
		}
		Double meanWaveHeight = null;
		if (pointForecast.getMeanWaveHeight() != null) {
			meanWaveHeight = pointForecast.getMeanWaveHeight().getForecast();
		}
		Double meanWavePeriod = null;
		if (pointForecast.getMeanWavePeriod() != null) {
			meanWavePeriod = pointForecast.getMeanWavePeriod().getForecast();
		}		
		Double currentDirection = null;
		if (pointForecast.getCurrentDirection() != null) {
			currentDirection = pointForecast.getCurrentDirection().getForecast();
		}
		Double currentSpeed = null;
		if (pointForecast.getCurrentSpeed() != null) {
			currentSpeed = pointForecast.getCurrentSpeed().getForecast() * (3.6d/1.852d);
		}
		Double windSpeed = null;
		if (pointForecast.getWindSpeed() != null) {
			windSpeed = pointForecast.getWindSpeed().getForecast();
		}		
		Double windDirection = null;
		if (pointForecast.getWindDirection() != null) {
			windDirection = pointForecast.getWindDirection().getForecast();
		}
		String meanWaveStr = "";
		if (meanWavePeriod != null) {
			meanWaveStr = " (" + Formatter.formatDouble(meanWavePeriod, 2) + " sec)";
		}
		Double seaLevel = null;
		if (pointForecast.getSeaLevel() != null){
			seaLevel = pointForecast.getSeaLevel().getForecast();
		}
		StringBuilder buf = new StringBuilder();
		buf.append("<html>");
		buf.append("<b>METOC DATA for "+Formatter.formatLongDateTime(date)+"</b><br/>");
		buf.append("<table cellpadding='0' cellspacing='2'>");		
		if (settings.getDataTypes().contains(MetocDataTypes.CU) || currentSpeed != null || currentDirection != null) {
			buf.append("<tr><td>Current:</td><td>"+Formatter.formatCurrentSpeed(currentSpeed, 1)+ " - " +Formatter.formatDegrees(currentDirection, 0)+"</td></tr>");
		}
		if (settings.getDataTypes().contains(MetocDataTypes.WI) || windSpeed != null || windDirection != null) {
			buf.append("<tr><td>Wind:</td><td>"+Formatter.formatWindSpeed(windSpeed, 0)+" - "+Formatter.formatDegrees(windDirection, 0)+"</td></tr>");
		}
		if (settings.getDataTypes().contains(MetocDataTypes.WA) || meanWaveDirection != null) {
			buf.append("<tr><td>Waves:</td><td>"+Formatter.formatMeters(meanWaveHeight, 1)+" - "+Formatter.formatDegrees(meanWaveDirection, 0)+ meanWaveStr + "</td></tr>");
		}
		if (settings.getDataTypes().contains(MetocDataTypes.SE) || seaLevel != null) {
			buf.append("<tr><td>Sea level:</td><td>"+Formatter.formatMeters(seaLevel, 1)+"</td></tr>");
		}
		buf.append("</table></html>");
		showText(buf.toString());
	}
	
}
