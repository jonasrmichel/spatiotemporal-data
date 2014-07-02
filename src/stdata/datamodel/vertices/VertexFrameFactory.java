package stdata.datamodel.vertices;

import stdata.datamodel.SpatiotemporalDatabase;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

public class VertexFrameFactory<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>, T extends VertexFrame> {
	protected G baseGraph;
	protected F framedGraph;
	
	public VertexFrameFactory(G baseGraph, F framedGraph) {
		this.baseGraph = baseGraph;
		this.framedGraph = framedGraph;
	}

	public T addFramedVertex(Object id, Class<T> kind) {
		T vertex = framedGraph.addVertex(id, kind);
		vertex.asVertex().setProperty(SpatiotemporalDatabase.FRAMED_CLASS_KEY, kind.getName());
		
		return vertex;
	}
	
}
