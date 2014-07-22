package edu.utexas.ece.mpc.stdata.datamodel;

import java.util.List;

import edu.utexas.ece.mpc.stdata.datamodel.vertices.Datum;
import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.Rule;

public interface IDatumFactory extends IDatumDelegate {

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
	 * @param measurable
	 *            true if this datum should contain measured metadata.
	 * @param rule
	 *            a rule that explicitly governs this datum.
	 * @return the newly created datum.
	 */
	public Datum addDatum(Geoshape phenomenonLoc, Geoshape hostLoc,
			long timestamp, String domain, List<Datum> context,
			boolean measurable, Rule rule);

}
