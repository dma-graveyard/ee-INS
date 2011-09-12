package dk.frv.enav.ins.ais;

import java.util.Date;

public abstract class AisTarget {
	
	public enum Status {OK, GONE};
	
	protected Date lastReceived;
	protected long mmsi;
	protected Status status;
	
	public AisTarget() {
		status = Status.OK;
	}
	
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
