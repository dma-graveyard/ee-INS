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

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.enav.common.xml.nogo.response.NogoResponse;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.IRoutesUpdateListener;
import dk.frv.enav.ins.route.RouteManager;
import dk.frv.enav.ins.route.RoutesUpdateEvent;
import dk.frv.enav.ins.services.shore.ShoreServiceException;
import dk.frv.enav.ins.services.shore.ShoreServices;
import dk.frv.enav.ins.settings.EnavSettings;

/**
 * Component for handling MSI messages
 */
public class NogoHandler extends MapHandlerChild implements Runnable, IRoutesUpdateListener {
	
	private static final Logger LOG = Logger.getLogger(NogoHandler.class);
	
	private ShoreServices shoreServices;
	private RouteManager routeManager;

	//Create a seperate layer for the nogo information
	//private NogoLayer nogoLayer;
	
	//Do we need to store anything?
	//private MsiStore msiStore;
	private Date lastUpdate;
	private long pollInterval;

	public NogoHandler(EnavSettings enavSettings) {
		//pollInterval = enavSettings.getNogoPollInterval();
		EeINS.startThread(this, "NogoHandler");
	}
	@Override
	
	public void run() {
		while (true) {
			EeINS.sleep(30000);
			updateNogo();
		}
	}
	public void updateNogo() {
		boolean nogoUpdated = false;
		
		Date now = new Date();
		if (getLastUpdate() == null || (now.getTime() - getLastUpdate().getTime() > pollInterval * 1000)) {
			// Poll for new messages from shore
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
			notifyUpdate();
		}
	}
	
	public void notifyUpdate() {
		// Update layer - todo
//		if (nogoLayer != null) {
//			nogoLayer.doUpdate();
//		}
	}
	
	public boolean poll() throws ShoreServiceException {

		if (shoreServices == null || routeManager.getActiveRoute() == null) {
			return false;
		}
		ActiveRoute route = routeManager.getActiveRoute();
		
		NogoResponse nogoResponse = shoreServices.nogoPoll(route);
		
		if (nogoResponse == null || nogoResponse.getNogoShape() == null) {
			return false;
		}
		LOG.info("Received something from nogo...what exactly we don't know yet");
		return true;
	}
	
	public synchronized Date getLastUpdate() {
		return lastUpdate;
	}
	
	private synchronized void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof ShoreServices) {
			shoreServices = (ShoreServices)obj;
		}
		if (obj instanceof RouteManager) {
			routeManager = (RouteManager)obj;
		}
	}	
	
	@Override
	public void routesChanged(RoutesUpdateEvent e) {
		if(e == RoutesUpdateEvent.ROUTE_ACTIVATED) {
			//Get the nogo area?
			notifyUpdate();
		}
		if(e == RoutesUpdateEvent.ROUTE_DEACTIVATED) {
			//Disable the nogo layer?
			notifyUpdate();
		}
		if(e == RoutesUpdateEvent.ROUTE_MSI_UPDATE || e == RoutesUpdateEvent.ROUTE_ADDED || e == RoutesUpdateEvent.ROUTE_REMOVED || e == RoutesUpdateEvent.ROUTE_CHANGED) {
			//Request new nogo?
			updateNogo();			
		}
	}
	
	
}
