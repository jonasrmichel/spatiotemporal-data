package stdata.datamodel;

import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
	ISpaceTimePositionFactory spaceTimePositionFactory;
	Class<D> datumClass;

	public DatumFactory(FramedGraph<G> graph, IRuleRegistry ruleRegistry,
			ISpaceTimePositionFactory spaceTimePositionFactory,
			Class<D> datumClass) {
		this.graph = graph;
		this.ruleRegistry = ruleRegistry;
		this.spaceTimePositionFactory = spaceTimePositionFactory;
		this.datumClass = datumClass;
	}

	/* IDatumFactory interface implementation. */

	@Override
	public D addDatum(Geoshape phenomenonLocation, Geoshape hostLocation,
			long timestamp, String domain, List<D> context, Rule rule) {
		// create the datum
		D datum = graph.addVertex(null, datumClass);

		// initialize the datum's spatiotemporal trajectory
		SpaceTimePosition pos = spaceTimePositionFactory.addSpaceTimePosition(
				hostLocation, timestamp, domain);

		// configure the datum
		datum.add(pos);
		datum.setLocation(phenomenonLocation);

		if (context != null)
			datum.setContextData((Iterable<Datum>) context);

		// register the datum's rule
		if (rule != null)
			ruleRegistry.registerRule(rule, datum);

		return datum;
	}

	@Override
	public D addDatum(JSONObject json, Geoshape location, long timestamp,
			String domain, List<D> context, Rule rule) {
		D datum = null;
		try {
			// create the datum
			datum = graph.addVertex(null, datumClass);

			// unmarshal the datum
			datum.unmarshal(json, spaceTimePositionFactory);
			
			// append a new space time position to the datum's trajectory
			SpaceTimePosition pos = spaceTimePositionFactory.addSpaceTimePosition(
					location, timestamp, domain);
			datum.add(pos);
			
			if (context != null)
				datum.setContextData((Iterable<Datum>) context);
			
			if (rule != null)
				ruleRegistry.registerRule(rule, datum);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return datum;
	}
}
