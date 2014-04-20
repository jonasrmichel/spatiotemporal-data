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
import stdata.titan.TitanSpatiotemporalDatabase;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.frames.FramedGraph;

public class Host extends MovingObject {
	String graphDir, indexDir, logDir;

	String simulationId;

	double phenomenaSensingRange;
	int phenomenaSensingInterval;

	int trajectoryTemporalResolution;
	double trajectorySpatialResolution;

	SpatiotemporalDatabase<TitanGraph, MeasuredDatum> spatiotemporalDB;

	HostContext hostContext;

	IHostDelegate delegate;

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

		initialize();
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

			// perform the per-time per-trajectory measurements
			Logger.appendTrajectoryMeasurement(logDir, simulationId,
					identifier, datum.asVertex().getId(),
					datum.getTriggerType(), time, datum.getSize(),
					datum.getLength(), (int) datum.getAge(),
					(int) datum.getCreationTime(), datum.getCreationLocation(),
					phenomenonLocation, location,
					datum.getDistanceHostCreation(),
					datum.getDistancePhenomenonCreation());
		}

		// TODO perform the per-time host-level aggregate measurements

		// perform time:host aggregate temporal resolution measurements+logging
		executeHostLevelMeasurements(TriggerType.SPATIAL, time);
		// perform time:host aggregate spatial resolution measurements+logging
		executeHostLevelMeasurements(TriggerType.TEMPORAL, time);
	}

	/**
	 * Performs host-level running aggregate measurements (over all trajectories
	 * up to the provided simulation time).
	 * 
	 * @param trigger
	 *            the type of trajectories to measure.
	 * @param time
	 *            the simulation time.
	 */
	private void executeHostLevelMeasurements(TriggerType trigger, int time) {
		// TODO
		// db_size
		// size (avg, median, minimum, maximum, stdev)
		// length (avg, median, minimum, maximum, stdev)
		// age (avg, median, minimum, maximum, stdev)
		// dist_h_0 (avg, median, minimum, maximum, stdev)
		// dist_p_0 (avg, median, minimum, maximum, stdev)
	}
}
