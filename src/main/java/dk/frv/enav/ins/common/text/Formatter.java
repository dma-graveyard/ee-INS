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
package dk.frv.enav.ins.common.text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dk.frv.enav.ins.ais.AisAdressedRouteSuggestion.Status;
import dk.frv.enav.ins.common.Heading;

/**
 * Utility class for doing different formatting
 */
public class Formatter {
	
	private static SimpleDateFormat tzConvert = new SimpleDateFormat("Z");
	private static SimpleDateFormat shortDateTime = new SimpleDateFormat("MM/dd HH:mm:ss");
	private static SimpleDateFormat longDateTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	//private static final TimeZone tzGMT = TimeZone.getTimeZone("GMT+0000");

	public static String formatShortDateTime(Date date) {
		if (date == null) {
			return "N/A";
		}
		return shortDateTime.format(date) + "(" + getTzNumber(date) + ")";		
	}
	
	public static String formatLongDateTime(Date date){
		if(date == null){
			return "N/A";
		}
		return longDateTime.format(date)+ "(" + getTzNumber(date) + ")";
	}
	
	public static String formatTime(Long time) {
		if (time == null) {
			return "N/A";
		}
		long secondInMillis = 1000;
		long minuteInMillis = secondInMillis * 60;
		long hourInMillis = minuteInMillis * 60;
		long dayInMillis = hourInMillis * 24;

		long elapsedDays = time / dayInMillis;
		time = time % dayInMillis;
		long elapsedHours = time / hourInMillis;
		time = time % hourInMillis;
		long elapsedMinutes = time / minuteInMillis;
		time = time % minuteInMillis;
		long elapsedSeconds = time / secondInMillis;

		if (elapsedDays > 0) {
			return String.format("%02d:%02d:%02d:%02d", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
		} else {
			return String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
		}
	}
		
	public static String getTzNumber(Date date) {
		String tz = tzConvert.format(date);
		double tzVal = Double.parseDouble(tz) / 100;
		// TOOD Should be able to handle decimal zones
		return Long.toString((long)tzVal);
	}
	
	public static String latToPrintable(double lat) {
		String ns = "N";
		if (lat < 0) {
			ns = "S";
			lat *= -1;
		}
		int hours = (int)lat;
		lat -= hours;
		lat *= 60;
		String latStr = String.format(Locale.US, "%3.3f", lat);
		while (latStr.indexOf('.') < 2) {
			latStr = "0" + latStr;
		}		
		return String.format(Locale.US, "%02d %s%s", hours, latStr, ns);
	}
	
	public static String lonToPrintable(double lon) {
		String ns = "E";
		if (lon < 0) {
			ns = "W";
			lon *= -1;
		}
		int hours = (int)lon;
		lon -= hours;
		lon *= 60;		
		String lonStr = String.format(Locale.US, "%3.3f", lon);
		while (lonStr.indexOf('.') < 2) {
			lonStr = "0" + lonStr;
		}		
		return String.format(Locale.US, "%03d %s%s", hours, lonStr, ns);
	}
	
	public static String formatString(String str, String defaultStr) {
		if (str == null) return defaultStr;
		return str;
	}
	
	public static String formatString(String str) {
		return formatString(str, "");
	}
		
	public static String formatSpeed(Double s) {
		if (s == null) return "N/A";
		return formatDouble(s, 2) + " kn";
	}
	
	public static String formatWindSpeed(Double s) {
		if (s == null) return "N/A";
		return formatDouble(s, 2) + " m/s";
	}
	
	public static String formatWindSpeed(Double s, int decimals) {
		if (s == null) return "N/A";
		return formatDouble(s, decimals) + " m/s";
	}
	
	public static String formatCurrentSpeed(Double s) {
		if (s == null) return "N/A";
		return formatDouble(s, 2) + " kn";
	}
	
	public static String formatCurrentSpeed(Double s, int decimals) {
		if (s == null) return "N/A";
		return formatDouble(s, decimals) + " kn";
	}
	
	public static String formatMeters(Double dist) {
		return formatMeters(dist, 0);
	}
	
	public static String formatMeters(Double dist, int decimals) {
		if (dist == null) return "N/A";
		return formatDouble(dist, decimals) + " m";
	}
		
	public static String formatDistNM(Double dist, int decimals) {
		if (dist == null) return "N/A";
		return formatDouble(dist, decimals) + " NM";
	}

	public static String formatDistNM(Double dist) {
		return formatDistNM(dist, 2);
	}
	
	public static String formatDegrees(Double dgs, int decimals) {
		if (dgs == null) return "N/A";
		return addZeroesToDecimalNumber(formatDouble(dgs, decimals),3) + "°";		
	}
	
	public static String formatHeading(Heading heading) {
		if (heading == null) return "N/A";
		return heading.name();
	}
	
	public static String formatRot(Double rot) {
		if (rot == null) return "N/A";
		return String.format(Locale.US, "%.1f", rot) + "°/min";
	}
	
	public static String formatLong(Long l) {
		if (l == null) {
			return "N/A";
		}
		return Long.toString(l);
	}
	
	public static String formatDouble(Double d, int decimals) {
		if (d == null) {
			return "N/A";
		}
		if (decimals == 0) {
			return String.format(Locale.US, "%d", Math.round(d));
		}
		String format = "%." + decimals + "f";
		return String.format(Locale.US, format, d);
	}
	
	public static String formatAisRouteType(int type) {
		switch (type) {
		case 1:
			return "Mandatory";
		case 2:
			return "Recommended";
		case 3:
			return "Alternative";
		case 4:
			return "Recommended through ice";
		case 5:
			return "Ship route";
		default:
			return "Unknown";
		}
	}
	
	public static String formatRouteSuggestioStatus(Status status) {
		switch (status) {
		case PENDING:
			return "Pending";
		case ACCEPTED:
			return "Accepted";
		case REJECTED:
			return "Rejected";
		case NOTED:
			return "Noted";
		case IGNORED:
			return "Ignored";
		default:
			return "Unknown";
		}
	}

	private static String addZeroesToDecimalNumber(String str, int num) {
		int lengthBeforeDecimal = str.indexOf('.');
		if (lengthBeforeDecimal < 0) {
			lengthBeforeDecimal = str.length();
		}
		for (int i=0; i < num - lengthBeforeDecimal; i++) {
			str = "0" + str;
		}
		return str;
	}
	
}
