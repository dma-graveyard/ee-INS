/*
 * Copyright 2012 Danish Maritime Authority. All rights reserved.
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
package dk.frv.enav.ins.service.intendedroute;

import org.apache.log4j.Logger;

import dk.dma.enav.services.voyage.intendedroute.IntendedRouteMessage;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.IRoutesUpdateListener;
import dk.frv.enav.ins.route.RoutesUpdateEvent;
import dk.frv.enav.ins.service.EnavService;
import dk.frv.enav.ins.service.EnavServiceHandler;

/**
 * Intended route service implementation
 */
public class IntendedRouteService extends EnavService implements IRoutesUpdateListener, Runnable {
	
	private static final Logger LOG = Logger.getLogger(IntendedRouteService.class);

	/**
	 * The current active route provider
	 */
	private ActiveRouteProvider provider;

	public IntendedRouteService(EnavServiceHandler enavServiceHandler, ActiveRouteProvider provider) {
		super(enavServiceHandler);
		this.provider = provider;		
	}

	/**
	 * Broadcast intended route
	 */
	public void broadcastIntendedRoute() {
		System.out.println("BROADCAST INTENDED ROUTE");

		// Get active route from provider
		LOG.info("Get active route");
		
		ActiveRoute activeRoute = provider.getActiveRoute();
		LOG.info("Got active route");
		
		// Make intended route message
		IntendedRouteMessage message = new IntendedRouteMessage();
		
		

		// If active route is null, make cancellation message

		// Transform active route into intended route message

		// send message
		LOG.info("Sending");
		enavServiceHandler.getEnavCloudHandler().sendMessage(message);
		LOG.info("Done sending");
		
	}

	/**
	 * Handle event of active route change
	 */
	@Override
	public void routesChanged(RoutesUpdateEvent e) {
		if (e == RoutesUpdateEvent.ACTIVE_ROUTE_UPDATE || e == RoutesUpdateEvent.ACTIVE_ROUTE_FINISHED
				|| e == RoutesUpdateEvent.ROUTE_ACTIVATED || e == RoutesUpdateEvent.ROUTE_DEACTIVATED) {
			broadcastIntendedRoute();
		}
	}

	/**
	 * Send active route periodically
	 */
	@Override
	public void run() {
		while (true) {
			EeINS.sleep(10000);
			broadcastIntendedRoute();
		}
	}
	
	public void start() {
		(new Thread(this)).start();
	}

}
