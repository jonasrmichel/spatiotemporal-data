package edu.utexas.ece.mpc.stdata.factories;

import java.util.List;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;
import edu.utexas.ece.mpc.stdata.vertices.IDatumVertexDelegate;

public interface IDatumFactory<D extends DatumVertex> extends IDatumVertexDelegate {

	/**
	 * Creates a new datum with the provided parameters.
	 * 
	 * @param phenomenonLocation
	 *            the sensed phenomenon's geospatial location.
	 * @param context
	 *            any initial explicit contextual relations this datum will
	 *            have.
	 * @param rules
	 *            unregistered rules that explicitly govern this datum.
	 * @return the newly created datum.
	 */
	public D addDatum(Geoshape phenomenonLoc, List<D> context, Rule... rules);

	/**
	 * Creates a new measurable datum with the provided parameters.
	 * 
	 * @param phenomenonLocation
	 *            the sensed phenomenon's geospatial location.
	 * @param context
	 *            any initial explicit contextual relations this datum will
	 *            have.
	 * @param rules
	 *            unregistered rules that explicitly governs this datum.
	 * @return the newly created datum.
	 */
	public D addMeasurableDatum(Geoshape phenomenonLoc, List<D> context,
			Rule... rules);
}
