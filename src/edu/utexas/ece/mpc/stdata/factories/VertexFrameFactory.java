package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.SpatiotemporalDatabase;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.rules.Rule;

public abstract class VertexFrameFactory<T extends VertexFrame> {
	protected TransactionalGraph baseGraph;
	protected FramedGraph framedGraph;
	protected IRuleRegistry ruleRegistry;

	protected Class<T> type;

	private boolean initialized;

	public VertexFrameFactory(Class<T> type) {
		this.type = type;

		initialized = false;
	}

	public VertexFrameFactory(Class<T> type, TransactionalGraph baseGraph,
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

	public T addVertex(Object id) {
		T vertex = (T) framedGraph.addVertex(id, type);

		// set a special property that indicates this vertex is framed and its
		// framed class
		vertex.asVertex().setProperty(SpatiotemporalDatabase.FRAMED_CLASS_KEY,
				type);

		return vertex;
	}

	public T addVertex(Object id, Rule... rules) {
		T vertex = addVertex(id);

		// register the vertex's rule
		if (rules != null) {
			for (Rule rule : rules)
				ruleRegistry.registerRule(rule, vertex);
		}

		return vertex;
	}
}
