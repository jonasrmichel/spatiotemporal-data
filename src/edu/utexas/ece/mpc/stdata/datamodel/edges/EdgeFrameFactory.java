package edu.utexas.ece.mpc.stdata.datamodel.edges;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.datamodel.SpatiotemporalDatabase;

public class EdgeFrameFactory<T extends EdgeFrame> {
	protected TransactionalGraph baseGraph;
	protected FramedGraph framedGraph;

	public EdgeFrameFactory(TransactionalGraph baseGraph,
			FramedGraph framedGraph) {
		this.baseGraph = baseGraph;
		this.framedGraph = framedGraph;
	}

	public T addEdge(Object id, Class<T> kind) {
		T edge = (T) framedGraph.addVertex(id, kind);

		// set a special property that indicates this edge is framed
		edge.asEdge().setProperty(SpatiotemporalDatabase.FRAMED_CLASS_KEY,
				kind.getName());

		return edge;
	}
}
