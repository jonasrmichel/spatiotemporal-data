package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.SpatiotemporalDatabase;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.RuleProxyVertex;

public abstract class VertexFrameFactory<V extends VertexFrame> {
	protected TransactionalGraph baseGraph;
	protected FramedGraph framedGraph;
	protected IRuleRegistry ruleRegistry;

	protected Class<V> type;

	private boolean initialized;

	public VertexFrameFactory(Class<V> type) {
		this.type = type;

		initialized = false;
	}

	public VertexFrameFactory(Class<V> type, TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		this.type = type;

		initialize(baseGraph, framedGraph, ruleRegistry);
	}

	public void initialize(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		this.baseGraph = baseGraph;
		this.framedGraph = framedGraph;
		this.ruleRegistry = ruleRegistry;

		initialized = true;
	}

	public boolean initialized() {
		return initialized;
	}

	public V addVertex(Object id) {
		V vertex = (V) framedGraph.addVertex(id, type);

		// set a special property that indicates this vertex is framed and its
		// framed class
		vertex.asVertex().setProperty(SpatiotemporalDatabase.FRAMED_CLASS_KEY,
				type);

		return vertex;
	}

	public V addVertex(Object id, Rule... rules) {
		V vertex = addVertex(id);

		// register the vertex's rules
		if (rules != null) {
			for (Rule rule : rules)
				ruleRegistry.registerRule(rule, vertex);
		}

		return vertex;
	}
}
