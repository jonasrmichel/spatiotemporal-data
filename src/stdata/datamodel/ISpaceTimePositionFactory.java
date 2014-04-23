package stdata.datamodel;

import org.codehaus.jettison.json.JSONObject;

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

	/**
	 * Inserts an existing space-time position into the spatiotemporal database.
	 * 
	 * @param json
	 *            a marshaled space-time position.
	 * @return
	 */
	public SpaceTimePosition addSpaceTimePosition(JSONObject json);

}
