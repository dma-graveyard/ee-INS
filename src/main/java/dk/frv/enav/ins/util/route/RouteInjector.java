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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import dk.frv.ais.proprietary.GatehouseFactory;
import dk.frv.ais.reader.AisReader;
import dk.frv.ais.reader.AisStreamReader;
import dk.frv.enav.ins.common.text.Formatter;

public class RouteInjector {

	private String inFilename;
	private String outFilename;
	private TrackCollector trackCollector;
	private List<TimePoint> route;
	private long mmsi;

	public RouteInjector(String inFilename, String outFilename, long mmsi) {
		this.inFilename = inFilename;
		this.outFilename = outFilename;
		this.mmsi = mmsi;
		trackCollector = new TrackCollector(mmsi);
	}

	public void collectTrack() throws Exception {
		// Make reader for input file
		AisReader aisReader = new AisStreamReader(new FileInputStream(inFilename));
		// Register handler
		aisReader.registerHandler(trackCollector);
		// Register proprietary handler (optional)
		aisReader.addProprietaryFactory(new GatehouseFactory());
		// Start reader thread
		aisReader.start();
		// Wait for thread to finish
		aisReader.join();
	}

	public void generateRoute() {
		// Create route generator
		IRouteGenerator generator;
		// generator = new SimpleRouteGenerator();
		generator = new ApproxRouteGenerator();

		// Generate route
		route = generator.generateRoute(trackCollector.getSortedTrack());
	}

	public void saveRoute() throws Exception {
		FileWriter outFile = new FileWriter("generated_route.txt");
		PrintWriter out = new PrintWriter(outFile);
		out.println("Generated route");
		int i = 0;
		for (TimePoint point : route) {
			List<String> fields = new ArrayList<String>();
			fields.add(String.format("WP_%03d", i));
			fields.add(Formatter.latToPrintable(point.getLatitude()));
			fields.add(Formatter.lonToPrintable(point.getLongitude()));
			fields.add("17.00");
			fields.add("1");
			fields.add("0.100");
			fields.add("0.500");
			out.println(StringUtils.join(fields.iterator(), "\t"));
			i++;
		}
		outFile.close();
	}

	public void injectBroadcasts() throws Exception {
		// Create handler
		AisRouteInject injector = new AisRouteInject(outFilename, route, mmsi);

		// Make reader for input file
		AisReader aisReader = new AisStreamReader(new FileInputStream(inFilename));
		// Register handler
		aisReader.registerHandler(injector);
		// Register proprietary handler (optional)
		aisReader.addProprietaryFactory(new GatehouseFactory());
		// Start reader thread
		aisReader.start();
		// Wait for thread to finish
		aisReader.join();
	}

	private static void inject(String inFilename, String outFilename, long mmsi) throws Exception {
		System.out.println("Generate for MMSI: " + mmsi);
		RouteInjector routeInjector = new RouteInjector(inFilename, outFilename, mmsi);
		System.out.println("First pass - collect track");
		routeInjector.collectTrack();
		System.out.println("Generate route");
		routeInjector.generateRoute();
		System.out.println("Save route");
		routeInjector.saveRoute();
		System.out.println("Inject broadcasts");
		routeInjector.injectBroadcasts();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Route Injector");
		if (args.length < 3) {
			usage();
		}
		String inFilename = args[0];
		String outFilename = args[1];

		String[] mmsis = args[2].split(",");
		String tmpInFileName = inFilename;
		String tmpOutFilename = "tmp";
		for (int i = 0; i < mmsis.length; i++) {
			Long mmsi = Long.parseLong(mmsis[i]);
			if (i == (mmsis.length - 1)) {
				tmpOutFilename = outFilename;
			} else {
				tmpOutFilename += "_" + mmsi;
			}
			inject(tmpInFileName, tmpOutFilename, mmsi);
			if (i > 0) {
				(new File(tmpInFileName)).delete();
			}
			tmpInFileName = tmpOutFilename;
		}

	}

	public static void usage() {
		System.out.println("Usage: RouteInjector <infile> <outfile> <mmsi>");
		System.exit(0);
	}

}
