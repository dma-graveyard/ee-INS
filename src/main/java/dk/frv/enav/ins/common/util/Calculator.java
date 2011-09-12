package dk.frv.enav.ins.common.util;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.common.Heading;

public class Calculator {

	/**
	 * Find range between two points given heading
	 * @param pos1
	 * @param pos2
	 * @param heading
	 */
	public static double range(GeoLocation pos1, GeoLocation pos2, Heading heading) {
		double meters;
		if (heading == Heading.RL) {
			meters = pos1.getRhumbLineDistance(pos2);
		} else {
			meters = pos1.getGeodesicDistance(pos2);
		}
		return Converter.metersToNm(meters);
	}
	
	/**
	 * Calculate bearing between two points given heading
	 * @param pos1
	 * @param pos2
	 * @param heading
	 * @return
	 */
	public static double bearing(GeoLocation pos1, GeoLocation pos2, Heading heading) {
		if (heading == Heading.RL) {
			return pos1.getRhumbLineBearing(pos2);
		} else {
			return pos1.getGeodesicInitialBearing(pos2);
		}

	}
}
