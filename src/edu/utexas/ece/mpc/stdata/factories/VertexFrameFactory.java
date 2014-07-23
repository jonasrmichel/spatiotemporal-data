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

	public VertexFrameFactory(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		this.baseGraph = baseGraph;
		this.framedGraph = framedGraph;
		this.ruleRegistry = ruleRegistry;
	}

	public T addVertex(Object id, Class<T> kind) {
		T vertex = (T) framedGraph.addVertex(id, kind);

		// set a special property that indicates this vertex is framed
		vertex.asVertex().setProperty(SpatiotemporalDatabase.FRAMED_CLASS_KEY,
				kind.getName());

		return vertex;
	}

	public T addVertex(Object id, Class<T> kind, Rule rule) {
		T vertex = addVertex(id, kind);

		// register the vertex's rule
		if (rule != null)
			ruleRegistry.registerRule(rule, vertex);

		return vertex;
	}
}
