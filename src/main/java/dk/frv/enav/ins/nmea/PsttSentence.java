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
import java.util.Date;
import java.util.TimeZone;

import dk.frv.ais.sentence.SentenceException;
import dk.frv.enav.ins.gps.GnssTimeMessage;

public class PsttSentence {
	
	private GnssTimeMessage gnssTimeMessage = null;

	public PsttSentence() {
		
	}

	public boolean parse(String msg) throws SentenceException {
		String[] fields = msg.split(",|\\*");
		if (fields.length != 5) {
			throw new SentenceException("Not four fields i PSTT sentence: " + msg);
		}
		
		if (fields[2].equals("00000000") || fields[3].equals("999999")) {
			return false;
		}
		
		String dateStr = fields[2] + " " + fields[3];
		
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
			Date d = dateFormat.parse(dateStr);
			gnssTimeMessage = new GnssTimeMessage(d);
		} catch (ParseException e) {
			throw new SentenceException("Wrong date format in PSTT sentence: " + msg);
		}
		return true;		
	}
	
	public GnssTimeMessage getGnssTimeMessage() {
		return gnssTimeMessage;
	}

}
