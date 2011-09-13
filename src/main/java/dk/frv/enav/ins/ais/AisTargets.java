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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.bbn.openmap.MapHandlerChild;

import dk.frv.ais.binary.SixbitException;
import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisBinaryMessage;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisMessage18;
import dk.frv.ais.message.AisMessage21;
import dk.frv.ais.message.AisMessage24;
import dk.frv.ais.message.AisMessage5;
import dk.frv.ais.message.AisMessage6;
import dk.frv.ais.message.AisPositionMessage;
import dk.frv.ais.message.binary.AddressedRouteInformation;
import dk.frv.ais.message.binary.AisApplicationMessage;
import dk.frv.ais.message.binary.BroadcastRouteInformation;
import dk.frv.ais.message.binary.RouteInformation;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.gps.GpsData;
import dk.frv.enav.ins.nmea.IAisListener;
import dk.frv.enav.ins.nmea.NmeaSensor;
import dk.frv.enav.ins.nmea.SensorType;
import dk.frv.enav.ins.services.ais.AisServices;
import dk.frv.enav.ins.status.AisStatus;
import dk.frv.enav.ins.status.ComponentStatus;
import dk.frv.enav.ins.status.IStatusComponent;

public class AisTargets extends MapHandlerChild implements IAisListener, IStatusComponent, Runnable {

	private static final Logger LOG = Logger.getLogger(AisTargets.class);

	// How long targets are saved without reports
	private static final long TARGET_TTL = 60 * 60 * 1000; // One hour
	private static final double SIMULATED_AIS_RANGE = 20;

	private Map<Long, AtoNTarget> atonTargets = new HashMap<Long, AtoNTarget>();
	private Map<Long, VesselTarget> vesselTargets = new HashMap<Long, VesselTarget>();
	private Map<Long, SarTarget> sarTargets = new HashMap<Long, SarTarget>();
	private List<IAisTargetListener> listeners = new ArrayList<IAisTargetListener>();
	private List<IAisRouteSuggestionListener> suggestionListeners = new ArrayList<IAisRouteSuggestionListener>();
	private VesselTarget ownShip = new VesselTarget();	
	private double aisRange = 0;
	private NmeaSensor nmeaSensor = null;
	private AisServices aisServices = null;
	private NameCache nameCache;
	private AisStatus aisStatus = new AisStatus();
	private String sartMmsiPrefix = "970";
	
	public AisTargets() {
		if (EeINS.getSettings().getSensorSettings().isSimulateGps() && EeINS.getSettings().getSensorSettings().getAisSensorRange() == 0) {
			aisRange = SIMULATED_AIS_RANGE;
			ownShip.setMmsi(EeINS.getSettings().getSensorSettings().getSimulatedOwnShip());
		} else {
			aisRange = EeINS.getSettings().getSensorSettings().getAisSensorRange();
		}
		nameCache = new NameCache();
		nameCache.loadFromFile();
		sartMmsiPrefix = EeINS.getSettings().getAisSettings().getSartPrefix();
		EeINS.startThread(this, "AisTargets");
	}

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
				if (appMessage.getDac() == 1 && appMessage.getFi() == 27) {
					BroadcastRouteInformation routeInformation = (BroadcastRouteInformation)appMessage;
					//LOG.info("BroadcastRouteInformation: " + routeInformation);
					// Handle intended route
					if (routeInformation.getRouteType() == RouteInformation.RouteType.SHIP_ROUTE.getType()
							&& routeInformation.getSenderClassification() == 0) {
						updateIntendedRoute(aisMessage.getUserId(), new AisIntendedRoute(routeInformation));
					}
				}
			}
			// Handle addressed messages
			if (aisMessage.getMsgId() == 6 && appMessage != null) {
				
				// Check if for own ship
				AisMessage6 msg6 = (AisMessage6)aisMessage; 
				if (ownShip.getMmsi() != msg6.getDestination()) {
					return;
				}
				
				// Handle adressed route information
				if (appMessage.getDac() == 1 && appMessage.getFi() == 28) {
					AddressedRouteInformation routeInformation = (AddressedRouteInformation)appMessage;
					LOG.info("AddressedRouteInformation: " + routeInformation);
					AisAdressedRouteSuggestion addressedRouteSuggestion = new AisAdressedRouteSuggestion(routeInformation);
					addressedRouteSuggestion.setSender(aisMessage.getUserId());
					for (IAisRouteSuggestionListener suggestionListener : suggestionListeners) {
						suggestionListener.receiveRouteSuggestion(addressedRouteSuggestion);
					}
					// Acknowledge the reception
					if (suggestionListeners.size() > 0) {
						aisServices.acknowledgeRouteSuggestion(msg6, routeInformation);
					}
				}
			}			
		}
		
	}
	
	public synchronized void hideAllIntendedRoutes() {
		for (VesselTarget vesselTarget : vesselTargets.values()) {
			VesselTargetSettings settings = vesselTarget.getSettings();
			if (settings.isShowRoute() && vesselTarget.hasIntendedRoute()) {
				settings.setShowRoute(false);
				publishUpdate(vesselTarget);
			}			
		}
	}
	
	public synchronized void showAllIntendedRoutes() {
		for (VesselTarget vesselTarget : vesselTargets.values()) {
			VesselTargetSettings settings = vesselTarget.getSettings();
			if (!settings.isShowRoute() && vesselTarget.hasIntendedRoute()) {
				settings.setShowRoute(true);
				publishUpdate(vesselTarget);
			}			
		}
	}

	@Override
	public synchronized void receiveOwnMessage(AisMessage aisMessage) {
		if (aisMessage instanceof AisPositionMessage) {
			AisPositionMessage aisPositionMessage = (AisPositionMessage) aisMessage;
			ownShip.setAisClass(VesselTarget.AisClass.A);
			ownShip.setPositionData(new VesselPositionData(aisPositionMessage));
		} else if (aisMessage instanceof AisMessage18) {
			AisMessage18 posMessage = (AisMessage18) aisMessage;
			ownShip.setAisClass(VesselTarget.AisClass.B);
			ownShip.setPositionData(new VesselPositionData(posMessage));
		} else if (aisMessage instanceof AisMessage5) {
			AisMessage5 msg5 = (AisMessage5) aisMessage;
			ownShip.setStaticData(new VesselStaticData(msg5));
		}

		ownShip.setLastReceived(GnssTime.getInstance().getDate());
		ownShip.setMmsi(aisMessage.getUserId());

	}

	private synchronized void updateAton(AisMessage21 msg21) {
		if (!isWithinRange(msg21.getPos().getGeoLocation())) {
			return;
		}
		
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
	
	private synchronized void updateIntendedRoute(long mmsi, AisIntendedRoute routeData) {
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

	private synchronized void updateStatics(long mmsi, VesselStaticData staticData) {
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
		
		// Update name cache
		nameCache.setName(mmsi, staticData.getName());
	}
	
	private synchronized void updateClassBStatics(AisMessage24 msg24) {
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
		
		// Update name cache
		if (staticData.getName() != null) {
			nameCache.setName(msg24.getUserId(), staticData.getName());
		}
	}
	
	private synchronized void updateSartStatics(long mmsi, VesselStaticData staticData) {
		// Try to find exiting target
		SarTarget sarTarget = sarTargets.get(mmsi);
		// If not exists, wait for it to be created by position report
		if (sarTarget == null) {
			return;
		}		
		// Update static data
		sarTarget.setStaticData(staticData);
	}
	
	public NameCache getNameCache() {
		return nameCache;
	}
	
	public boolean isSarTarget(long mmsi) {
		// AIS-SART transponder MMSI begins with 970
		String strMmsi = Long.toString(mmsi);
		return strMmsi.startsWith(sartMmsiPrefix);
	}

	private synchronized void updatePos(long mmsi, VesselPositionData positionData, VesselTarget.AisClass aisClass) {
		if (!isWithinRange(positionData.getPos())) {
			return;
		}
		
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
			vesselTarget.getSettings().setShowRoute(EeINS.getSettings().getAisSettings().isShowIntendedRouteByDefault());
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
	
	private synchronized void updateSartPos(long mmsi,  VesselPositionData positionData) {
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
	
	private synchronized boolean isWithinRange(GeoLocation pos) {
		if (getAisRange() <= 0) {
			return true;
		}
		GpsData gpsData = EeINS.getGpsHandler().getCurrentData();
		if (gpsData == null) {
			return false;
		}
		if (gpsData.isBadPosition()) {
			// If simulation we will not accept targets before own pos is known once
			if (EeINS.getSettings().getSensorSettings().isSimulateGps() && vesselTargets.size() == 0) {
				return false;
			}
		}
		
		double distance = gpsData.getPosition().getRhumbLineDistance(pos) / 1852.0;
		return (distance <= aisRange);		
	}

	private synchronized void publishUpdate(AisTarget aisTarget) {		
//		AisTarget copy;
//		if (aisTarget instanceof VesselTarget) {
//			copy = new VesselTarget((VesselTarget) aisTarget);
//		} else {
//			copy = new AtoNTarget((AtoNTarget) aisTarget);
//		}
		
		for (IAisTargetListener listener : listeners) {
			//listener.targetUpdated(copy);
			listener.targetUpdated(aisTarget);
		}
	}
	
	public synchronized void addListener(IAisTargetListener targetListener) {
		listeners.add(targetListener);
	}
	
	public synchronized void removeListener(IAisTargetListener targetListener) {
		listeners.remove(targetListener);
	}
	
	public synchronized void addRouteSuggestionListener(IAisRouteSuggestionListener routeSuggestionListener) {
		suggestionListeners.add(routeSuggestionListener);
	}
	
	public synchronized void removeRouteSuggestionListener(IAisRouteSuggestionListener routeSuggestionListener) {
		suggestionListeners.remove(routeSuggestionListener);
	}
	
	public synchronized VesselTarget getOwnShip() {		
		if (ownShip == null) return null;
		return new VesselTarget(ownShip);
	}

	private synchronized void updateStatus() {
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
	private synchronized boolean updateTarget(AisTarget aisTarget, Date now) {
		if (aisTarget.isGone()) {
			// Maybe too old and needs to be deleted
			if (aisTarget.isDeadTarget(TARGET_TTL, now)) {							
				return true;
			}
			return false;
		}
		if (aisTarget.hasGone(now, EeINS.getSettings().getAisSettings().isStrict())) {
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
	
	public synchronized void saveToFile() {
		nameCache.saveToFile();
	}


	public double getAisRange() {
		return aisRange;
	}
	
	public void setAisRange(double aisRange) {
		this.aisRange = aisRange;
	}
	
	@Override
	public void run() {
		while (true) {
			EeINS.sleep(10000);
			// Update status on targets
			updateStatus();

		}
	}

	@Override
	public void findAndInit(Object obj) {
		if (nmeaSensor == null && obj instanceof NmeaSensor) {			
			NmeaSensor sensor = (NmeaSensor)obj;
			if (sensor.isSensorType(SensorType.AIS)) {
				LOG.info("Found AIS sensor");
				nmeaSensor = sensor;
				nmeaSensor.addAisListener(this);
			}
		}
		else if (obj instanceof AisServices) {
			aisServices = (AisServices)obj;
		}
	}
	
	@Override
	public void findAndUndo(Object obj) {
		if (obj == nmeaSensor) {
			nmeaSensor.removeAisListener(this);
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
}
