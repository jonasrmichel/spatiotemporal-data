package stdata.rules;

import java.util.HashMap;
import java.util.Map;

import stdata.IContextProvider;
import stdata.INetworkProvider;
import stdata.datamodel.edges.EdgeFrameFactory;
import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.RuleContainer;
import stdata.datamodel.vertices.VertexFrameFactory;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public class RuleRegistry<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		implements IRuleRegistry<G, E, F> {
	private G baseGraph;
	private E eventGraph;
	private F framedGraph;

	private Map<String, EdgeFrameFactory> edgeFrameFactories;
	private Map<String, VertexFrameFactory> vertexFrameFactories;

	private IContextProvider contextProvider;
	private INetworkProvider networkProvider;

	Map<Object, Rule<G, E, F>> rules;

	public RuleRegistry(G baseGraph, E eventGraph, F framedGraph,
			Map<String, EdgeFrameFactory> edgeFrameFactories,
			Map<String, VertexFrameFactory> vertexFrameFactories,
			IContextProvider contextProvider, INetworkProvider networkProvider) {
		this.baseGraph = baseGraph;
		this.eventGraph = eventGraph;
		this.framedGraph = framedGraph;

		this.edgeFrameFactories = edgeFrameFactories;
		this.vertexFrameFactories = vertexFrameFactories;

		this.contextProvider = contextProvider;
		this.networkProvider = networkProvider;

		rules = new HashMap<Object, Rule<G, E, F>>();
	}

	/* IRuleRegistry interface implementation. */

	@Override
	public RuleContainer registerRule(Rule<G, E, F> rule) {
		return registerRule(rule, null);
	}

	@Override
	public RuleContainer registerRule(Rule<G, E, F> rule, Datum datum) {
		// create the rule's container vertex
		RuleContainer ruleContainer = (RuleContainer) framedGraph.addVertex(
				null, RuleContainer.class);

		// intialize the rule with the container as its delegate
		rule.initialize(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories, contextProvider, networkProvider,
				ruleContainer);

		// configure the graph references to governed data
		ruleContainer.addGoverns(datum);

		// register the rule
		rules.put(ruleContainer.asVertex().getId(), rule);

		return ruleContainer;
	}

}
