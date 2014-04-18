package stdata.simulator;

import java.util.List;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public interface IMovingObjectDatabase {

	/**
	 * Inserts a new time-location position for the moving object with the
	 * provided identifier and type.
	 * 
	 * @param identifier
	 *            the moving object's unique identifier.
	 * @param type
	 *            the moving object's type.
	 * @param timestamp
	 *            the timestamp of the position update.
	 * @param location
	 *            the location of the position update.
	 */
	public void updatePosition(int identifier, String type, int timestamp,
			Geoshape location);

	/**
	 * Returns a list of nearby host mobile objects within a given range.
	 * 
	 * @param type
	 *            the desired type of moving objects (leave null for all types).
	 * @param location
	 *            the reference location.
	 * @param distance
	 *            distance (in meters) from the reference location to look for
	 *            nearby hosts.
	 * @return list of hosts' identifiers within range meters.
	 */
	public List<Integer> getNearbyObjects(String type, Geoshape location,
			double distance);

}
