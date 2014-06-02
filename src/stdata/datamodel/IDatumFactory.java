package stdata.datamodel;

import java.util.List;

import stdata.datamodel.vertices.Datum;
import stdata.geo.Geoshape;
import stdata.rules.Rule;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public interface IDatumFactory<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>> {

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
			boolean measurable, Rule<G, E, F> rule);

}
