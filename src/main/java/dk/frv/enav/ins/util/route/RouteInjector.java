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
