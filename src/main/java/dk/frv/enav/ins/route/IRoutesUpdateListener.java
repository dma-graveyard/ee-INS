package dk.frv.enav.ins.route;

/**
 * Classes interesed in routes updates should implement this interface
 */
public interface IRoutesUpdateListener {
	
	void routesChanged(RoutesUpdateEvent e);

}
