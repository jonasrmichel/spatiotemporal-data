package edu.utexas.ece.mpc.stdata.rules;

import java.util.HashMap;
import java.util.Map;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventTransactionalGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.INetworkProvider;
import edu.utexas.ece.mpc.stdata.factories.EdgeFrameFactory;
import edu.utexas.ece.mpc.stdata.factories.VertexFrameFactory;
import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;
import edu.utexas.ece.mpc.stdata.vertices.RuleContainerVertex;

public class RuleRegistry implements IRuleRegistry {
	private TransactionalGraph baseGraph;
	private EventTransactionalGraph eventGraph;
	private FramedGraph framedGraph;

	private Map<Class, EdgeFrameFactory> edgeFrameFactories;
	private Map<Class, VertexFrameFactory> vertexFrameFactories;

	private IContextProvider contextProvider;
	private INetworkProvider networkProvider;

	Map<Object, Rule> rules;

	public RuleRegistry(TransactionalGraph baseGraph,
			EventTransactionalGraph eventGraph, FramedGraph framedGraph,
			Map<Class, EdgeFrameFactory> edgeFrameFactories,
			Map<Class, VertexFrameFactory> vertexFrameFactories,
			IContextProvider contextProvider, INetworkProvider networkProvider) {
		this.baseGraph = baseGraph;
		this.eventGraph = eventGraph;
		this.framedGraph = framedGraph;

		this.edgeFrameFactories = edgeFrameFactories;
		this.vertexFrameFactories = vertexFrameFactories;

		this.contextProvider = contextProvider;
		this.networkProvider = networkProvider;

		rules = new HashMap<Object, Rule>();
	}

	/* IRuleRegistry interface implementation. */

	@Override
	public RuleContainerVertex registerRule(Rule rule) {
		return registerRule(rule, null);
	}

	@Override
	public RuleContainerVertex registerRule(Rule rule, DatumVertex... data) {
		// create the rule's container vertex
		RuleContainerVertex ruleContainer = (RuleContainerVertex) framedGraph.addVertex(
				null, RuleContainerVertex.class);

		// intialize the rule with the container as its delegate
		rule.initialize(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories, contextProvider, networkProvider,
				ruleContainer);

		// configure the graph references to governed data
		for (DatumVertex datum : data)
			ruleContainer.addGoverns(datum);

		// register the rule
		rules.put(ruleContainer.asVertex().getId(), rule);

		return ruleContainer;
	}

	@Override
	public <V extends VertexFrame> RuleContainerVertex<V> registerRule(Rule rule,
			V... vertices) {
		// create the rule's container vertex
		RuleContainerVertex<V> ruleContainer = (RuleContainerVertex<V>) framedGraph
				.addVertex(null, RuleContainerVertex.class);

		// intialize the rule with the container as its delegate
		rule.initialize(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories, contextProvider, networkProvider,
				ruleContainer);

		// configure the graph references to governed data
		for (V vertex : vertices)
			ruleContainer.addGoverns(vertex);

		// register the rule
		rules.put(ruleContainer.asVertex().getId(), rule);

		return ruleContainer;
	}

}
