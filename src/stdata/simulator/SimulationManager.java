package stdata.simulator;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The SimulationManager collects command line parameters and runs simulations
 * each driven by the SimulationDriver.
 * 
 */
public class SimulationManager {

	/** Simulation options. */
	public static final int numHosts = 100;
	public static final int numPhenomena = 1500;

	/** Verbose mode. */
	public static boolean verbose = false;
	/** Debug mode. */
	public static boolean debug = false;
	/** Simulation start time (offset in mobility trace files). */
	public static int startTime = 0;
	/** Simulation stop time. */
	public static int stopTime = 60 * 60 * 8;

	/** Mobility trace file directory (used by LocationManager). */
	public static String traceDir = "../traces";
	/** Graph database storage directory (used by Hosts). */
	public static String graphDir = "../graphs";
	/** Index storage directory (used by Hosts). */
	public static String indexDir = "../index";
	/** Statistics logging directory. */
	public static String statsDir = "../stats";

	/** Phenomena sensing range (meters). */
	public static double phenomenaSensingRange = 30;
	/** Phenomena sensing interval (seconds). */
	public static int phenomenaSensingInterval = 60;

	/** Degree of host mobility. */
	public static String[] mobilityHosts = { "slow", "medium", "fast" };
	/** Degree of phenomena mobility. */
	public static String[] mobilityPhenomena = { "slow", "medium", "fast" };

	/** Trajectory temporal resolution (seconds). */
	public static int[] trajectoryTemporalResolution = { 10 };
	/** Trajectory spatial resolution (meters). */
	public static double[] trajectorySpatialResolution = { 10 };

	public static void main(String[] args) {
		try {
			// create the parser
			CommandLineParser parser = new BasicParser();

			// parse the command line arguments
			Options options = getOptions();
			CommandLine line = parser.parse(getOptions(), args);

			// configure simulation options
			if (line.hasOption("help")) {
				new HelpFormatter().printHelp("SimulationManager", options);
				System.exit(0);
			}

			if (line.hasOption("verbose"))
				verbose = true;

			if (line.hasOption("debug"))
				debug = true;

			if (line.hasOption("start"))
				startTime = Integer.parseInt(line.getOptionValue("start"));

			if (line.hasOption("stop"))
				startTime = Integer.parseInt(line.getOptionValue("stop"));

			if (line.hasOption("trace-dir"))
				traceDir = line.getOptionValue("trace-dir");

			if (line.hasOption("graph-dir"))
				graphDir = line.getOptionValue("graph-dir");

			if (line.hasOption("index-dir"))
				indexDir = line.getOptionValue("index-dir");

			if (line.hasOption("stats-dir"))
				statsDir = line.getOptionValue("stats-dir");

			if (line.hasOption("sensing-range"))
				phenomenaSensingRange = Double.parseDouble(line
						.getOptionValue("sensing-range"));

			if (line.hasOption("sensing-interval"))
				phenomenaSensingInterval = Integer.parseInt(line
						.getOptionValue("sensing-interval"));

			if (line.hasOption("mobility-hosts"))
				mobilityHosts = line.getOptionValues("mobility-hosts");

			if (line.hasOption("mobility-phenomena"))
				mobilityPhenomena = line.getOptionValues("mobility-phenomena");

			if (line.hasOption("temporal-resolution")) {
				String[] trajectoryTemporalResolution = line
						.getOptionValues("temporal-resolution");
				SimulationManager.trajectoryTemporalResolution = new int[trajectoryTemporalResolution.length];
				for (int i = 0; i < trajectoryTemporalResolution.length; i++)
					SimulationManager.trajectoryTemporalResolution[i] = Integer
							.parseInt(trajectoryTemporalResolution[i]);
			}

			if (line.hasOption("spatial-resolution")) {
				String[] trajectorySpatialResolution = line
						.getOptionValues("spatial-resolution");
				SimulationManager.trajectorySpatialResolution = new double[trajectorySpatialResolution.length];
				for (int i = 0; i < trajectorySpatialResolution.length; i++)
					SimulationManager.trajectorySpatialResolution[i] = Double
							.parseDouble(trajectorySpatialResolution[i]);
			}

			// begin simulations
			simulate();

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Command line argument parsing failed: "
					+ e.getMessage());
		}
	}

	private static Options getOptions() {
		Options options = new Options();

		options.addOption("h", "help", false, "print this message");
		options.addOption("v", "verbose", false, "be extra verbose");
		options.addOption("d", "debug", false, "print debugging information");
		options.addOption("b", "start", true, "simulation start time");
		options.addOption("e", "stop", true, "simulation stop time");

		options.addOption("t", "trace-dir", true, "mobility trace directory");
		options.addOption("g", "graph-dir", true,
				"graph database home directory");
		options.addOption("I", "index-dir", true,
				"graph database index (lucene) home directory");
		options.addOption("s", "stats-dir", true,
				"statistics logging directory");

		options.addOption("r", "sensing-range", true,
				"phenomena sensing range (meters)");
		options.addOption("i", "sensing-interval", true,
				"phenomena sensing interval (seconds)");

		options.addOption(OptionBuilder
				.withArgName("H")
				.withLongOpt("mobility-hosts")
				.hasArgs()
				.withDescription(
						"degrees of host mobility to iterate over (0 slow, 1 medium, 2 fast)")
				.create("host mobility degrees"));
		options.addOption(OptionBuilder
				.withArgName("P")
				.withLongOpt("mobility-phenomena")
				.hasArgs()
				.withDescription(
						"degrees of phenomena mobility to iterate over (0 slow, 1 medium, 2 fast)")
				.create("phenomena mobility degrees"));

		options.addOption(OptionBuilder
				.withArgName("T")
				.withLongOpt("temporal-resolution")
				.hasArgs()
				.withDescription(
						"trajectory temporal resolutions to iterate over (seconds)")
				.create("temporal resolutions"));
		options.addOption(OptionBuilder
				.withArgName("S")
				.withLongOpt("spatial-resolution")
				.hasArgs()
				.withDescription(
						"trajectory spatial resolutions to iterate over (meters)")
				.create("spatial resolutions"));

		return options;
	}

	private static void simulate() {
		// TODO Auto-generated method stub
	}
}