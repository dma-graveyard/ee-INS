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

import dk.frv.ais.message.AisMessage24;
import dk.frv.ais.message.AisMessage5;

public class VesselStaticData {

	private long imo;
	private String callsign;
	private String name;
	private int shipType;
	private int dimBow;
	private int dimStern;
	private int dimPort;
	private int dimStarboard;
	private int posType;
	private long eta;
	private float draught;
	private String destination;

	public VesselStaticData(VesselStaticData vesselStaticData) {
		this.imo = vesselStaticData.imo;
		this.callsign = vesselStaticData.callsign;		
		this.name = vesselStaticData.name;
		this.shipType = vesselStaticData.shipType;
		this.dimBow = vesselStaticData.dimBow;
		this.dimStern = vesselStaticData.dimStern;
		this.dimPort = vesselStaticData.dimPort;
		this.dimStarboard = vesselStaticData.dimStarboard;
		this.posType = vesselStaticData.posType;
		this.eta = vesselStaticData.eta;
		this.draught = vesselStaticData.draught;
		this.destination = vesselStaticData.destination;
	}

	public VesselStaticData(AisMessage5 msg5) {
		imo = msg5.getImo();
		callsign = msg5.getCallsign();
		name = msg5.getName();
		shipType = msg5.getShipType();
		dimBow = msg5.getDimBow();
		dimStern = msg5.getDimStern();
		dimPort = msg5.getDimPort();
		dimStarboard = msg5.getDimStarboard();
		posType = msg5.getPosType();
		eta = msg5.getEta();
		draught = msg5.getDraught();
		destination = msg5.getDest();
	}
	
	public VesselStaticData(AisMessage24 msg24) {
		update(msg24);
	}
	
	public void update(AisMessage24 msg24) {
		if (msg24.getPartNumber() == 0) {
			// part A
			this.name = msg24.getName();
			return;
		}
		// part B
		callsign = msg24.getCallsign();
		shipType = msg24.getShipType();
		dimBow = msg24.getDimBow();
		dimStern = msg24.getDimStern();
		dimPort = msg24.getDimPort();
		dimStarboard = msg24.getDimStarboard();		
	}

	public long getImo() {
		return imo;
	}

	public void setImo(long imo) {
		this.imo = imo;
	}

	public String getCallsign() {
		return callsign;
	}

	public void setCallsign(String callsign) {
		this.callsign = callsign;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getShipType() {
		return shipType;
	}

	public void setShipType(int shipType) {
		this.shipType = shipType;
	}

	public int getDimBow() {
		return dimBow;
	}

	public void setDimBow(int dimBow) {
		this.dimBow = dimBow;
	}

	public int getDimStern() {
		return dimStern;
	}

	public void setDimStern(int dimStern) {
		this.dimStern = dimStern;
	}

	public int getDimPort() {
		return dimPort;
	}

	public void setDimPort(int dimPort) {
		this.dimPort = dimPort;
	}

	public int getDimStarboard() {
		return dimStarboard;
	}

	public void setDimStarboard(int dimStarboard) {
		this.dimStarboard = dimStarboard;
	}

	public int getPosType() {
		return posType;
	}

	public void setPosType(int posType) {
		this.posType = posType;
	}

	public long getEta() {
		return eta;
	}

	public void setEta(long eta) {
		this.eta = eta;
	}

	public float getDraught() {
		return draught;
	}

	public void setDraught(float draught) {
		this.draught = draught;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VesselStaticData [callsign=");
		builder.append(callsign);
		builder.append(", destination=");
		builder.append(destination);
		builder.append(", dimBow=");
		builder.append(dimBow);
		builder.append(", dimPort=");
		builder.append(dimPort);
		builder.append(", dimStarboard=");
		builder.append(dimStarboard);
		builder.append(", dimStern=");
		builder.append(dimStern);
		builder.append(", draught=");
		builder.append(draught);
		builder.append(", eta=");
		builder.append(eta);
		builder.append(", imo=");
		builder.append(imo);
		builder.append(", name=");
		builder.append(name);
		builder.append(", posType=");
		builder.append(posType);
		builder.append(", shipType=");
		builder.append(shipType);
		builder.append("]");
		return builder.toString();
	}
	
}
