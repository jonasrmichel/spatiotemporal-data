package edu.utexas.ece.mpc.stdata.rules;

import java.util.HashMap;
import java.util.Map;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.INetworkProvider;
import edu.utexas.ece.mpc.stdata.factories.EdgeFrameFactory;
import edu.utexas.ece.mpc.stdata.factories.VertexFrameFactory;
import edu.utexas.ece.mpc.stdata.vertices.Datum;
import edu.utexas.ece.mpc.stdata.vertices.RuleContainer;

public class RuleRegistry implements IRuleRegistry {
	private TransactionalGraph baseGraph;
	private EventGraph eventGraph;
	private FramedGraph framedGraph;

	private Map<Class, EdgeFrameFactory> edgeFrameFactories;
	private Map<Class, VertexFrameFactory> vertexFrameFactories;

	private IContextProvider contextProvider;
	private INetworkProvider networkProvider;

	Map<Object, Rule> rules;

	public RuleRegistry(TransactionalGraph baseGraph, EventGraph eventGraph,
			FramedGraph framedGraph,
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
	public RuleContainer registerRule(Rule rule) {
		return registerRule(rule, null);
	}

	@Override
	public RuleContainer registerRule(Rule rule, Datum... data) {
		// create the rule's container vertex
		RuleContainer ruleContainer = (RuleContainer) framedGraph.addVertex(
				null, RuleContainer.class);

		// intialize the rule with the container as its delegate
		rule.initialize(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories, contextProvider, networkProvider,
				ruleContainer);

		// configure the graph references to governed data
		for (Datum datum : data)
			ruleContainer.addGoverns(datum);

		// register the rule
		rules.put(ruleContainer.asVertex().getId(), rule);

		return ruleContainer;
	}

}
