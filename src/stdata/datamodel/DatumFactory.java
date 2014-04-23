package stdata.datamodel;

import java.util.List;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.rules.IRuleRegistry;
import stdata.rules.Rule;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.frames.FramedGraph;

public class DatumFactory<G extends Graph, D extends Datum> implements
		IDatumFactory<D> {
	FramedGraph<G> graph;
	IRuleRegistry ruleRegistry;
	Class<D> datumClass;

	public DatumFactory(FramedGraph<G> graph, IRuleRegistry ruleRegistry,
			Class<D> datumClass) {
		this.graph = graph;
		this.ruleRegistry = ruleRegistry;
		this.datumClass = datumClass;
	}

	/* IDatumFactory interface implementation. */

	@Override
	public D addDatum(Geoshape phenomenonLocation, Geoshape hostLocation,
			long timestamp, String domain, List<D> context, Rule rule) {
		// create the datum
		D datum = graph.addVertex(null, datumClass);

		// initialize the datum's spatiotemporal trajectory
		SpaceTimePosition pos = graph.addVertex(null, SpaceTimePosition.class);
		pos.setLocation(hostLocation);
		pos.setTimestamp(timestamp);
		pos.setDomain(domain);

		// configure the datum
		datum.add(pos);
		datum.setLocation(phenomenonLocation);
		
		if (context != null)
			datum.setContextData((Iterable<Datum>) context);

		// register the datum's rule
		ruleRegistry.registerRule(rule, datum);

		return datum;
	}

}
