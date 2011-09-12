package dk.frv.enav.ins.layers.route;

import java.util.Date;

import dk.frv.enav.common.xml.metoc.MetocDataTypes;
import dk.frv.enav.common.xml.metoc.MetocForecastPoint;
import dk.frv.enav.ins.common.text.Formatter;
import dk.frv.enav.ins.gui.InfoPanel;
import dk.frv.enav.ins.route.RouteMetocSettings;

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
			buf.append("<tr><td>Current:</td><td>"+Formatter.formatCurrentSpeed(currentSpeed)+ " - " +Formatter.formatDegrees(currentDirection, 0)+"</td></tr>");
		}
		if (settings.getDataTypes().contains(MetocDataTypes.WI) || windSpeed != null || windDirection != null) {
			buf.append("<tr><td>Wind:</td><td>"+Formatter.formatWindSpeed(windSpeed)+" - "+Formatter.formatDegrees(windDirection, 0)+"</td></tr>");
		}
		if (settings.getDataTypes().contains(MetocDataTypes.WA) || meanWaveDirection != null) {
			buf.append("<tr><td>Waves:</td><td>"+Formatter.formatMeters(meanWaveHeight, 2)+" - "+Formatter.formatDegrees(meanWaveDirection, 0)+ meanWaveStr + "</td></tr>");
		}
		if (settings.getDataTypes().contains(MetocDataTypes.SE) || seaLevel != null) {
			buf.append("<tr><td>Sea level:</td><td>"+Formatter.formatMeters(seaLevel, 2)+"</td></tr>");
		}
		buf.append("</table></html>");
		showText(buf.toString());
	}
	
}
