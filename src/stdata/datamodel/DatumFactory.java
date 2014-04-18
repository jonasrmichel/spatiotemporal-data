package stdata.datamodel;

import java.util.List;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.rules.IRuleRegistry;
import stdata.rules.Rule;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.frames.FramedGraph;

public class DatumFactory<T extends Graph> implements IDatumFactory {
	FramedGraph<T> graph;
	IRuleRegistry ruleRegistry;

	public DatumFactory(FramedGraph<T> graph, IRuleRegistry ruleRegistry) {
		this.graph = graph;
		this.ruleRegistry = ruleRegistry;
	}

	/* IDatumFactory interface implementation. */

	@Override
	public Datum addDatum(double latitude, double longitude, long timestamp,
			String domain, List<Datum> context, Rule rule) {
		// create the datum
		Datum datum = graph.addVertex(null, Datum.class);

		// initialize the datum's spatiotemporal trajectory
		SpaceTimePosition pos = graph.addVertex(null, SpaceTimePosition.class);
		pos.setDatum(datum);
		pos.setLocation(Geoshape.point(latitude, longitude));
		pos.setTimestamp(timestamp);
		pos.setDomain(domain);

		// configure the datum
		datum.setTrajectoryHead(pos);
		datum.setContextData(context);

		// register the datum's rules
		ruleRegistry.registerRule(rule, datum);

		return datum;
	}

}
