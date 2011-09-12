package dk.frv.enav.ins.ais;

import java.util.Date;

public class SarTarget extends AisTarget {
	
	private static final long OLD_TTL = 720; // 12 min
	private static final long GONE_TTL = 1800; // 30 min
		
	private VesselPositionData positionData = null;
	private VesselStaticData staticData = null;
	private boolean old = false;
	private Date firstReceived = null;

	public SarTarget(SarTarget sarTarget) {
		super(sarTarget);
		if (sarTarget.positionData != null) {
	    	this.positionData = new VesselPositionData(sarTarget.positionData);
	    }
	    if (sarTarget.staticData != null) {
	    	this.staticData = new VesselStaticData(sarTarget.staticData);
	    }
	}
	
	public SarTarget() {
		super();
	}

	@Override
	public boolean hasGone(Date now, boolean strict) {		
		long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;		
		// Determine if gone
		return elapsed > GONE_TTL;
	}
	
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
