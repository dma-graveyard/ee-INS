package dk.frv.enav.ins.util.route;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleRouteGenerator implements IRouteGenerator {
	
	protected List<TimePoint> track;
	protected List<TimePoint> route = new ArrayList<TimePoint>(); 

	@Override
	public List<TimePoint> generateRoute(List<TimePoint> track) {
		this.track = track;
		if (track.size() == 0) {
			return route;
		}
		
		// Simply just make a waypoint every six minutes
		Date lastWpTime = track.get(0).getTime();
		for (int i=0; i < track.size(); i++) {
			TimePoint point = track.get(i);
			long elapsed = point.getTime().getTime() - lastWpTime.getTime();
			if (elapsed > 360000) {
				route.add(point);
				lastWpTime = point.getTime();
			}
		}
		
		
		return route;		
	}

}
