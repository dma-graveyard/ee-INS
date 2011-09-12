package dk.frv.enav.ins.util.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisPositionMessage;

public class TrackCollector implements IAisHandler {
	
	private long mmsi;
	private List<TimePoint> track = new ArrayList<TimePoint>();  
	
	public TrackCollector(long mmsi) {
		this.mmsi = mmsi;
	}

	@Override
	public void receive(AisMessage aisMessage) {
		if (aisMessage.getUserId() != mmsi) {
			return;
		}
		if (!(aisMessage instanceof AisPositionMessage)) {
			return;
		}
		if (aisMessage.getSourceTag() == null) {
			System.err.println("No GH source tag for position message: " + aisMessage.getVdm().getOrgLinesJoined());
			return;
		}
		Date timestamp = aisMessage.getSourceTag().getTimestamp();
		if (timestamp == null) {
			System.err.println("No timestamp in GH source tag for position message: " + aisMessage.getVdm().getOrgLinesJoined());
			return;
		}
		
		AisPositionMessage posMessage = (AisPositionMessage)aisMessage;
		TimePoint point = new TimePoint(posMessage.getPos().getGeoLocation(), timestamp);
		track.add(point);
	}
	
	public List<TimePoint> getSortedTrack() {
		Collections.sort(track);
		return track;
	}

}
