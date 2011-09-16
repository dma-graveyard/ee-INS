/*
 * Copyright 2011 Danish Maritime Safety Administration. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 *   2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Safety Administration ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of Danish Maritime Safety Administration.
 * 
 */
package dk.frv.enav.ins.util.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dk.frv.ais.handler.IAisHandler;
import dk.frv.ais.message.AisMessage;
import dk.frv.ais.message.AisPositionMessage;

/**
 * Utility for collecting tracks from AIS feed.
 */
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
