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
package dk.frv.enav.ins.msi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.MsiPoint;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.common.util.Calculator;
import dk.frv.enav.ins.gps.GnssTime;
import dk.frv.enav.ins.route.ActiveRoute;
import dk.frv.enav.ins.route.Route;

/**
 * Serializable class to store MSI information
 */
public class MsiStore implements Serializable {
	// TODO: If class changed, generate a new serial version ID
	private static final long serialVersionUID = -5653288769636767014L;
	private static final Logger LOG = Logger.getLogger(MsiStore.class);	
	private static final String msiFile = ".msi";
	
	private Map<Integer, MsiMessage> messages = new TreeMap<Integer, MsiMessage>();
	private int lastMessage = 0;
	private Set<Integer> acknowledged = new HashSet<Integer>();
	private Set<Integer> visibleGPS = new HashSet<Integer>();
	private Set<Integer> visibleRoute = new HashSet<Integer>();
	private Set<Integer> relevant = new HashSet<Integer>();
	private Set<Integer> allVisible = new HashSet<Integer>();
	
	public MsiStore() {

	}
	
	public synchronized boolean hasValidUnacknowledged() {
		Date now = GnssTime.getInstance().getDate();
		for (Integer msgId : messages.keySet()) {
			MsiMessage msg = messages.get(msgId);
			if (msg.getValidFrom() != null && msg.getValidFrom().after(now)) {
				continue;
			}
			if (!acknowledged.contains(msgId)) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean hasValidVisibleUnacknowledged() {
		Date now = GnssTime.getInstance().getDate();
		for (Integer msgId : messages.keySet()) {
			MsiMessage msg = messages.get(msgId);
			if(msg.getValidFrom() != null && msg.getValidFrom().after(now)) {
				continue;
			}
			
			if(!acknowledged.contains(msgId) && (visibleGPS.contains(msgId) || visibleRoute.contains(msgId))) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized void update(List<MsiMessage> newMessages, GeoLocation calculationPosition, List<Route> routes) {
		for (MsiMessage newMessage : newMessages) {
			// Update lastMessage
			if (newMessage.getId() > lastMessage) {
				lastMessage = newMessage.getId(); 
			}
			// Remove acknowledge if existing message  
			acknowledged.remove(newMessage.getMessageId());
			if (newMessage.getDeleted() != null) {
				// Remove message
				messages.remove(newMessage.getMessageId());
			} else {
				// Insert/update message
				messages.put(newMessage.getMessageId(), newMessage);
			}
		}
		visibleGPS.clear();
		if(calculationPosition != null)
			setVisibility(calculationPosition);
		if(routes != null)
			setVisibility(routes);
		saveToFile();
	}
	
	/**
	 * Sets msi warnings visible if they are in the radius of the given location (ship location)
	 * @param calculationPosition Current location of own ship
	 */
	public void setVisibility(GeoLocation calculationPosition) {
		visibleGPS.clear();
		Iterator<Map.Entry<Integer, MsiMessage>> it = messages.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, MsiMessage> entry = it.next();
			MsiMessage msiMessage = entry.getValue();
			List<MsiPoint> msiPoints = msiMessage.getLocation().getPoints();
			Double distance = Double.MAX_VALUE;
			for (MsiPoint msiPoint : msiPoints) {
				GeoLocation msiLocation = new GeoLocation(msiPoint.getLatitude(), msiPoint.getLongitude());
				double currentDistance = Calculator.range(calculationPosition, msiLocation, Heading.GC);
				distance = Math.min(currentDistance, distance);
			}
			if(distance <= EeINS.getSettings().getEnavSettings().getMsiRelevanceFromOwnShipRange())
				visibleGPS.add(msiMessage.getMessageId());
		}
		LOG.debug("Relevance calculation performed at:" + calculationPosition.getLatitude() + ", "
				+ calculationPosition.getLongitude() + " yielded " + visibleGPS.size() + " visible warnings");
	}
	
	/**
	 * Sets msi warnings visible if they are within a rectangle given by the routes' waypoints.
	 * @param routes List of routes for which to enable msi warnings at
	 */
	public void setVisibility(List<Route> routes) {
		visibleRoute.clear();
		if(routes == null || routes.size() == 0) {
			return;
		}
		Iterator<Map.Entry<Integer, MsiMessage>> it = messages.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, MsiMessage> entry = it.next();
			MsiMessage msiMessage = entry.getValue();
			
			boolean contained = false;
			List<MsiPoint> msiPoints = msiMessage.getLocation().getPoints();
			for (MsiPoint msiPoint : msiPoints) {
				for (Route route : routes) {
					GeoLocation msiLocation = new GeoLocation(msiPoint.getLatitude(), msiPoint.getLongitude());
					if(route.isVisible() && route.isPointWithingBBox(msiLocation)){
						contained = true;
					}
				}
			}
			if(contained) {
				visibleRoute.add(msiMessage.getMessageId());
			}
		}
		LOG.debug("Relevance calculation performed for routes yielded " + visibleRoute.size() + " visible warnings");
	}
	
	/**
	 * Sets relevance for MSI warnings in proximity of an active route. Currently implemented with a bounding
	 * box method, but later should be implemented with calculation of cross track distance from route to point
	 * @param route Active route
	 */
	public void setRelevance(ActiveRoute route) {
		relevant.clear();
		Iterator<Map.Entry<Integer, MsiMessage>> it = messages.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, MsiMessage> entry = it.next();
			MsiMessage msiMessage = entry.getValue();
			
			boolean contained = false;
			List<MsiPoint> msiPoints = msiMessage.getLocation().getPoints();
			for (MsiPoint msiPoint : msiPoints) {
				GeoLocation msiLocation = new GeoLocation(msiPoint.getLatitude(), msiPoint.getLongitude());
				if(route.isPointWithingBBox(msiLocation)) {
					contained = true;
				}
			}
			if(contained) {
				relevant.add(msiMessage.getMessageId());
			}
		}
	}
	
	public void clearRelevance() {
		relevant.clear();
	}
	
	public synchronized boolean cleanup() {
		List<Integer> doDelete = new ArrayList<Integer>();
		Date now = GnssTime.getInstance().getDate();
		for (MsiMessage message : messages.values()) {
			// Check if validTo has been passed
			if (message.getValidTo() != null && message.getValidTo().before(now)) {
				doDelete.add(message.getMessageId());
			}
		}
		for (Integer msgId : doDelete) {
			messages.remove(msgId);
			acknowledged.remove(msgId);
		}
		return (doDelete.size() > 0);
	}
	
	public synchronized void deleteMessage(MsiMessage msiMessage) {
		acknowledged.remove(msiMessage.getMessageId());
		messages.remove(msiMessage.getMessageId());		
	}
	
	public synchronized int getLastMessage() {
		return lastMessage;
	}
	
	public synchronized void saveToFile() {
		try {
			FileOutputStream fileOut = new FileOutputStream(msiFile);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(this);
			objectOut.close();
			fileOut.close();
		} catch (IOException e) {
			LOG.error("Failed to save MSI file: " + e.getMessage());
		}
	}
	
	public static MsiStore loadFromFile() {
		try {
			FileInputStream fileIn = new FileInputStream(msiFile);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			MsiStore msiStore = (MsiStore) objectIn.readObject();
			objectIn.close();
			fileIn.close();
			return msiStore;
		} catch (FileNotFoundException e) {
			// Not an error
			System.out.println("Exception");
		} catch (Exception e) {
			LOG.error("Failed to load MSI file: " + e.getMessage());
			// Delete possible corrupted or old file
			(new File(msiFile)).delete();
		}
		return new MsiStore();
	}
	
	public synchronized Set<Integer> getAcknowledged() {
		return acknowledged;
	}
	
	public synchronized Set<Integer> getVisible() {
		allVisible.clear();
		allVisible.addAll(visibleGPS);
		allVisible.addAll(visibleRoute);
		return allVisible;
	}
	
	public synchronized Set<Integer> getRelevant() {
		return relevant;
	}
	
	public synchronized Map<Integer, MsiMessage> getMessages() {
		return messages;
	}

}
