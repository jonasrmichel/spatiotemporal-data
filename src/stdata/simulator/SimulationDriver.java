package stdata.simulator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import stdata.datamodel.vertices.MeasuredDatum.TriggerType;
import stdata.simulator.measurement.Logger;
import stdata.simulator.measurement.ReducibleRunningStatistics;
import stdata.simulator.measurement.RunningStatistics;
import stdata.simulator.measurement.RunningStatisticsMap;
import stdata.simulator.movingobjects.Host;
import stdata.simulator.movingobjects.IHostDelegate;
import stdata.simulator.movingobjects.MovingObject;
import stdata.simulator.movingobjects.Phenomenon;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public class SimulationDriver implements IHostDelegate {

	/** Simulation options. */
	int startTime, stopTime;

	String traceDir, graphDir, indexDir, logDir;

	double phenomenaSensingRange;
	int phenomenaSensingInterval;

	String[] mobilityHosts, mobilityPhenomena;

	int[] trajectoryTemporalResolution;
	double[] trajectorySpatialResolution;
	int numSTResolutions;

	/** Map of moving objects' identifiers to moving objects. */
	Map<Integer, Host> hosts;
	Map<Integer, Phenomenon> phenomena;

	/** The moving object database. */
	MovingObjectDatabase database;

	/** The location managers. */
	LocationManager hostLocationManager, phenomenonLocationManager;

	public SimulationDriver(int startTime, int stopTime, String traceDir,
			String graphDir, String indexDir, String logDir,
			double phenomenaSensingRange, int phenomenaSensingInterval,
			String[] mobilityHosts, String[] mobilityPhenomena,
			int[] trajectoryTemporalResolution,
			double[] trajectorySpatialResolution) {
		this.startTime = startTime;
		this.stopTime = stopTime;

		this.traceDir = traceDir;
		this.graphDir = graphDir;
		this.indexDir = indexDir;
		this.logDir = logDir;

		this.phenomenaSensingRange = phenomenaSensingRange;
		this.phenomenaSensingInterval = phenomenaSensingInterval;

		this.mobilityHosts = mobilityHosts;
		this.mobilityPhenomena = mobilityPhenomena;

		this.trajectoryTemporalResolution = trajectoryTemporalResolution;
		this.trajectorySpatialResolution = trajectorySpatialResolution;

		hosts = new HashMap<Integer, Host>(SimulationManager.NUM_HOSTS);
		phenomena = new HashMap<Integer, Phenomenon>(
				SimulationManager.NUM_PHENOMENA);

		// ensure that
		// |trajectoryTemporalResolution| == |trajectorySpatialResolution|
		if (trajectoryTemporalResolution.length != trajectorySpatialResolution.length) {
			System.err
					.println("Error: trajectory spatial and temporal resolution vectors "
							+ "must be of equal length.");
			System.exit(1);
		}

		numSTResolutions = trajectoryTemporalResolution.length;
	}

	/**
	 * Returns a location manager loaded with a mobility trace file.
	 * 
	 * @param numObjects
	 * @param mobility
	 * @param startTime
	 * @param stopTime
	 * @return a fully loaded location manager.
	 */
	private LocationManager loadMobilityTraces(int numObjects, String mobility,
			int startTime, int stopTime) {
		if (SimulationManager.verbose)
			Util.report(
					SimulationDriver.class,
					"loading mobility trace file: " + " ["
							+ Integer.toString(numObjects) + " objects]" + " ["
							+ mobility + " mobility]" + " ["
							+ Integer.toString(startTime) + " start,"
							+ Integer.toString(stopTime) + " stop]");

		return new LocationManager(traceDir + File.separator
				+ Integer.toString(numObjects) + "-" + mobility + ".txt",
				startTime, stopTime);
	}

	private void loadHosts(int temporalResolution, double spatialResolution,
			String simulationId) {
		if (SimulationManager.verbose)
			Util.report(
					SimulationDriver.class,
					"loading Host mobile objects:" + " ["
							+ Integer.toString(temporalResolution)
							+ " temporal resolution]" + " ["
							+ Double.toString(spatialResolution)
							+ " spatial resolution]");

		// clear any existing host moving objects
		// (assuming they've already been properly shutdown)
		hosts.clear();

		// clear out simulation-specific graph and index subdirectories
		String simulationGraphDir = graphDir + File.separator + simulationId;
		String simulationIndexDir = indexDir + File.separator + simulationId;

		clearDirectory(simulationGraphDir);
		clearDirectory(simulationIndexDir);

		// load the required number of host moving objects
		int numHosts = hostLocationManager.getNumTraces();
		for (int i = 0; i < numHosts; i++)
			hosts.put(i, new Host(i, SimulationManager.HOST_OBJECT_TYPE,
					hostLocationManager, database, this, simulationGraphDir,
					simulationIndexDir, logDir, simulationId,
					SimulationManager.phenomenaSensingRange,
					SimulationManager.phenomenaSensingInterval,
					temporalResolution, spatialResolution));
	}

	private void loadPhenomena() {
		if (SimulationManager.verbose)
			Util.report(SimulationDriver.class,
					"loading Phenomenon mobile objects");

		// clear any existing phenomenon moving objects
		// (assuming they've already been properly shutdown)
		phenomena.clear();

		// load the required number of phenomenon moving objects
		int numPhenomena = phenomenonLocationManager.getNumTraces();
		for (int i = 0; i < numPhenomena; i++)
			phenomena.put(i, new Phenomenon(i,
					SimulationManager.PHENOMENON_OBJECT_TYPE,
					phenomenonLocationManager, database));
	}

	private void clearDirectory(String dir) {
		try {
			File folder = new File(dir);

			// delete any existing directory at this path
			if (folder.exists()) {
				if (!folder.isDirectory()) {
					System.err.println("Error: " + dir + " is not a directory");
					System.exit(1);
				}

				FileUtils.deleteDirectory(folder);
			}

			// create a new empty directory at this path
			if (!folder.mkdir()) {
				System.err.println("Error: could not create directory "
						+ folder.getAbsolutePath());
				System.exit(1);
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void runSimulations() {
		// each simulation has a unique identifier
		String simulationId;

		// maps to hold simulation-level running and overall statistics for
		// spatially- and temporally-modulated data.
		RunningStatisticsMap<ReducibleRunningStatistics> spatiallyModulatedStatistics, temporallyModulatedStatistics;

		for (String pMobility : mobilityPhenomena) {
			// load the phenomenon mobility traces
			phenomenonLocationManager = loadMobilityTraces(
					SimulationManager.NUM_PHENOMENA, pMobility, startTime,
					stopTime);

			for (String hMobility : mobilityHosts) {
				// load the host mobility traces
				hostLocationManager = loadMobilityTraces(
						SimulationManager.NUM_HOSTS, hMobility, startTime,
						stopTime);

				for (int stResolution = 0; stResolution < numSTResolutions; stResolution++) {
					// create the simulation's unique identifier
					// hm_<host mobility>_pm_<phenemonon
					// mobility>_stres_<space-time resolution>
					simulationId = "hm_" + hMobility + "_pm_" + pMobility
							+ "_stres_" + Integer.toString(stResolution);

					// clear any existing logging info for this simulation
					Logger.clearLogs(logDir, simulationId);

					// create the simulation's simulation-level statistics maps
					spatiallyModulatedStatistics = new RunningStatisticsMap<ReducibleRunningStatistics>(
							ReducibleRunningStatistics.class,
							RunningStatisticsMap.HOST_LEVEL_KEYS);
					temporallyModulatedStatistics = new RunningStatisticsMap<ReducibleRunningStatistics>(
							ReducibleRunningStatistics.class,
							RunningStatisticsMap.HOST_LEVEL_KEYS);

					if (SimulationManager.verbose)
						Util.report(SimulationDriver.class,
								"initiating simulation " + "[" + simulationId
										+ "]");

					// connect to the moving object database
					database = new MovingObjectDatabase(simulationId);

					// load the phenomenon moving objects
					loadPhenomena();

					// load the host moving objects with the required trajectory
					// spatial and temporal resolutions
					loadHosts(trajectoryTemporalResolution[stResolution],
							trajectorySpatialResolution[stResolution],
							simulationId);

					// create a collection containing all of the moving objects
					Collection<MovingObject> movingObjects = new HashSet<MovingObject>();
					movingObjects.addAll(phenomena.values());
					movingObjects.addAll(hosts.values());

					// begin the simulation
					int totalTime = stopTime - startTime;
					int time = startTime;
					while (time <= stopTime) {
						if (SimulationManager.verbose)
							Util.report(
									SimulationDriver.class,
									Float.toString(100 * ((float) time)
											/ totalTime)
											+ "%");

						// advance each moving object to the current time
						for (MovingObject movingObject : movingObjects)
							movingObject.advance(time);

						// instruct each moving object to perform a simulation
						// step
						for (MovingObject movingObject : movingObjects)
							movingObject.step(time);

						for (Host host : hosts.values()) {
							// update the running per-time simulation-level
							// aggregate measurements
							pushSimulationLevelMeasurements(
									spatiallyModulatedStatistics, host,
									TriggerType.SPATIAL);
							pushSimulationLevelMeasurements(
									temporallyModulatedStatistics, host,
									TriggerType.TEMPORAL);

						}

						// log the running per-time simulation-level aggregate
						// measurements
						Logger.appendSimulationMeasurement(logDir,
								simulationId, TriggerType.SPATIAL, time,
								spatiallyModulatedStatistics
										.getRunningStatistics());
						Logger.appendSimulationMeasurement(logDir,
								simulationId, TriggerType.TEMPORAL, time,
								temporallyModulatedStatistics
										.getRunningStatistics());

						// advance the simulation time
						time++;

					} // end simulation

					// log the overall aggregate measurements for this
					// simulation
					Logger.appendOverallMeasurement(logDir,
							TriggerType.SPATIAL,
							spatiallyModulatedStatistics.getRunningStatistics());
					Logger.appendOverallMeasurement(logDir,
							TriggerType.TEMPORAL, temporallyModulatedStatistics
									.getRunningStatistics());

					// shutdown the simulation objects
					for (MovingObject movingObject : movingObjects)
						movingObject.shutdown();

					database.shutdown();

				} // end of spatiotemporal resolution
			} // end of host mobility
		} // end of phenomenon mobility
	}

	/**
	 * Reduces the simulation-level running aggregate measurements with the
	 * provided host's (host-level) running statistics.
	 * 
	 * @param statistics
	 * @param host
	 * @param trigger
	 */
	private void pushSimulationLevelMeasurements(
			RunningStatisticsMap<ReducibleRunningStatistics> statistics,
			Host host, TriggerType trigger) {
		// push the db_size (host datum count) measure manually
		// (it's not tracked as a host-level running statistic)
		statistics.get(RunningStatisticsMap.DB_SIZE_KEY).push(
				(double) host.getDatumCount());

		RunningStatisticsMap<RunningStatistics> hostStatistics = host
				.getStatistics(trigger);
		for (Entry<String, RunningStatistics> entry : hostStatistics
				.getEntries())
			statistics.get(entry.getKey()).reduce(entry.getValue());
	}

	/**
	 * Performs overall aggregate measurements (over the entire simulation).
	 * 
	 * @param trigger
	 *            the type of trajectories to measure.
	 */
	private void executeOverallMeasurements(TriggerType trigger) {
		// TODO
		// db_size (avg, median, minimum, maximum, stdev)
		// size (avg, median, minimum, maximum, stdev)
		// length (avg, median, minimum, maximum, stdev)
		// age (avg, median, minimum, maximum, stdev)
		// dist_h_0 (avg, median, minimum, maximum, stdev)
		// dist_p_0 (avg, median, minimum, maximum, stdev)
	}

	/* IHostDelegate interface implementation. */
	@Override
	public Geoshape getPhenomenonLocation(int identifier, int time) {
		return phenomenonLocationManager.getLocation(identifier, time);
	}
}
