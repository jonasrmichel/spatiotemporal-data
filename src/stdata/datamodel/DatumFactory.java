package stdata.datamodel;

import java.util.List;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.rules.IRuleRegistry;
import stdata.rules.Rule;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public class DatumFactory<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		implements IDatumFactory<G, E, F> {
	G baseGraph;
	F framedGraph;
	IRuleRegistry<G, E, F> ruleRegistry;

	public DatumFactory(G baseGraph, F framedGraph,
			IRuleRegistry<G, E, F> ruleRegistry) {
		this.baseGraph = baseGraph;
		this.framedGraph = framedGraph;
		this.ruleRegistry = ruleRegistry;
	}

	/* IDatumFactory interface implementation. */

	@Override
	public Datum addDatum(Geoshape phenomenonLocation, Geoshape hostLocation,
			long timestamp, String domain, List<Datum> context,
			boolean measurable, Rule<G, E, F> rule) {
		// create the datum
		Datum datum = framedGraph.addVertex(null, Datum.class);
		datum.setIsMeasurable(measurable);

		// initialize the datum's spatiotemporal trajectory
		if (!measurable) {
			SpaceTimePosition pos = framedGraph.addVertex(null,
					SpaceTimePosition.class);
			pos.setLocation(hostLocation);
			pos.setTimestamp(timestamp);
			pos.setDomain(domain);
			
			datum.add(pos);
			
		} else {
			datum.addMeasured(hostLocation, timestamp);
			
		}

		datum.setLocation(phenomenonLocation);

		if (context != null)
			datum.setContextData((Iterable<Datum>) context);

		// commit changes
		// baseGraph.commit();

		// register the datum's rule
		ruleRegistry.registerRule(rule, datum);

		return datum;
	}

}
