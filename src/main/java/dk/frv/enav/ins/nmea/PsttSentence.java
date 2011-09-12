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
