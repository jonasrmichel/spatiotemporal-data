package stdata.simulator;

import java.util.HashMap;
import java.util.Map;

public class SimulationDriver {

	/** Simulation options. */
	int startTime, stopTime;

	String traceDir, graphDir, indexDir, statsDir;

	double phenomenaSensingRange;
	int phenomenaSensingInterval;

	String[] mobilityHosts, mobilityPhenomena;

	int[] trajectoryTemporalResolution;
	double[] trajectorySpatialResolution;

	/** The current simulation time. */
	int currentTime;

	/** Map of moving objects' identifiers to moving objects. */
	Map<Integer, Host> hosts;
	Map<Integer, Phenomenon> phenomena;

	/** The location managers. */
	LocationManager hostLocationManager, phenomenonLocationManager;

	public SimulationDriver(int startTime, int stopTime, String traceDir,
			String graphDir, String indexDir, String statsDir,
			double phenomenaSensingRange, int phenomenaSensingInterval,
			String[] mobilityHosts, String[] mobilityPhenomena,
			int[] trajectoryTemporalResolution,
			double[] trajectorySpatialResolution) {
		this.startTime = startTime;
		this.stopTime = stopTime;

		this.traceDir = traceDir;
		this.graphDir = graphDir;
		this.indexDir = indexDir;
		this.statsDir = statsDir;

		this.phenomenaSensingRange = phenomenaSensingRange;
		this.phenomenaSensingInterval = phenomenaSensingInterval;

		this.mobilityHosts = mobilityHosts;
		this.mobilityPhenomena = mobilityPhenomena;

		this.trajectoryTemporalResolution = trajectoryTemporalResolution;
		this.trajectorySpatialResolution = trajectorySpatialResolution;

		hosts = new HashMap<Integer, Host>(SimulationManager.numHosts);
		phenomena = new HashMap<Integer, Phenomenon>(
				SimulationManager.numPhenomena);

	}

	private void initLocationManager(LocationManager locationManager,
			int numHosts, String mobility) {
		locationManager = new LocationManager(Integer.toString(numHosts) + "-"
				+ mobility + ".txt");
	}
}
