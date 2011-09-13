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
package dk.frv.enav.ins.nmea;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.ais.sentence.Sentence;
import dk.frv.ais.sentence.SentenceException;
import dk.frv.enav.ins.common.FormatException;
import dk.frv.enav.ins.common.util.ParseUtils;
import dk.frv.enav.ins.gps.GnssTimeMessage;

public class GpRmcSentence extends Sentence {
	
	private GpsMessage gpsMessage;
	private GnssTimeMessage gnssTimeMessage;
	private String status;	
	
	public GpRmcSentence() {
		super();
	}

	@Override
	public int parse(String line) throws SentenceException {
		gpsMessage = new GpsMessage();
		gnssTimeMessage = new GnssTimeMessage();
		
		// Do common parsing
		super.baseParse(line);
		
		// Check RMC
		if (!this.formatter.equals("RMC")) {
			throw new SentenceException("Not RMC sentence");
		}
		
		// Check that there is a least 10 fields
		if (fields.length < 10) {
			throw new SentenceException("RMC sentence " + line + " 	does not have at least 10 fields ");
		}
		
		// Get lat and lon		
		try {
			if (fields[3].length() > 2 && fields[5].length() > 3) {
				double lat = ParseUtils.parseLatitude(fields[3].substring(0, 2), fields[3].substring(2, fields[3].length() - 1), fields[4]);
				double lon = ParseUtils.parseLongitude(fields[5].substring(0, 3), fields[5].substring(3, fields[5].length() - 1), fields[6]);
				gpsMessage.setPos(new GeoLocation(lat, lon));
			}
			if (fields[7].length() > 0) {
				gpsMessage.setSog(ParseUtils.parseDouble(fields[7]));
			}
			if (fields[8].length() > 0) {
				gpsMessage.setCog(ParseUtils.parseDouble(fields[8]));
			}
		} catch (FormatException e1) {
			throw new SentenceException("GPS sentence not valid: " + line);
		}		
		
		// Parse time
		String dateTimeStr = fields[1] + " " + fields[9];
		SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss.SS ddMMyy");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
		try {
			gnssTimeMessage.setTime(dateFormat.parse(dateTimeStr));
		} catch (ParseException e) {
			throw new SentenceException("GPS time " + dateTimeStr + " not valid ");
		}
		
		// Get status
		status = fields[2];
		
		return 0;
	}
	
	@Override
	public String getEncoded() {
		return null;
	}

	public GpsMessage getGpsMessage() {
		return gpsMessage;
	}
	
	public GnssTimeMessage getGnssTimeMessage() {
		return gnssTimeMessage;
	}

	public String getStatus() {
		return status;
	}

}
