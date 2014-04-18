package stdata.simulator.movingobjects;

import stdata.simulator.ILocationManager;
import stdata.simulator.IMovingObjectDatabase;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public abstract class MovingObject {

	/** The object's unique identifier. */
	protected int identifier;

	/** The object's type. */
	protected String type;

	/** The object's current geographic location. */
	protected Geoshape location;
	protected Geoshape prevLocation;

	/** An interface for the mobile trace location manager. */
	protected ILocationManager locationManager;

	/** An interface for a spatial (moving object) database. */
	protected IMovingObjectDatabase database;

	public MovingObject(int identifier, String type,
			ILocationManager locationManager, IMovingObjectDatabase database) {
		this.identifier = identifier;
		this.type = type;
		this.locationManager = locationManager;
		this.database = database;
	}

	/**
	 * Performs any required moving object initialization.
	 */
	protected abstract void initialize();

	/**
	 * Shuts down the moving object.
	 */
	public abstract void shutdown();

	/**
	 * Executes the tasks performed during a simulation step.
	 * 
	 * @param time
	 */
	protected abstract void step(int time);

	/**
	 * Advances the moving object to the provided simulation time.
	 * 
	 * @param time
	 *            the simulation time to advance to.
	 */
	public void advance(int time) {
		// update the moving object's location
		prevLocation = location;
		location = locationManager.getLocation(identifier, time);
		
		// if necessary, update the the moving objects database
		if (!prevLocation.equals(location))
			database.updatePosition(identifier, type, time, location);
		
		// perform simulation step tasks
		step(time);
	}

}
