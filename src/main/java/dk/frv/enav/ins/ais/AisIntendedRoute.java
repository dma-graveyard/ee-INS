package dk.frv.enav.ins.ais;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dk.frv.ais.message.binary.RouteInformation;

public class AisIntendedRoute extends AisRouteData {
	private static final long serialVersionUID = 1L;
			
	protected List<Date> etas = new ArrayList<Date>();	
	protected Double speed = null;
	protected Double activeWpRange = null;
	
	public AisIntendedRoute(AisIntendedRoute routeData) {
		super(routeData);
		this.etas = routeData.etas;
		this.speed = routeData.speed;
		this.activeWpRange = routeData.activeWpRange;
	}
	
	public AisIntendedRoute(RouteInformation routeInformation) {
		super(routeInformation);
		if (duration == 0) {
			// Cancel route
			return;
		}
		
		// Calculate avg speed
		speed = ranges.get(waypoints.size() - 1) / ((double)routeInformation.getDuration() / 60.0);
		
		// ETA's
		long start = etaFirst.getTime();
		etas.add(etaFirst);
		for (int i=0; i < waypoints.size() - 1; i++) {
			double dist = ranges.get(i + 1) - ranges.get(i);
			double dur = (dist / speed) * 60 * 60 * 1000;
			start += dur;
			etas.add(new Date(start));
		}
		
	}
		
	public void update(VesselPositionData posData) {
		if (posData == null || posData.getPos() == null || waypoints.size() == 0) {
			return;
		}
		
		// Range to first wp
		activeWpRange = posData.getPos().getRhumbLineDistance(waypoints.get(0)) / 1852.0;
	}
	
	public Double getRange(int index) {
		if (activeWpRange == null) {
			return null;
		}
		return activeWpRange + ranges.get(index);
	}
	
	public Date getEta(int index) {
		if (index >= etas.size()) {
			return null;
		}
		return etas.get(index);
	}
		
	public Double getSpeed() {
		return speed;
	}
	
}
