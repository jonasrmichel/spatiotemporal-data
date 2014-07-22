package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.SpatiotemporalDatabase;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.rules.Rule;

public abstract class EdgeFrameFactory<T extends EdgeFrame> {
	protected TransactionalGraph baseGraph;
	protected FramedGraph framedGraph;
	protected IRuleRegistry ruleRegistry;

	public EdgeFrameFactory(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		this.baseGraph = baseGraph;
		this.framedGraph = framedGraph;
		this.ruleRegistry = ruleRegistry;
	}

	public T addEdge(Object id, Class<T> kind) {
		T edge = (T) framedGraph.addVertex(id, kind);

		// set a special property that indicates this edge is framed
		edge.asEdge().setProperty(SpatiotemporalDatabase.FRAMED_CLASS_KEY,
				kind.getName());

		return edge;
	}
}
