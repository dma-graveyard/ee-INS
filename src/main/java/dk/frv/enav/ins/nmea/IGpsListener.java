package dk.frv.enav.ins.nmea;

/**
 * Interface to implement to receive GPS messages 
 * @author obo
 */
public interface IGpsListener {
	
	void receive(GpsMessage gpsMessage);

}
