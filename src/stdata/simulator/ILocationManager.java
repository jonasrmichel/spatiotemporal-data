package stdata.simulator;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public interface ILocationManager {

	/**
	 * Returns the total number of moving object traces.
	 * 
	 * @return
	 */
	public int getNumTraces();

	/**
	 * Retrieves the geospatial location in the mobile object's trace with the
	 * provided identifier at the provided simulation time.
	 * 
	 * @param identifier
	 *            the unique identifier of a mobile object.
	 * @param time
	 *            the timestamp of the desired location.
	 * @return
	 */
	public Geoshape getLocation(int identifier, int time);

}
