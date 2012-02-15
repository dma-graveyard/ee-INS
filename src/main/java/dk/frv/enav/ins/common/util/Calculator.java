/*
 * Copyright 2011 Danish Maritime Authority. All rights reserved.
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
 * THIS SOFTWARE IS PROVIDED BY Danish Maritime Authority ``AS IS'' 
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
 * either expressed or implied, of Danish Maritime Authority.
 * 
 */
package dk.frv.enav.ins.common.util;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.common.Heading;

/**
 * Class for doing different common calculations
 */
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
