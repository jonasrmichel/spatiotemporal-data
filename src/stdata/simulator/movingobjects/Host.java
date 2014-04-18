package stdata.simulator.movingobjects;

import stdata.datamodel.SpatiotemporalDatabase;
import stdata.simulator.ILocationManager;
import stdata.simulator.IMovingObjectDatabase;

public class Host extends MovingObject {
	String graphDir, indexDir, statsDir;

	int trajectoryTemporalResolution;
	double trajectorySpatialResolution;

	SpatiotemporalDatabase stdb;

	public Host(int identifier, String type, ILocationManager locationManager,
			IMovingObjectDatabase database, String graphDir, String indexDir,
			String statsDir, int trajectoryTemporalResolution,
			double trajectorySpatialResolution) {
		super(identifier, type, locationManager, database);

		this.graphDir = graphDir;
		this.indexDir = indexDir;
		this.statsDir = statsDir;
		this.trajectoryTemporalResolution = trajectoryTemporalResolution;
		this.trajectorySpatialResolution = trajectorySpatialResolution;

		initialize();
	}

	/* MovingObject abstract method implementations. */

	@Override
	protected void initialize() {
		stdb = new SpatiotemporalDatabase();

	}

	@Override
	public void shutdown() {
		// TODO perform post-simulation analysis
		// TODO shutdown stdb

	}

	@Override
	protected void step(int time) {
		// TODO perform sensing, creating datums+trajectories
		
		// TODO trigger trajectory updates per temporal resolution
		// TODO trigger trajectory updates per spatial resolution
		
		// TODO perform time:per-trajectory temporal resolution measurements+logging
		// TODO perform time:per-trajectory spatial resolution measurements+logging
		
		// TODO perform time:host aggregate temporal resolution measurements+logging
		// TODO perform time:host aggregate spatial resolution measurements+logging
	}

}
