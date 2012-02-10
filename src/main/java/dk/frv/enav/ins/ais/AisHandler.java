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
package dk.frv.enav.ins.ais;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.ais.binary.SixbitException;
import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisBinaryMessage;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage18;
import dk.frv.ais.message.AisMessage21;
import dk.frv.ais.message.AisMessage24;
import dk.frv.ais.message.AisMessage5;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.message.binary.AisApplicationMessage;
import dk.frv.ais.message.binary.BroadcastIntendedRoute;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.services.ais.AisServices;
import dk.frv.enav.ins.status.AisStatus;
import dk.frv.enav.ins.status.ComponentStatus;
import dk.frv.enav.ins.status.IStatusComponent;

/**
 * Class for handling incoming AIS messages on a vessel and maintainer of AIS target tables 
 */
public class AisHandler extends MapHandlerChild implements IAisHandler, IStatusComponent, Runnable {

	private static final Logger LOG = Logger.getLogger(AisHandler.class);
	
	protected static final String aisViewFile = ".aisview";
	
	public class AisMessageExtended {
		public String name;
		public long MMSI;
		public double hdg;
		public String dst;
		public AisMessageExtended(String name, Long key, double hdg, String dst2) {
			this.name = name;
			this.MMSI = key;
			this.hdg = hdg;
			this.dst = dst2;
		}

	}	

	// How long targets are saved without reports
	protected static final long TARGET_TTL = 60 * 60 * 1000; // One hour	

	protected Map<Long, AtoNTarget> atonTargets = new HashMap<Long, AtoNTarget>();
	protected Map<Long, VesselTarget> vesselTargets = new HashMap<Long, VesselTarget>();
	protected Map<Long, SarTarget> sarTargets = new HashMap<Long, SarTarget>();
	protected List<IAisTargetListener> listeners = new ArrayList<IAisTargetListener>();
	protected AisServices aisServices = null;	
	protected AisStatus aisStatus = new AisStatus();
	protected String sartMmsiPrefix = "970";	
	protected boolean showIntendedRouteDefault = false;
	protected boolean strictAisMode = true;
	
	public AisHandler() {
		
	}
	
	public AisHandler(boolean showIntendedRouteDefault, boolean strictAisMode) {
		this.showIntendedRouteDefault = showIntendedRouteDefault;
		this.strictAisMode = strictAisMode;
	}
	
	/**
	 * Get target with mmsi
	 * @param mmsi
	 * @return
	 */
	public synchronized AisTarget getTarget(long mmsi) {
		if (vesselTargets.containsKey(mmsi)) {
			return new VesselTarget(vesselTargets.get(mmsi));
		} else if (sarTargets.containsKey(mmsi)) {
			return new SarTarget(sarTargets.get(mmsi));
		} else if (atonTargets.containsKey(mmsi)) {
			return new AtoNTarget(atonTargets.get(mmsi));
		}
		return null;
	}
	
	/**
	 * Method receiving AIS messages from AIS sensor
	 */
	@Override
	public synchronized void receive(AisMessage aisMessage) {
		// Mark successful reception 
		aisStatus.markAisReception();
		
		if (aisMessage instanceof AisPositionMessage) {
			AisPositionMessage aisPositionMessage = (AisPositionMessage) aisMessage;
			// Create PositionData
			VesselPositionData vesselPositionData = new VesselPositionData(aisPositionMessage);
			// Update or create entry
			if (vesselPositionData.hasPos()) {
				updatePos(aisPositionMessage.getUserId(), vesselPositionData, VesselTarget.AisClass.A);
			}
		} else if (aisMessage instanceof AisMessage18) {
			AisMessage18 posMessage = (AisMessage18) aisMessage;
			VesselPositionData vesselPositionData = new VesselPositionData(posMessage);
			// Update or create entry
			if (vesselPositionData.hasPos()) {
				updatePos(posMessage.getUserId(), vesselPositionData, VesselTarget.AisClass.B);
			}
		} else if (aisMessage instanceof AisMessage21) {
			AisMessage21 msg21 = (AisMessage21) aisMessage;
			updateAton(msg21);
		} else if (aisMessage instanceof AisMessage5) {
			AisMessage5 msg5 = (AisMessage5) aisMessage;
			VesselStaticData staticData = new VesselStaticData(msg5);
			updateStatics(msg5.getUserId(), staticData);
		} else if (aisMessage instanceof AisMessage24) {
			AisMessage24 msg24 = (AisMessage24) aisMessage;			
			updateClassBStatics(msg24);
		} else if (aisMessage instanceof AisBinaryMessage) {
			AisBinaryMessage binaryMessage = (AisBinaryMessage)aisMessage;
			AisApplicationMessage appMessage;
			try {
				 appMessage = binaryMessage.getApplicationMessage();
			} catch (SixbitException e) {
				LOG.error("Failed to get application specific message: " + e.getMessage());
				return;
			}
			// Handle broadcast messages
			if (aisMessage.getMsgId() == 8 && appMessage != null) {
				// Handle route information
				if (appMessage.getDac() == BroadcastIntendedRoute.DAC && appMessage.getFi() == BroadcastIntendedRoute.FI) {
					BroadcastIntendedRoute intendedRoute = (BroadcastIntendedRoute)appMessage;
					//LOG.info("BroadcastRouteInformation: " + routeInformation);
					// Handle intended route
					updateIntendedRoute(aisMessage.getUserId(), new AisIntendedRoute(intendedRoute));
				}
			}					
		}
	}
	
	public synchronized void hideAllIntendedRoutes() {
		for (VesselTarget vesselTarget : vesselTargets.values()) {
			VesselTargetSettings vesselTargetSettings = vesselTarget.getSettings();
			if (vesselTargetSettings.isShowRoute() && vesselTarget.hasIntendedRoute()) {
				vesselTargetSettings.setShowRoute(false);
				publishUpdate(vesselTarget);
			}			
		}
	}
	
	public synchronized void showAllIntendedRoutes() {
		for (VesselTarget vesselTarget : vesselTargets.values()) {
			VesselTargetSettings vesselTargetSettings = vesselTarget.getSettings();
			if (!vesselTargetSettings.isShowRoute() && vesselTarget.hasIntendedRoute()) {
				vesselTargetSettings.setShowRoute(true);
				publishUpdate(vesselTarget);
			}			
		}
	}


	/**
	 * Update AtoN target
	 * @param msg21
	 */
	protected synchronized void updateAton(AisMessage21 msg21) {
		// Try to find existing entry
		AtoNTarget atonTarget = atonTargets.get(msg21.getUserId());
		// If not exists, create new and insert
		if (atonTarget == null) {
			atonTarget = new AtoNTarget();
			atonTarget.setMmsi(msg21.getUserId());
			atonTargets.put(msg21.getUserId(), atonTarget);
		}
		// Update target
		atonTarget.update(msg21);
		// Update last received
		atonTarget.setLastReceived(GnssTime.getInstance().getDate());
		// Update status
		atonTarget.setStatus(AisTarget.Status.OK);
		publishUpdate(atonTarget);
	}
	
	/**
	 * Update intended route of vessel target
	 * @param mmsi
	 * @param routeData
	 */
	protected synchronized void updateIntendedRoute(long mmsi, AisIntendedRoute routeData) {
		// Try to find exiting target
		VesselTarget vesselTarget = vesselTargets.get(mmsi);
		// If not exists, wait for it to be created by position report
		if (vesselTarget == null) {
			return;
		}
		// Update intented route
		vesselTarget.setAisRouteData(routeData);
		publishUpdate(vesselTarget);
	}
	
	/**
	 * Update vessel target statics
	 * @param mmsi
	 * @param staticData
	 */
	protected synchronized void updateStatics(long mmsi, VesselStaticData staticData) {
		// Determine if this is SART
		if (isSarTarget(mmsi)) {			
			updateSartStatics(mmsi, staticData);
			return;
		}
		
		// Try to find exiting target
		VesselTarget vesselTarget = vesselTargets.get(mmsi);
		// If not exists, wait for it to be created by position report
		if (vesselTarget == null) {
			return;
		}
		// Update static data
		vesselTarget.setStaticData(staticData);
		
	}
	
	/**
	 * Update class b vessel statics
	 * @param msg24
	 */
	protected synchronized void updateClassBStatics(AisMessage24 msg24) {
		// Try to find exiting target
		VesselTarget vesselTarget = vesselTargets.get(msg24.getUserId());
		// If not exists, wait for it to be created by position report
		if (vesselTarget == null) {
			return;
		}
		
		// Get or create static data
		VesselStaticData staticData = vesselTarget.getStaticData();
		if (staticData == null) {
			staticData = new VesselStaticData(msg24);
			vesselTarget.setStaticData(staticData);
		} else {
			staticData.update(msg24);
		}
		
	}
	
	/**
	 * Update SART statics
	 * @param mmsi
	 * @param staticData
	 */
	protected synchronized void updateSartStatics(long mmsi, VesselStaticData staticData) {
		// Try to find exiting target
		SarTarget sarTarget = sarTargets.get(mmsi);
		// If not exists, wait for it to be created by position report
		if (sarTarget == null) {
			return;
		}		
		// Update static data
		sarTarget.setStaticData(staticData);
	}
	
	/**
	 * Determine if mmsi belongs to a SART
	 * @param mmsi
	 * @return
	 */
	public boolean isSarTarget(long mmsi) {
		// AIS-SART transponder MMSI begins with 970
		String strMmsi = Long.toString(mmsi);
		return strMmsi.startsWith(sartMmsiPrefix);
	}

	/**
	 * Update vessel target position data
	 * @param mmsi
	 * @param positionData
	 * @param aisClass
	 */
	protected synchronized void updatePos(long mmsi, VesselPositionData positionData, VesselTarget.AisClass aisClass) {
		// Determine if this is SART
		if (isSarTarget(mmsi)) {			
			updateSartPos(mmsi, positionData);
			return;
		}
		
		// Try to find exiting target
		VesselTarget vesselTarget = vesselTargets.get(mmsi);
		// If not exists, create and insert
		if (vesselTarget == null) {
			vesselTarget = new VesselTarget();
			vesselTarget.getSettings().setShowRoute(showIntendedRouteDefault);
			vesselTarget.setMmsi(mmsi);
			vesselTargets.put(mmsi, vesselTarget);
		}
		// Update class and pos data
		vesselTarget.setAisClass(aisClass);
		vesselTarget.setPositionData(positionData);
		// Update track
		// TODO
		// Update last received
		vesselTarget.setLastReceived(GnssTime.getInstance().getDate());
		// Update status
		vesselTarget.setStatus(AisTarget.Status.OK);
		// Publish update
		publishUpdate(vesselTarget);
	}
	
	/**
	 * Update SART position data
	 * @param mmsi
	 * @param positionData
	 */
	protected synchronized void updateSartPos(long mmsi,  VesselPositionData positionData) {
		Date now = GnssTime.getInstance().getDate();
		// Try to find target
		SarTarget sarTarget = sarTargets.get(mmsi);
		// If not exists, create and insert
		if (sarTarget == null) {
			sarTarget = new SarTarget();
			sarTarget.setMmsi(mmsi);
			sarTarget.setFirstReceived(now);
			sarTargets.put(mmsi, sarTarget);
		}
		// Update pos data
		sarTarget.setPositionData(positionData);
		// Update last received
		sarTarget.setLastReceived(now);
		// Update status
		sarTarget.setStatus(AisTarget.Status.OK);
		// Update old
		sarTarget.setOld(false);
		// Publish update
		publishUpdate(sarTarget);
	}
	
	/**
	 * Publish the update of a target to all listeners
	 * @param aisTarget
	 */
	protected synchronized void publishUpdate(AisTarget aisTarget) {
		for (IAisTargetListener listener : listeners) {
			listener.targetUpdated(aisTarget);
		}
	}
	
	protected synchronized void publishAll() {
		LOG.debug("Published all targets");
		publishAll(vesselTargets.values());
		publishAll(atonTargets.values());
		publishAll(sarTargets.values());
	}
	
	protected synchronized void publishAll(Collection<?> targets) {
		for (Object aisTarget : targets) {
			publishUpdate((AisTarget)aisTarget);
		}
	}
	
	public synchronized void addListener(IAisTargetListener targetListener) {
		listeners.add(targetListener);
	}
	
	public synchronized void removeListener(IAisTargetListener targetListener) {
		listeners.remove(targetListener);
	}
	
	/**
	 * Update status of all targets
	 */
	protected synchronized void updateStatus() {
		Date now = GnssTime.getInstance().getDate();
		List<Long> deadTargets = new ArrayList<Long>();

		// Go through all vessel targets
		for (VesselTarget vesselTarget : vesselTargets.values()) {
			if (updateTarget(vesselTarget, now)) {
				deadTargets.add(vesselTarget.getMmsi());
			}
		}
		
		// Remove dead targets
		for (Long mmsi : deadTargets) {
			LOG.debug("Dead target " + mmsi);
			vesselTargets.remove(mmsi);
		}
		
		deadTargets.clear();

		// Go through all aton targets
		for (AtoNTarget atonTarget : atonTargets.values()) {
			if (updateTarget(atonTarget, now)) {
				deadTargets.add(atonTarget.getMmsi());
			}
		}
		
		// Remove dead targets
		for (Long mmsi : deadTargets) {
			LOG.debug("Dead AtoN target " + mmsi);
			atonTargets.remove(mmsi);
		}
		
		deadTargets.clear();
		
		// Go through all sart targets
		for (SarTarget sarTarget : sarTargets.values()) {
			if (updateTarget(sarTarget, now)) {
				deadTargets.add(sarTarget.getMmsi());
			}
		}
		
		// Remove dead targets
		for (Long mmsi : deadTargets) {
			LOG.debug("Dead target " + mmsi);
			sarTargets.remove(mmsi);
		}
		
		deadTargets.clear();

	}
	
	
	/**
	 * Update AIS target. Return true if the target is considered dead, not just gone
	 * @param aisTarget
	 * @param now
	 * @return
	 */
	protected boolean updateTarget(AisTarget aisTarget, Date now) {
		if (aisTarget.isGone()) {
			// Maybe too old and needs to be deleted
			if (aisTarget.isDeadTarget(TARGET_TTL, now)) {							
				return true;
			}
			return false;
		}
		if (aisTarget.hasGone(now, strictAisMode)) {
			aisTarget.setStatus(AisTarget.Status.GONE);
			publishUpdate(aisTarget);
			return false;
		}
		// Check if route information is invalid
		if (aisTarget instanceof VesselTarget) {
			if (((VesselTarget)aisTarget).checkAisRouteData()) {
				publishUpdate(aisTarget);
				return false;
			}
		}
		// Check if sart has gone old
		if (aisTarget instanceof SarTarget) {
			if (((SarTarget)aisTarget).hasGoneOld(now)) {
				publishUpdate(aisTarget);
				return false;
			}
		}
		return false;
	}
		
	@Override
	public void run() {
		// Publish loaded targets		
		EeINS.sleep(2000);
		publishAll();
		
		while (true) {
			EeINS.sleep(10000);
			// Update status on targets
			updateStatus();
		}
	}

	@Override
	public void findAndInit(Object obj) {		
		if (obj instanceof AisServices) {
			aisServices = (AisServices)obj;
		}
	}
	
	@Override
	public ComponentStatus getStatus() {
		return aisStatus;
	}
	
	public AisStatus getAisStatus() {
		return aisStatus;
	}
	
	public Map<Long, VesselTarget> getVesselTargets() {
		return vesselTargets;
	}
		
	
	/**
	 * Try to load AIS view from disk
	 */
	public synchronized void loadView() {
		AisStore aisStore = null;		 
		
		try {
			FileInputStream fileIn = new FileInputStream(aisViewFile);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			aisStore = (AisStore) objectIn.readObject();
			objectIn.close();
			fileIn.close();
		} catch (FileNotFoundException e) {
			// Not an error
		} catch (Exception e) {
			LOG.error("Failed to load AIS view file: " + e.getMessage());
			// Delete possible corrupted or old file
			(new File(aisViewFile)).delete();
		}
		
		if (aisStore == null) {
			return;
		}
		
		// Retrieve targets
		if (aisStore.getVesselTargets() != null) {
			vesselTargets = aisStore.getVesselTargets();
		}
		if (aisStore.getAtonTargets() != null) {
			atonTargets = aisStore.getAtonTargets();
		}
		if (aisStore.getSarTargets() != null) {
			sarTargets = aisStore.getSarTargets();
		}
				
		LOG.info("AIS handler loaded total targets: " + (vesselTargets.size() + atonTargets.size() + sarTargets.size()));
				
		// Update status to update old and gone (twice for old and gone)
		updateStatus();
		updateStatus();		
	}
	
	/**
	 * Save AIS view to file
	 */
	public synchronized void saveView() {
		AisStore aisStore = new AisStore();
		aisStore.setVesselTargets(vesselTargets);
		aisStore.setAtonTargets(atonTargets);
		aisStore.setSarTargets(sarTargets);
		
		try {
			FileOutputStream fileOut = new FileOutputStream(aisViewFile);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(aisStore);
			objectOut.close();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
			LOG.error("Failed to save Ais view file: " + e.getMessage());
		}
	}

	
}
