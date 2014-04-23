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
	protected IMovingObjectDatabase objectDB;

	public MovingObject(int identifier, String type,
			ILocationManager locationManager, IMovingObjectDatabase objectDB) {
		this.identifier = identifier;
		this.type = type;
		this.locationManager = locationManager;
		this.objectDB = objectDB;
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
	public abstract void step(int time);

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
		if (prevLocation == null || !prevLocation.equals(location))
			objectDB.updatePosition(identifier, type, time, location);
	}

}
