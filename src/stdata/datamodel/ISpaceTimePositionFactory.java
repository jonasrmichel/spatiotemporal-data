package stdata.datamodel;

import stdata.datamodel.vertices.SpaceTimePosition;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public interface ISpaceTimePositionFactory {

	/**
	 * Creates a new space-time position with the provided parameters.
	 * 
	 * @param location
	 * @param timestamp
	 * @param domain
	 * @return
	 */
	public SpaceTimePosition addSpaceTimePosition(Geoshape location,
			long timestamp, String domain);
}
