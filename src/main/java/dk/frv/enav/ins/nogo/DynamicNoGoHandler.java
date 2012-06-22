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
package dk.frv.enav.ins.nogo;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.nogo.response.NogoResponse;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.layers.nogo.DynamicNogoLayer;
import dk.frv.enav.ins.layers.nogo.NogoLayer;
import dk.frv.enav.ins.services.shore.ShoreServiceException;
import dk.frv.enav.ins.services.shore.ShoreServices;
import dk.frv.enav.ins.settings.EnavSettings;

/**
 * Component for handling MSI messages
 */
public class DynamicNogoHandler extends MapHandlerChild implements Runnable {

	private static final Logger LOG = Logger.getLogger(NogoHandler.class);

	GeoLocation northWestPointOwn;
	GeoLocation southEastPointOwn;
	float draughtOwn;

	private long mmsiTarget = -1;
	GeoLocation northWestPointTarget;
	GeoLocation southEastPointTarget;
	float draughtTarget;

	boolean nogoFailed = false;

	private ShoreServices shoreServices;
	private GpsHandler gpsHandler = null;
	private AisHandler aisHandler = null;

	// Create a seperate layer for the nogo information
	private DynamicNogoLayer nogoLayer;

	private Date lastUpdate;
	private long pollInterval;

	// Data from the nogo response
	private List<NogoPolygon> nogoPolygons;
	private Date validFrom;
	private Date validTo;
	private int noGoErrorCode;
	private String noGoMessage;

	private boolean dynamicNoGoActive = true;

	public int getNoGoErrorCode() {
		return noGoErrorCode;
	}

	public String getNoGoMessage() {
		return noGoMessage;
	}

	public boolean getNogoFailed() {
		return nogoFailed;
	}

	public void setNogoFailed(boolean nogoFailed) {
		this.nogoFailed = nogoFailed;
	}

	private Boolean isVisible = true;

	public DynamicNogoHandler(EnavSettings enavSettings) {
		// pollInterval = enavSettings.getNogoPollInterval();
		EeINS.startThread(this, "DynamicNoGoHandler");
	}

	@Override
	public void run() {
		
		EeINS.sleep(15000);
		updateNogo();
		
		
		while (dynamicNoGoActive) {
			 EeINS.sleep(300000);
//			 EeINS.sleep(150000);
			 updateNogo();	 
		}
	}

	public synchronized void updateNogo() {
		System.out.println("Dynamic nogo");
		

		// Is dynamic nogo activated?
		if (dynamicNoGoActive) {

			// Get current ship location and add box around it, + / - something
			if (aisHandler.getOwnShip().getPositionData() != null) {

				GeoLocation shipLocation = aisHandler.getOwnShip()
						.getPositionData().getPos();

				southEastPointOwn = new GeoLocation(
						shipLocation.getLatitude() - 0.04,
						shipLocation.getLongitude() + 0.08);
				northWestPointOwn = new GeoLocation(
						shipLocation.getLatitude() + 0.04,
						shipLocation.getLongitude() - 0.08);
				
				// gpsHandler.getCurrentData().getPosition().getLatitude();
				// gpsHandler.getCurrentData().getPosition().getLongitude();
				
				
				notifyUpdate(false);
				
			}

			if (aisHandler.getOwnShip().getStaticData() != null) {
				System.out.println("Getting draught from static");
				draughtOwn = aisHandler.getOwnShip().getStaticData()
						.getDraught() / 10;
				
				System.out.println(aisHandler.getOwnShip().getStaticData()
						.getDraught());
			} else {
				System.out.println("Setting draught to 5");
				draughtOwn = 5;
			}
			
			// NorthWest pos
			// SouthEast pos

			// Get current target if exists.
			// Get current timezone - Configure dynamic NoGo box? Not important
			// right now
			// Get own draught

			// aisHandler.getOwnShip().getPositionData().getPos()

			// Get target ship draught - if exists
			// aisHandler.getVesselTargets().get(mmsiTarget).getStaticData()
			// .getDraught();

			// Make two nogo requests, one for the ship, one for the target
			// Plot these requests into the DynamicNoGoLayer
			// Done?

			
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
					LOG.error("Failed to get NoGo from shore: "
							+ e.getMessage());

					nogoFailed = true;
					nogoUpdated = true;
					setLastUpdate(now);
				}
			}
			// Notify if update
			if (nogoUpdated) {
				notifyUpdate(true);
			}

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
		validFrom = date;
		validTo = date;

		if (aisHandler.getOwnShip().getPositionData() != null) {

			System.out.println("Making a request to the server");
			
			// Send a rest to shoreServices for NoGo
			NogoResponse nogoResponseOwn = shoreServices.nogoPoll(-draughtOwn,
					northWestPointOwn, southEastPointOwn, validFrom, validTo);
			//
			// NogoResponse nogoResponseTarget =
			// shoreServices.nogoPoll(draughtTarget,
			// northWestPointTarget, southEastPointTarget, validFrom, validTo);
			

			nogoPolygons = nogoResponseOwn.getPolygons();
			validFrom = nogoResponseOwn.getValidFrom();
			validTo = nogoResponseOwn.getValidTo();
			noGoErrorCode = nogoResponseOwn.getNoGoErrorCode();
			noGoMessage = nogoResponseOwn.getNoGoMessage();

			// System.out.println(nogoResponse.getNoGoErrorCode());
			// System.out.println(nogoResponse.getNoGoMessage());
			// System.out.println(nogoResponse.getPolygons().size());

			
			
			
			if (nogoResponseOwn == null
					|| nogoResponseOwn.getPolygons() == null) {
				return false;
			}
		}

		return true;

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
		if (obj instanceof DynamicNogoLayer) {
			nogoLayer = (DynamicNogoLayer) obj;
		}
		if (gpsHandler == null && obj instanceof GpsHandler) {
			gpsHandler = (GpsHandler) obj;
		}
		if (aisHandler == null && obj instanceof AisHandler) {
			aisHandler = (AisHandler) obj;
		}

	}

	public float getDraughtOwn() {
		return draughtOwn;
	}

	public GeoLocation getNorthWestPointOwn() {
		return northWestPointOwn;
	}

	public GeoLocation getSouthEastPointOwn() {
		return southEastPointOwn;
	}

	
	
	
}
