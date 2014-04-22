package stdata.datamodel;

import java.util.List;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.rules.Rule;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public interface IDatumFactory<D extends Datum> {

	/**
	 * Creates a new datum with the provided parameters.
	 * 
	 * @param phenomenonLocation
	 *            the sensed phenomenon's geospatial location.
	 * @param hostLocation
	 *            the host's geospatial location.
	 * @param timestamp
	 *            the current timestamp.
	 * @param domain
	 *            the logical domain.
	 * @param context
	 *            any initial explicit contextual relations this datum will
	 *            have.
	 * @param rule
	 *            a rule that explicitly governs this datum.
	 * @return the newly created datum.
	 */
	public D addDatum(Geoshape phenomenonLocation, Geoshape hostLocation,
			long timestamp, String domain, List<D> context, Rule rule);

	/**
	 * Inserts an existing datum with the provided trajectory into the
	 * spatiotemporal database.
	 * 
	 * @param datum
	 * @param trajectory
	 */
	public void insertDatum(Datum datum, SpaceTimePosition[] trajectory);

}
