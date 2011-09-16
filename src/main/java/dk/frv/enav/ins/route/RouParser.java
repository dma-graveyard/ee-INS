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
package dk.frv.enav.ins.route;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import dk.frv.ais.geo.GeoLocation;
import dk.frv.enav.ins.EeINS;
import dk.frv.enav.ins.common.FormatException;
import dk.frv.enav.ins.common.Heading;
import dk.frv.enav.ins.common.util.ParseUtils;
import dk.frv.enav.ins.settings.NavSettings;

/**
 * Class for paring ROU format route files
 */
public class RouParser {

	private String line = null;
	private BufferedReader reader;
	private Route route = new Route();
	private Double sogDefault = null;
	private RouteWaypoint lastWp = null;
	private RouteLeg lastLeg = null;
	private int wpCount = 1;

	public RouParser() {

	}
	
	public Route parse(File file) throws IOException, RouteLoadException {
		reader = new BufferedReader(new FileReader(file));
		while ((line = reader.readLine()) != null) {
			try {
				// Check for header
				if (line.startsWith("ROUTE HEADER INFORMATION")) {
					parseHeader();
				}
				else if (line.startsWith("WAYPOINT")) {
					parseWp();
				}
			} catch (FormatException e) {
				throw new RouteLoadException("ROU Parse error: " + e.getMessage());
			}
		}		
		if (lastWp != null) {
			lastWp.setOutLeg(null);
		}
		return route;
	}
	
	private void parseWp() throws IOException, RouteLoadException, FormatException {
		RouteWaypoint wp = new RouteWaypoint();
		RouteLeg outLeg = new RouteLeg();
		wp.setInLeg(lastLeg);
		wp.setOutLeg(outLeg);
		outLeg.setStartWp(wp);
		if (lastLeg != null) {
			lastLeg.setEndWp(wp);			
		}
		lastLeg = outLeg;
		lastWp = wp;
		Double lat = null;
		Double lon = null;
	
		// Set defaults
		wp.setName(String.format("%03d", wpCount));
		NavSettings navSettings = EeINS.getSettings().getNavSettings();
		wp.setSpeed(sogDefault);	
		wp.setTurnRad(navSettings.getDefaultTurnRad());
		outLeg.setXtdPort(navSettings.getDefaultXtd());
		outLeg.setXtdStarboard(navSettings.getDefaultXtd());
		
		while ((line = reader.readLine()) != null) {
			String str = line.trim();
			if (str.length() == 0) {
				break;
			}
			String[] parts = parsePair(str);
			if (parts[0].equals("Name")) {
				wp.setName(parts[1]);
			}
			else if (parts[0].startsWith("Latitude")) {
				lat = ParseUtils.parseDouble(parts[1]);
			}
			else if (parts[0].startsWith("Longitude")) {
				lon = ParseUtils.parseDouble(parts[1]);
			}
			else if (parts[0].startsWith("Turn radius")) {
				wp.setTurnRad(ParseUtils.parseDouble(parts[1]));
			}
			else if (parts[0].startsWith("SOG")) {
				wp.setSpeed(ParseUtils.parseDouble(parts[1]));
			}
			else if (parts[0].startsWith("Leg type")) {
				if (parts[1].startsWith("1")) {
					outLeg.setHeading(Heading.RL);
				} else {
					outLeg.setHeading(Heading.GC);
				}				
			}
			else if (parts[0].startsWith("Circles")) {
				String[] circleItems = StringUtils.split(parts[1]);
				if (circleItems.length != 5) {
					throw new RouteLoadException("Error parsing ROU circles: " + parts[1]);
				}
				outLeg.setXtdPort(ParseUtils.parseDouble(circleItems[2]));
				outLeg.setXtdStarboard(ParseUtils.parseDouble(circleItems[4]));
			}
		}		
		
		// Set position
		if (lat == null || lon == null) {
			throw new RouteLoadException("Missing latitude/longitude for WP " + wp.getName());
		}
		wp.setPos(new GeoLocation(lat, lon));
		wp.setSpeed(outLeg.getSpeed());
		
		route.getWaypoints().add(wp);
		
		wpCount++;
	}

	private void parseHeader() throws IOException, RouteLoadException, FormatException {
		while ((line = reader.readLine()) != null) {
			String str = line.trim();
			if (str.length() == 0) {
				break;
			}
			String[] parts = parsePair(str);
			if (parts[0].startsWith("Route name")) {
				route.setName(parts[1]);
			}
			else if (parts[0].startsWith("SOG default")) {
				sogDefault = ParseUtils.parseDouble(parts[1]);
			}

		}
		// Set default name if none given
		if (route.getName() == null) {
			route.setName("NO NAME");
		}
		
		if (sogDefault == null) {
			sogDefault = EeINS.getSettings().getNavSettings().getDefaultSpeed();
		}
	}

	private static String[] parsePair(String str) throws RouteLoadException {
		String[] parts = StringUtils.splitByWholeSeparator(str, ": ");
		if (parts.length != 2) {
			throw new RouteLoadException("Error in ROU key value pair: " + str);
		}
		return parts;
	}

}
