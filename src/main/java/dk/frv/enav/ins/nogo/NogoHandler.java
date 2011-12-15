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
package dk.frv.enav.ins.nogo;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.nogo.response.NogoResponse;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.layers.nogo.NogoLayer;
import dk.frv.enav.ins.services.shore.ShoreServiceException;
import dk.frv.enav.ins.services.shore.ShoreServices;
import dk.frv.enav.ins.settings.EnavSettings;

/**
 * Component for handling MSI messages
 */
public class NogoHandler extends MapHandlerChild implements Runnable {

	private static final Logger LOG = Logger.getLogger(NogoHandler.class);

	GeoLocation northWestPoint;
	GeoLocation southEastPoint;
	Double draught;

	private ShoreServices shoreServices;

	// Create a seperate layer for the nogo information
	private NogoLayer nogoLayer;

	public void setNorthWestPoint(GeoLocation northWestPoint) {
		this.northWestPoint = northWestPoint;
	}

	public void setSouthEastPoint(GeoLocation southEastPoint) {
		this.southEastPoint = southEastPoint;
	}

	private Date lastUpdate;
	private long pollInterval;

	// Data from the nogo response
	private List<NogoPolygon> nogoPolygons;
	private Date validFrom;
	private Date validTo;

	private Boolean isVisible = true;

	public NogoHandler(EnavSettings enavSettings) {
		// pollInterval = enavSettings.getNogoPollInterval();
		EeINS.startThread(this, "NogoHandler");
	}

	@Override
	public void run() {
		while (true) {
			EeINS.sleep(30000);
			// updateNogo();
		}
	}

	public synchronized void updateNogo() {
		notifyUpdate(false);
		boolean nogoUpdated = false;
		Date now = new Date();
		if (getLastUpdate() == null
				|| (now.getTime() - getLastUpdate().getTime() > pollInterval * 1000)) {
			// Poll for data from shore
			try {
				if (poll()) {
					nogoUpdated = true;
				}
				setLastUpdate(now);
			} catch (ShoreServiceException e) {
				LOG.error("Failed to get NoGo from shore: " + e.getMessage());
			}
		}
		// Notify if update
		if (nogoUpdated) {
			notifyUpdate(true);
		}

	}

	public void notifyUpdate(boolean completed) {
		if (nogoLayer != null) {
			nogoLayer.doUpdate(completed);
		}
	}

	public boolean poll() throws ShoreServiceException {

		if (shoreServices == null) {
			return false;
		}

		Date date = new Date();
		//Send a rest to shoreServices for NoGo
		NogoResponse nogoResponse = shoreServices.nogoPoll(draught,
				northWestPoint, southEastPoint, date, date);

		nogoPolygons = nogoResponse.getPolygons();
		validFrom = nogoResponse.getValidFrom();
		validTo = nogoResponse.getValidTo();

		if (nogoResponse == null || nogoResponse.getPolygons() == null) {
			return false;
		}
		return true;

	}

	public Double getDraught() {
		return draught;
	}

	public void setDraught(Double draught) {
		this.draught = -draught;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public synchronized List<NogoPolygon> getPolygons() {
		return nogoPolygons;
	}

	public synchronized Date getLastUpdate() {
		return lastUpdate;
	}

	private synchronized void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public boolean toggleLayer() {
		if (isVisible) {
			nogoLayer.setVisible(false);
			isVisible = false;
		} else {
			nogoLayer.setVisible(true);
			isVisible = true;
		}
		return isVisible;
	}

	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof ShoreServices) {
			shoreServices = (ShoreServices) obj;
		}
		if (obj instanceof NogoLayer) {
			nogoLayer = (NogoLayer) obj;
		}

	}

}
