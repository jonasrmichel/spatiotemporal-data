package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.SpatiotemporalDatabase;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;

public abstract class EdgeFrameFactory<T extends EdgeFrame> {
	protected TransactionalGraph baseGraph;
	protected FramedGraph framedGraph;
	protected IRuleRegistry ruleRegistry;

	protected Class<T> type;

	private boolean initialized;

	public EdgeFrameFactory(Class<T> type) {
		this.type = type;

		initialized = false;
	}

	public EdgeFrameFactory(Class<T> type, TransactionalGraph baseGraph,
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

	public T addEdge(Object id, Vertex outVertex, Vertex inVertex,
			Direction direction, String label) {
		T edge = (T) framedGraph.addEdge(id, outVertex, inVertex, label,
				direction, type);

		// set a special property that indicates this edge is framed and its
		// framed class
		edge.asEdge()
				.setProperty(SpatiotemporalDatabase.FRAMED_CLASS_KEY, type);

		return edge;
	}

}
