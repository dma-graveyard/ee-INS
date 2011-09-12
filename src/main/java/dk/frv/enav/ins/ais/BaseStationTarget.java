package dk.frv.enav.ins.ais;

import java.util.Date;

import dk.frv.ais.message.AisMessage4;

public class BaseStationTarget extends AisTarget {
	
	// TODO
	
	public BaseStationTarget() {
		super();	
	}
	
	public BaseStationTarget(BaseStationTarget bsTarget) {
		// TODO
	}
	
	public void update(AisMessage4 msg4) {
		// TODO
	}
	
	@Override
	public boolean hasGone(Date now, boolean strict) {
		long elapsed = (now.getTime() - lastReceived.getTime()) / 1000;		
		// Base gone "loosely" on ITU-R Rec M1371-4 4.2.1  (10 seconds)
		long tol = 120; // 2 minutes		
		return (elapsed > tol);
	}

}
