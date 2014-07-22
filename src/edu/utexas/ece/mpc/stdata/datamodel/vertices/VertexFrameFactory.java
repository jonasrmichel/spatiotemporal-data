package edu.utexas.ece.mpc.stdata.datamodel.vertices;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.datamodel.SpatiotemporalDatabase;

public class VertexFrameFactory<T extends VertexFrame> {
	protected TransactionalGraph baseGraph;
	protected FramedGraph framedGraph;

	public VertexFrameFactory(TransactionalGraph baseGraph, FramedGraph framedGraph) {
		this.baseGraph = baseGraph;
		this.framedGraph = framedGraph;
	}

	public T addVertex(Object id, Class<T> kind) {
		T vertex = (T) framedGraph.addVertex(id, kind);

		// set a special property that indicates this vertex is framed
		vertex.asVertex().setProperty(SpatiotemporalDatabase.FRAMED_CLASS_KEY,
				kind.getName());

		return vertex;
	}

}
