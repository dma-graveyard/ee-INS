package dk.frv.enav.ins.event;

import com.bbn.openmap.proj.coords.LatLonPoint;

public interface IMapCoordListener {
	void recieveCoord(LatLonPoint llp);
}
