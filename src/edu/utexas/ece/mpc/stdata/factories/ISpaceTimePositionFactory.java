package edu.utexas.ece.mpc.stdata.factories;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePosition;

public interface ISpaceTimePositionFactory {

	/**
	 * Creates a new space-time position with the the provided parameters.
	 * 
	 * @param location
	 *            a geographic location.
	 * @param timestamp
	 *            a timestamp.
	 * @param domain
	 *            a logical domain.
	 * @return the created space-time position.
	 */
	public SpaceTimePosition addSpaceTimePosition(Geoshape location,
			long timestamp, String domain);
}
