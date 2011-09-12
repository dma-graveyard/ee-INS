package dk.frv.enav.ins.gps;

public interface IGnssTimeListener {
	
	void receive(GnssTimeMessage gnssTimeMessage);

}
