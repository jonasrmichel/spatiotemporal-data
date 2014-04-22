package stdata.simulator.movingobjects;

import java.util.List;

import stdata.datamodel.SpatiotemporalDatabase;
import stdata.datamodel.vertices.HostContext;
import stdata.datamodel.vertices.MeasuredDatum;
import stdata.datamodel.vertices.MeasuredDatum.TriggerType;
import stdata.rules.SpatiallyModulatedTrajectoryRule;
import stdata.rules.TemporallyModulatedTrajectoryRule;
import stdata.simulator.ILocationManager;
import stdata.simulator.IMovingObjectDatabase;
import stdata.simulator.SimulationManager;
import stdata.simulator.measurement.Logger;
import stdata.simulator.measurement.RunningStatistics;
import stdata.simulator.measurement.RunningStatisticsMap;
import stdata.titan.TitanSpatiotemporalDatabase;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.frames.FramedGraph;

public class Host extends MovingObject {
	/** File paths. */
	String graphDir, indexDir, logDir;

	/** The unique identifier of the current simulation. */
	String simulationId;

	/** The range within a nearby phenomenon may be "sensed" by a host. */
	double phenomenaSensingRange;
	/** The interval (seconds) that the host periodically senses phenomena. */
	int phenomenaSensingInterval;

	/** The temporal resolution (seconds) of temporally-modulated trajectories. */
	int trajectoryTemporalResolution;

	/** The spatial resolution (meters) of spatially-modulated trajectories. */
	double trajectorySpatialResolution;

	/** The host's spatiotemporal graph database. */
	SpatiotemporalDatabase<TitanGraph, MeasuredDatum> spatiotemporalDB;

	/**
	 * The host's special context vertex that provides the graph database with
	 * access to the host's location and current time.
	 */
	HostContext hostContext;

	/** The host's simulation delegate. */
	IHostDelegate delegate;

	/**
	 * Maps to hold host-level running statistics for spatially- and temporally-
	 * modulated data.
	 */
	RunningStatisticsMap<RunningStatistics> spatiallyModulatedStatistics,
			temporallyModulatedStatistics;

	/** A running count of the number of stored datums. */
	int datumCount;

	public Host(int identifier, String type, ILocationManager locationManager,
			IMovingObjectDatabase database, IHostDelegate delegate,
			String graphDir, String indexDir, String logDir,
			String simulationId, double phenomenaSensingRange,
			int phenomenaSensingInterval, int trajectoryTemporalResolution,
			double trajectorySpatialResolution) {
		super(identifier, type, locationManager, database);

		this.delegate = delegate;
		this.graphDir = graphDir;
		this.indexDir = indexDir;
		this.logDir = logDir;
		this.simulationId = simulationId;
		this.phenomenaSensingRange = phenomenaSensingRange;
		this.phenomenaSensingInterval = phenomenaSensingInterval;
		this.trajectoryTemporalResolution = trajectoryTemporalResolution;
		this.trajectorySpatialResolution = trajectorySpatialResolution;

		spatiallyModulatedStatistics = new RunningStatisticsMap<RunningStatistics>(
				RunningStatistics.class, RunningStatisticsMap.HOST_LEVEL_KEYS);
		temporallyModulatedStatistics = new RunningStatisticsMap<RunningStatistics>(
				RunningStatistics.class, RunningStatisticsMap.HOST_LEVEL_KEYS);

		datumCount = 0;

		initialize();
	}

	/**
	 * Returns the map of running statistics for the provided trigger.
	 * 
	 * @param trigger
	 * @return
	 */
	public RunningStatisticsMap<RunningStatistics> getStatistics(TriggerType trigger) {
		RunningStatisticsMap<RunningStatistics> statistics = null;
		if (trigger.equals(TriggerType.SPATIAL))
			statistics = spatiallyModulatedStatistics;
		else if (trigger.equals(TriggerType.TEMPORAL))
			statistics = temporallyModulatedStatistics;

		return statistics;
	}

	/**
	 * Returns the trigger-modulated running statistic for the provided key.
	 * 
	 * @param key
	 *            the running statistic key.
	 * @param trigger
	 *            the statistic's modulation type.
	 * @return
	 */
	public RunningStatistics getStatistic(String key, TriggerType trigger) {
		RunningStatisticsMap<RunningStatistics> statistics = null;
		if (trigger.equals(TriggerType.SPATIAL))
			statistics = spatiallyModulatedStatistics;
		else if (trigger.equals(TriggerType.TEMPORAL))
			statistics = temporallyModulatedStatistics;

		return statistics.get(key);
	}

	/**
	 * Returns the current size of the host's spatiotemporal database (number of
	 * datums).
	 * 
	 * @return
	 */
	public int getDatumCount() {
		return datumCount;
	}

	/**
	 * Performs a host's sensing task. Senses "nearby" phenomena, creating a
	 * datum for each.
	 * 
	 * @param time
	 *            the simulation time
	 */
	private void senseNearbyPhenomena(int time) {
		// "sense" phenomena within the host's sensing range
		List<Integer> nearbyPhenomena = objectDB.getNearbyObjects(
				SimulationManager.PHENOMENON_OBJECT_TYPE, location,
				phenomenaSensingRange);

		// create a new datum for each sensed phenomenon
		Geoshape phenomenonLocation;
		MeasuredDatum spatiallyModulatedDatum, temporallyModulatedDatum;
		SpatiallyModulatedTrajectoryRule<FramedGraph<TitanGraph>> spatiallyModulatedRule;
		TemporallyModulatedTrajectoryRule<FramedGraph<TitanGraph>> temporallyModulatedRule;
		for (Integer phenomenon : nearbyPhenomena) {
			// get the phenomenon's location
			phenomenonLocation = delegate.getPhenomenonLocation(phenomenon,
					time);

			// create two datums representing this sensed phenomenon
			// (spatially and temporally triggered)

			// create a new spatially modulated rule
			spatiallyModulatedRule = new SpatiallyModulatedTrajectoryRule<FramedGraph<TitanGraph>>(
					spatiotemporalDB.framedGraph, trajectorySpatialResolution);
			// create a datum with the spatially modulated rule
			spatiallyModulatedDatum = spatiotemporalDB.datumFactory.addDatum(
					phenomenonLocation, location, (long) time,
					Integer.toString(identifier), null, spatiallyModulatedRule);
			// configure the MeasuredDatum properties
			spatiallyModulatedDatum.setPhenomenonIdentifier(phenomenon);
			spatiallyModulatedDatum.setTriggerType(TriggerType.SPATIAL);

			// create a new temporally modulated rule
			temporallyModulatedRule = new TemporallyModulatedTrajectoryRule<FramedGraph<TitanGraph>>(
					spatiotemporalDB.framedGraph, trajectoryTemporalResolution);
			// create a datum with the temporally modulated rule
			temporallyModulatedDatum = spatiotemporalDB.datumFactory
					.addDatum(phenomenonLocation, location, (long) time,
							Integer.toString(identifier), null,
							temporallyModulatedRule);
			// configure the MeasuredDatum properties
			temporallyModulatedDatum.setPhenomenonIdentifier(phenomenon);
			temporallyModulatedDatum.setTriggerType(TriggerType.TEMPORAL);

			// update datum count
			datumCount++;
		}
	}

	/**
	 * Pushes a datum onto the running per-time host-level aggregate statistics.
	 * 
	 * @param datum
	 *            the datum whose trajectory info is being measured.
	 * @param time
	 *            the simulation time.
	 */
	private void pushHostLevelMeasurements(MeasuredDatum datum, int time) {
		RunningStatisticsMap<RunningStatistics> statistics = null;
		if (datum.getTriggerType().equals(TriggerType.SPATIAL))
			statistics = spatiallyModulatedStatistics;
		else if (datum.getTriggerType().equals(TriggerType.TEMPORAL))
			statistics = temporallyModulatedStatistics;

		double value = 0;
		for (String key : statistics.getKeys()) {
			if (key.equals(RunningStatisticsMap.SIZE_KEY))
				value = (double) datum.getSize();

			else if (key.equals(RunningStatisticsMap.LENGTH_KEY))
				value = datum.getLength();

			else if (key.equals(RunningStatisticsMap.AGE_KEY))
				value = (double) datum.getAge();

			else if (key.equals(RunningStatisticsMap.DIST_H_0_KEY))
				value = datum.getDistanceHostCreation();

			else if (key.equals(RunningStatisticsMap.DIST_H_0_PER_SIZE_KEY))
				value = datum.getDistanceHostCreation() / datum.getSize();

			else if (key.equals(RunningStatisticsMap.DIST_H_0_PER_LENGTH_KEY))
				value = datum.getDistanceHostCreation() / datum.getLength();

			else if (key.equals(RunningStatisticsMap.DIST_H_0_PER_AGE_KEY))
				value = datum.getDistanceHostCreation() / datum.getAge();

			else if (key.equals(RunningStatisticsMap.DIST_P_0_KEY))
				value = datum.getDistancePhenomenonCreation();

			else if (key.equals(RunningStatisticsMap.DIST_P_0_PER_SIZE_KEY))
				value = datum.getDistancePhenomenonCreation() / datum.getSize();

			else if (key.equals(RunningStatisticsMap.DIST_P_0_PER_LENGTH_KEY))
				value = datum.getDistancePhenomenonCreation()
						/ datum.getLength();

			else if (key.equals(RunningStatisticsMap.DIST_P_0_PER_AGE_KEY))
				value = datum.getDistancePhenomenonCreation() / datum.getAge();

			statistics.push(key, value);
		}
	}

	/* MovingObject abstract method implementations. */

	@Override
	protected void initialize() {
		spatiotemporalDB = new TitanSpatiotemporalDatabase<MeasuredDatum>(
				Integer.toString(identifier), graphDir, indexDir,
				MeasuredDatum.class);

		// create a special vertex in the spatiotemporal database for the host's
		// position and simulation time
		hostContext = spatiotemporalDB.addVertex(null, HostContext.class);
	}

	@Override
	public void shutdown() {
		// TODO perform post-simulation analysis

		// shutdown spatiotemporalDB
		spatiotemporalDB.shutdown();
	}

	@Override
	public void step(int time) {
		// if necessary, perform sensing
		if (time % phenomenaSensingInterval == 0)
			senseNearbyPhenomena(time);

		// update the host's context vertex
		hostContext.setTimestamp(new Long(time));
		hostContext.setLocation(location);

		// trigger trajectory updates per temporal resolution
		hostContext.triggerTimestampUpdate();

		// trigger trajectory updates per spatial resolution
		hostContext.triggerLocationUpdate();

		// update each datum's measured properties
		Iterable<MeasuredDatum> datums = spatiotemporalDB
				.getFramedVertices(MeasuredDatum.class);
		Geoshape phenomenonLocation;
		for (MeasuredDatum datum : datums) {
			// update age
			datum.setAge((long) time - datum.getCreationTime());

			// update distance(host's current location, host's creation
			// location)
			datum.setDistanceHostCreation(location.getPoint().distance(
					datum.getCreationLocation().getPoint()));

			// update distance(phenomenon's current location, phenomenon's
			// creation location)
			phenomenonLocation = delegate.getPhenomenonLocation(
					datum.getPhenomenonIdentifier(), time);
			datum.setDistancePhenomenonCreation(phenomenonLocation.getPoint()
					.distance(datum.getLocation().getPoint()));

			// log the per-time per-trajectory measurements
			Logger.appendTrajectoryMeasurement(logDir, simulationId,
					identifier, datum.asVertex().getId(),
					datum.getTriggerType(), time, datum.getSize(),
					datum.getLength(), (int) datum.getAge(),
					(int) datum.getCreationTime(), datum.getCreationLocation(),
					phenomenonLocation, location,
					datum.getDistanceHostCreation(),
					datum.getDistancePhenomenonCreation());

			// update the running per-time host-level aggregate measurements
			pushHostLevelMeasurements(datum, time);
		}

		// log the running per-time host-level aggregate measurements
		Logger.appendHostMeasurement(logDir, simulationId, identifier,
				TriggerType.SPATIAL, time, datumCount,
				spatiallyModulatedStatistics.getRunningStatistics());
		Logger.appendHostMeasurement(logDir, simulationId, identifier,
				TriggerType.TEMPORAL, time, datumCount,
				temporallyModulatedStatistics.getRunningStatistics());
	}

}
