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
package dk.frv.enav.ins.service.communication.enavcloud;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.enav.messages.MaritimeMessage;
import dk.dma.enav.model.ship.ShipId;
import dk.dma.enav.node.ENavContainer;
import dk.dma.enav.node.ENavContainerConfiguration;
import dk.dma.enav.node.messagehandling.MessageBus;
import dk.dma.enav.node.messagehandling.MessageMetadata;
import dk.dma.enav.transport.jms.JmsC2SMessageSource;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.ais.AisHandler;
import dk.frv.enav.ins.ais.VesselTarget;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.gps.GpsHandler;
import dk.frv.enav.ins.gps.IGpsDataListener;
import dk.frv.enav.ins.settings.EnavSettings;

/**
 * Component to handle eNav Cloud communication
 */
public class EnavCloudHandler extends MapHandlerChild  implements IGpsDataListener, Runnable {
	
	private static final Logger LOG = Logger.getLogger(EnavCloudHandler.class);
	
	private String hostPort;
	private MessageBus messageBus = null;
	private ShipId shipId = null;
	private GpsHandler gpsHandler;
	private AisHandler aisHandler;
	
	public EnavCloudHandler(EnavSettings enavSettings) {
		this.hostPort = String.format("failover://tcp://%s:%d", enavSettings.getCloudServerHost(), enavSettings.getCloudServerPort());		
	}
	
	/**
	 * Send maritime message over enav cloud
	 * @param message
	 * @return
	 */
	public boolean sendMessage(MaritimeMessage message) {
		// TODO shape from where?
		if (messageBus == null) {
			return false;
		}
		
		// Make metadata with area
		MessageMetadata metadata = MessageMetadata.create();
		//TODO metadata.setShape();
		 
		
		messageBus.send(message, metadata);
		return true;
	}
	
	/**
	 * Create the message bus
	 */
	public void init() {
		LOG.info("Connecting to enav cloud server: " + hostPort + " with shipId " + shipId.getId());
		ENavContainerConfiguration conf = new ENavContainerConfiguration();
		conf.addDatasource(new JmsC2SMessageSource(hostPort, shipId));
		ENavContainer client = conf.createAndStart();
		messageBus = client.getService(MessageBus.class);
		LOG.info("Started succesfull cloud server: " + hostPort + " with shipId " + shipId.getId());

		
	}
	
	/**
	 * Receive position updates
	 */
	@Override
	public void gpsDataUpdate(GpsData gpsData) {
		// TODO give information to messageBus if valid position		
	}
	
	
	@Override
	public void findAndInit(Object obj) {
		if (obj instanceof GpsHandler) {
			this.gpsHandler = (GpsHandler)obj;
			this.gpsHandler.addListener(this);
		} else if (obj instanceof AisHandler) {
			this.aisHandler = (AisHandler)obj;
		}
	}
	
	@Override
	public void run() {
		// For now ship id will be MMSI so we need to know
		// own ship information. Busy wait for it.
		while (true) {
			EeINS.sleep(1000);
			if (this.aisHandler != null) {
				VesselTarget ownShip = this.aisHandler.getOwnShip();
				if (ownShip != null) {
					if (ownShip.getMmsi() > 0) {
						shipId = ShipId.create(Long.toString(ownShip.getMmsi()));
						init();
						return;
					}
				}
			}
		}		
	}
	
	public void start() {
		(new Thread(this)).start();
	}

}
