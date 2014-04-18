package stdata.simulator.movingobjects;

import java.util.List;

import stdata.datamodel.SpatiotemporalDatabase;
import stdata.datamodel.vertices.HostContext;
import stdata.datamodel.vertices.MeasuredDatum;
import stdata.rules.HostContextChagedRule;
import stdata.simulator.ILocationManager;
import stdata.simulator.IMovingObjectDatabase;
import stdata.simulator.SimulationManager;
import stdata.titan.TitanSpatiotemporalDatabase;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Geoshape;

public class Host extends MovingObject {
	String graphDir, indexDir, logDir;

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
			double phenomenaSensingRange, int phenomenaSensingInterval,
			int trajectoryTemporalResolution, double trajectorySpatialResolution) {
		super(identifier, type, locationManager, database);

		this.delegate = delegate;
		this.graphDir = graphDir;
		this.indexDir = indexDir;
		this.logDir = logDir;
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
		Geoshape location;
		for (Integer phenomenon : nearbyPhenomena) {
			// get the phenomenon's location
			location = delegate.getPhenomenonLocation(phenomenon, time);

			// TODO create two datums representing this sensed phenomenon
			// (spatially and temporally triggered)
			// !!!: set its initial-phenomenon-location
		}
	}

	/* MovingObject abstract method implementations. */

	@Override
	protected void initialize() {
		spatiotemporalDB = new TitanSpatiotemporalDatabase<MeasuredDatum>(
				Integer.toString(identifier), graphDir, indexDir);

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
		hostContext.setTimestampTrigger(true);

		// trigger trajectory updates per spatial resolution
		hostContext.setLocationTrigger(true);

		// TODO update each datum's age, distance-host-creation,
		// distance-phenomenon-creation

		// TODO perform time:per-trajectory temporal resolution
		// measurements+logging
		// TODO perform time:per-trajectory spatial resolution
		// measurements+logging

		// TODO perform time:host aggregate temporal resolution
		// measurements+logging
		// TODO perform time:host aggregate spatial resolution
		// measurements+logging
	}
}
