package stdata.datamodel.edges;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.FramedGraph;

public class EdgeFrameFactory<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>, T extends EdgeFrame> {
	protected G baseGraph;
	protected F framedGraph;

	/** The framed class type property key. */
	public static final String FRAMED_CLASS_KEY = "class";

	public EdgeFrameFactory(G baseGraph, F framedGraph) {
		this.baseGraph = baseGraph;
		this.framedGraph = framedGraph;
	}

	public T addEdge(Object id, Class<T> kind) {
		T edge = framedGraph.addVertex(id, kind);
		edge.asEdge().setProperty(FRAMED_CLASS_KEY, kind.getName());

		return edge;
	}
}
