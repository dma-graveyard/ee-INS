package dk.frv.enav.ins.layers.route;

import java.util.Date;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.common.xml.metoc.MetocForecastPoint;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.route.Route;

public class MetocGraphic extends OMGraphicList {

	private static final long serialVersionUID = 1L;

	private Route route;
	private boolean activeRoute;
	private int step = 1;

	public MetocGraphic(Route route, boolean activeRoute) {
		this.route = route;
		this.activeRoute = activeRoute;
		paintMetoc();
	}
	
	public void paintMetoc(){
		clear();
		MetocForecast metocForecast = route.getMetocForecast();
		List<MetocForecastPoint> forecasts = metocForecast.getForecasts();
		Date now = GnssTime.getInstance().getDate();
		for (int i = 0; i < forecasts.size(); i += step) {
			MetocForecastPoint metocPoint = forecasts.get(i);

			// If active route, only show if 2 min in future or more
			if (activeRoute) {
				long fromNow = (metocPoint.getTime().getTime() - now.getTime()) / 1000 / 60;
				if (fromNow < 2) {
					continue;
				}
			}

			MetocPointGraphic metocPointGraphic = new MetocPointGraphic(metocPoint, this);
			add(metocPointGraphic);
			
		}
	}

	public Route getRoute() {
		return route;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
	public int getStep() {
		return step;
	}
}
