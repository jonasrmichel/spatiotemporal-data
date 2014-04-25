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
	public static final String HOST_OBJECT_TYPE = "host";
	public static final String PHENOMENON_OBJECT_TYPE = "phenomenon";

	/** Verbose mode. */
	public static boolean verbose = true;
	/** Debug mode. */
	public static boolean debug = false;
	/** Simulation start time (offset in mobility trace files). */
	public static int startTime = 0;
	/** Simulation stop time. */
	public static int stopTime = 60 * 60 * 1; // 1 hour

	/** Mobility trace file directory (used by LocationManager). */
	public static String traceDir = "../traces";
	/** Graph database storage directory (used by Hosts). */
	public static String graphDir = "../graphs";
	/** Measurement logfile directory. */
	public static String logDir = "../logs";

	/** Phenomena sensing range (meters). */
	public static double phenomenaSensingRange = 30;
	/** Phenomena sensing interval (seconds). */
	public static int phenomenaSensingInterval = 60 * 5; // 5 minutes

	/** Number of mobile hosts. */
	public static int numHosts = 100;

	/** Number of mobile phenomena. */
	public static int numPhenomena = 750;

	/**
	 * Degree of phenomena mobility. { "slow", "medium", "fast" }
	 */
	public static String[] mobilityPhenomena = { "slow", "medium", "fast" };
	public static String[] mobilityPhenomenaSkip = null;
	/**
	 * Degree of host mobility. { "slow", "medium", "fast" }
	 */
	public static String[] mobilityHosts = { "slow", "medium", "fast" };
	public static String[] mobilityHostsSkip = null;

	/**
	 * Trajectory temporal resolution (seconds).
	 * 
	 * { low, medium, high, very high }
	 */
	public static int[] trajectoryTemporalResolution = { 10 * 60, 5 * 60, 60, 5 };
	/**
	 * Trajectory spatial resolution (meters).
	 * 
	 * { low, medium, high, very high }
	 */
	public static double[] trajectorySpatialResolution = { 50, 25, 10, 5 };

	public static int[] trajectoryResolutionIndexSkip = null;

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

			if (line.hasOption("start-time"))
				startTime = Integer.parseInt(line.getOptionValue("start-time"));

			if (line.hasOption("stop-time"))
				stopTime = Integer.parseInt(line.getOptionValue("stop-time"));

			if (line.hasOption("trace-dir"))
				traceDir = line.getOptionValue("trace-dir");

			if (line.hasOption("graph-dir"))
				graphDir = line.getOptionValue("graph-dir");

			if (line.hasOption("log-dir"))
				logDir = line.getOptionValue("log-dir");

			if (line.hasOption("sensing-range"))
				phenomenaSensingRange = Double.parseDouble(line
						.getOptionValue("sensing-range"));

			if (line.hasOption("sensing-interval"))
				phenomenaSensingInterval = Integer.parseInt(line
						.getOptionValue("sensing-interval"));

			if (line.hasOption("num-hosts"))
				numHosts = Integer.parseInt(line.getOptionValue("num-hosts"));

			if (line.hasOption("num-phenomena"))
				numPhenomena = Integer.parseInt(line
						.getOptionValue("num-phenomena"));

			if (line.hasOption("mobility-phenomena"))
				mobilityPhenomena = line.getOptionValues("mobility-phenomena");

			if (line.hasOption("mobility-phenomena-skip"))
				mobilityPhenomenaSkip = line
						.getOptionValues("mobility-phenomena-skip");

			if (line.hasOption("mobility-hosts"))
				mobilityHosts = line.getOptionValues("mobility-hosts");

			if (line.hasOption("mobility-hosts-skip"))
				mobilityHostsSkip = line.getOptionValues("mobility-hosts-skip");

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

			if (line.hasOption("resolution-skip")) {
				String[] trajectoryResolutionIndexSkip = line
						.getOptionValues("resolution-skip");
				SimulationManager.trajectoryResolutionIndexSkip = new int[trajectoryResolutionIndexSkip.length];
				for (int i = 0; i < trajectoryResolutionIndexSkip.length; i++)
					SimulationManager.trajectoryResolutionIndexSkip[i] = Integer
							.parseInt(trajectoryResolutionIndexSkip[i]);
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
		options.addOption("b", "start-time", true, "simulation start time");
		options.addOption("e", "stop-time", true, "simulation stop time");

		options.addOption("t", "trace-dir", true, "mobility trace directory");
		options.addOption("g", "graph-dir", true,
				"graph database home directory");
		options.addOption("l", "log-dir", true, "result logfile directory");

		options.addOption("r", "sensing-range", true,
				"phenomena sensing range (meters)");
		options.addOption("i", "sensing-interval", true,
				"phenomena sensing interval (seconds)");

		options.addOption("n", "num-hosts", true, "number of mobile hosts");
		options.addOption("p", "num-phenomena", true,
				"number of mobile phenomena");

		options.addOption(OptionBuilder
				.withArgName("P")
				.withLongOpt("mobility-phenomena")
				.hasArgs()
				.withDescription(
						"degrees of phenomena mobility to iterate over (slow, medium, fast)")
				.create());
		options.addOption(OptionBuilder
				.withArgName("x")
				.withLongOpt("mobility-phenomena-skip")
				.hasArgs()
				.withDescription(
						"degrees of phenomena mobility to SKIP over (slow, medium, fast)")
				.create());
		options.addOption(OptionBuilder
				.withArgName("H")
				.withLongOpt("mobility-hosts")
				.hasArgs()
				.withDescription(
						"degrees of host mobility to iterate over (slow, medium, fast)")
				.create());
		options.addOption(OptionBuilder
				.withArgName("y")
				.withLongOpt("mobility-hosts-skip")
				.hasArgs()
				.withDescription(
						"degrees of host mobility to SKIP over (slow, medium, fast)")
				.create());

		options.addOption(OptionBuilder
				.withArgName("T")
				.withLongOpt("temporal-resolution")
				.hasArgs()
				.withDescription(
						"trajectory temporal resolutions to iterate over (seconds)")
				.create());
		options.addOption(OptionBuilder
				.withArgName("S")
				.withLongOpt("spatial-resolution")
				.hasArgs()
				.withDescription(
						"trajectory spatial resolutions to iterate over (meters)")
				.create());
		options.addOption(OptionBuilder.withArgName("z")
				.withLongOpt("resolution-skip").hasArgs()
				.withDescription("trajectory resolution indices to SKIP over")
				.create());

		return options;
	}

	private static void simulate() {
		new SimulationDriver(startTime, stopTime, traceDir, graphDir, logDir,
				phenomenaSensingRange, phenomenaSensingInterval, numHosts,
				numPhenomena, mobilityHosts, mobilityHostsSkip,
				mobilityPhenomena, mobilityPhenomenaSkip,
				trajectoryTemporalResolution, trajectorySpatialResolution,
				trajectoryResolutionIndexSkip).runSimulations();
	}
}
