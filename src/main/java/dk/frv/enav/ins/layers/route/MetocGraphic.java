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

import java.util.Date;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.common.xml.metoc.MetocForecastPoint;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.route.Route;

/**
 * Metoc graphic for route
 */
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
