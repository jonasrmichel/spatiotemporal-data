package edu.utexas.ece.mpc.stdata.factories;

import java.util.List;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.Datum;
import edu.utexas.ece.mpc.stdata.vertices.IDatumDelegate;

public interface IDatumFactory extends IDatumDelegate {

	/**
	 * Creates a new datum with the provided parameters.
	 * 
	 * @param phenomenonLocation
	 *            the sensed phenomenon's geospatial location.
	 * @param context
	 *            any initial explicit contextual relations this datum will
	 *            have.
	 * @param rule
	 *            a rule that explicitly governs this datum.
	 * @return the newly created datum.
	 */
	public Datum addDatum(Geoshape phenomenonLoc, List<Datum> context, Rule rule);

	/**
	 * Creates a new measurable datum with the provided parameters.
	 * 
	 * @param phenomenonLocation
	 *            the sensed phenomenon's geospatial location.
	 * @param context
	 *            any initial explicit contextual relations this datum will
	 *            have.
	 * @param rule
	 *            a rule that explicitly governs this datum.
	 * @return the newly created datum.
	 */
	public Datum addMeasurableDatum(Geoshape phenomenonLoc,
			List<Datum> context, Rule rule);
}
