package dk.frv.enav.ins.gps;

public interface IGpsDataListener {
	
	/**
	 * Method called when the position of ship changes, or the known position is lost 
	 */
	void gpsDataUpdate(GpsData gpsData);

}
