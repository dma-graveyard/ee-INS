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
 * Abstract base class for AIS targets
 */
public abstract class AisTarget {
	
	/**
	 * Status of target can either be OK og GONE
	 */
	public enum Status {OK, GONE};
	
	protected Date lastReceived;
	protected long mmsi;
	protected Status status;
	
	public AisTarget() {
		status = Status.OK;
	}

	/**
	 * Copy constructor
	 * @param aisTarget
	 */
	public AisTarget(AisTarget aisTarget) {
		lastReceived = aisTarget.lastReceived;
		mmsi = aisTarget.mmsi;
		status = aisTarget.status;
	}
	
	/**
	 * Returns true if target has gone
	 * @param now
	 * @param strict
	 * @return
	 */
	public abstract boolean hasGone(Date now, boolean strict);
	
	/**
	 * Determine if target is dead given ttl (time-to-live)
	 * @param ttl
	 * @param now
	 * @return
	 */
	public boolean isDeadTarget(long ttl, Date now) {
		return (now.getTime() - lastReceived.getTime() > ttl);		
	}
	
	public void setLastReceived(Date lastReceived) {
		this.lastReceived = lastReceived;
	}
	
	public Date getLastReceived() {
		return lastReceived;
	}
	
	public long getMmsi() {
		return mmsi;
	}
	
	public void setMmsi(long mmsi) {
		this.mmsi = mmsi;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public boolean isGone() {
		return (status == Status.GONE);
	}
	
	@Override
	public int hashCode() {
		return (int)mmsi;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AisTarget [lastReceived=");
		builder.append(lastReceived);
		builder.append(", mmsi=");
		builder.append(mmsi);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
	
}
