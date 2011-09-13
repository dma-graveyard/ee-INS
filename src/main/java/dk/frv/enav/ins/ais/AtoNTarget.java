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

import java.util.Date;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.message.AisMessage21;

/**
 * Class representing a AtoN target
 */
public class AtoNTarget extends AisTarget {
	
	private GeoLocation pos;
	private int atonType;
	private String name;
	private int posAcc;
	private int dimBow;
	private int dimStern;
	private int dimPort;
	private int dimStarboard;
	private int posType;
	private int offPosition;
	private int regional;
	private int raim;
	private int virtual;
	private int assigned;
	private String nameExt;
	
	/**
	 * Empty constructor
	 */
	public AtoNTarget() {
		super();
	}
	
	/**
	 * Copy constructor
	 * @param atoNTarget
	 */
	public AtoNTarget(AtoNTarget atoNTarget) {
		super(atoNTarget);
		pos = new GeoLocation(pos);
		atonType = atoNTarget.atonType;
		name = atoNTarget.name;
		posAcc = atoNTarget.posAcc;
		dimBow = atoNTarget.dimBow;
		dimStern = atoNTarget.dimStern;
		dimPort = atoNTarget.dimPort;
		dimStarboard = atoNTarget.dimStarboard;
		posType = atoNTarget.posType;
		offPosition = atoNTarget.offPosition;
		regional = atoNTarget.regional;
		raim = atoNTarget.raim;
		virtual = atoNTarget.virtual;
		assigned = atoNTarget.assigned;
		nameExt = atoNTarget.nameExt;
	}
	
	/**
	 * Update AtoN target given AIS message #21
	 * @param msg21
	 */
	public void update(AisMessage21 msg21) {
		pos = msg21.getPos().getGeoLocation();
		atonType = msg21.getAtonType();
		name = msg21.getName();
		posAcc = msg21.getPosAcc();
		dimBow = msg21.getDimBow();
		dimStern = msg21.getDimStern();
		dimPort = msg21.getDimPort();
		dimStarboard = msg21.getDimStarboard();
		posType = msg21.getPosType();
		offPosition = msg21.getOffPosition();
		regional = msg21.getRegional();
		raim = msg21.getRaim();
		virtual = msg21.getVirtual();
		assigned = msg21.getAssigned();
		nameExt = msg21.getNameExt();
	}
	
	/**
	 * Determine if AtoN target has gone
	 */
	@Override
	public boolean hasGone(Date now, boolean strict) {
		long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;		
		// Base gone "loosely" on ITU-R Rec M1371-4 4.2.1  (3 minutes)
		long tol = 600; // 10 minutes
		return (elapsed > tol);
	}
	
	public GeoLocation getPos() {
		return pos;
	}

	public int getAtonType() {
		return atonType;
	}

	public void setAtonType(int atonType) {
		this.atonType = atonType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosAcc() {
		return posAcc;
	}

	public void setPosAcc(int posAcc) {
		this.posAcc = posAcc;
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

	public int getOffPosition() {
		return offPosition;
	}

	public void setOffPosition(int offPosition) {
		this.offPosition = offPosition;
	}

	public int getRegional() {
		return regional;
	}

	public void setRegional(int regional) {
		this.regional = regional;
	}

	public int getRaim() {
		return raim;
	}

	public void setRaim(int raim) {
		this.raim = raim;
	}

	public int getVirtual() {
		return virtual;
	}

	public void setVirtual(int virtual) {
		this.virtual = virtual;
	}

	public int getAssigned() {
		return assigned;
	}

	public void setAssigned(int assigned) {
		this.assigned = assigned;
	}

	public String getNameExt() {
		return nameExt;
	}

	public void setNameExt(String nameExt) {
		this.nameExt = nameExt;
	}

	public void setPos(GeoLocation pos) {
		this.pos = pos;
	}
	
}
