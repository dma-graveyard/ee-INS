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
