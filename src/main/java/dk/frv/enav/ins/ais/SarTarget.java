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

/**
 * Class representing an AIS SART
 */
public class SarTarget extends AisTarget {
	
	private static final long OLD_TTL = 720; // 12 min
	private static final long GONE_TTL = 1800; // 30 min
		
	private VesselPositionData positionData = null;
	private VesselStaticData staticData = null;
	private boolean old = false;
	private Date firstReceived = null;

	/**
	 * Copy constructor
	 * @param sarTarget
	 */
	public SarTarget(SarTarget sarTarget) {
		super(sarTarget);
		if (sarTarget.positionData != null) {
	    	this.positionData = new VesselPositionData(sarTarget.positionData);
	    }
	    if (sarTarget.staticData != null) {
	    	this.staticData = new VesselStaticData(sarTarget.staticData);
	    }
	}
	
	/**
	 * Empty constructor
	 */
	public SarTarget() {
		super();
	}

	/**
	 * Determines if the target should be considered gone
	 * @return if the target has gone
	 */
	@Override
	public boolean hasGone(Date now, boolean strict) {		
		long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;		
		// Determine if gone
		return elapsed > GONE_TTL;
	}
	
	/**
	 * Determine if the target has changed state to old
	 * @param now
	 * @return changed to old
	 */
	public boolean hasGoneOld(Date now) {
		long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;
		boolean newOld = elapsed > OLD_TTL;
		if (newOld != old) {
			old = newOld;
			return true;
		}		
		return false;
	}
	
	public VesselPositionData getPositionData() {
		return positionData;
	}

	public void setPositionData(VesselPositionData positionData) {
		this.positionData = positionData;
	}

	public VesselStaticData getStaticData() {
		return staticData;
	}

	public void setStaticData(VesselStaticData staticData) {
		this.staticData = staticData;
	}

	public boolean isOld() {
		return old;
	}
	
	public void setOld(boolean old) {
		this.old = old;
	}
	
	public Date getFirstReceived() {
		return firstReceived;
	}
	
	public void setFirstReceived(Date firstReceived) {
		this.firstReceived = firstReceived;
	}

}
