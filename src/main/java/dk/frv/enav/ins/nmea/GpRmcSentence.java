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
