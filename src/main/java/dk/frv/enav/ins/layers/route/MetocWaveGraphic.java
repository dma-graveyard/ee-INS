package dk.frv.enav.ins.layers.route;

import dk.frv.enav.ins.EeINS;

public class MetocWaveGraphic extends MetocRaster {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String markerDir = "/images/metoc/wave/mark";

	public MetocWaveGraphic(double lat, double lon, double angle, double waveHeight, double waveWarnLimit) {
		super();
		
		double defaultWaveLow = EeINS.getSettings().getEnavSettings().getDefaultWaveLow();
		double defaultWaveMedium = EeINS.getSettings().getEnavSettings().getDefaultWaveMedium();
				
		if(waveHeight >= 0 && waveHeight <= defaultWaveLow){
			markerDir += "01";
		} else if (waveHeight > defaultWaveLow && waveHeight <= defaultWaveMedium){
			markerDir += "02";
		} else if (waveHeight > defaultWaveMedium){
			markerDir += "03";
		}
		
		if(waveHeight >= waveWarnLimit){
			markerDir += "red.png";
		} else {
			markerDir += ".png";
		}
		
		addRaster(markerDir, lat, lon, angle);
	}
}
