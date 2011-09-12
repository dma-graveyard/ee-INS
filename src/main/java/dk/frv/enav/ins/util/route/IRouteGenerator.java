package dk.frv.enav.ins.util.route;

import java.util.List;

public interface IRouteGenerator {
	
	List<TimePoint> generateRoute(List<TimePoint> track); 

}
