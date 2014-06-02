package stdata.rules;

import java.util.Map;

import stdata.datamodel.edges.EdgeFrameFactory;
import stdata.datamodel.vertices.VertexFrameFactory;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public abstract class Rule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>> {
	/** The rule's base graph. */
	protected G baseGraph;

	/** The rule's event graph wrapper. */
	protected E eventGraph;

	/** The rule's framed graph wrapper. */
	protected F framedGraph;

	/** Holds a map of edge frame factories keyed on their class names. */
	protected Map<String, EdgeFrameFactory> edgeFrameFactories;

	/** Holds a map of vertex frame factories keyed on their class names. */
	protected Map<String, VertexFrameFactory> vertexFrameFactories;

	/** The rule's delegate interface. */
	protected IRuleDelegate delegate = null;

	public Rule(G baseGraph, E eventGraph, F framedGraph,
			Map<String, EdgeFrameFactory> edgeFrameFactories,
			Map<String, VertexFrameFactory> vertexFrameFactories) {
		this.baseGraph = baseGraph;
		this.eventGraph = eventGraph;
		this.framedGraph = framedGraph;
		this.edgeFrameFactories = edgeFrameFactories;
		this.vertexFrameFactories = vertexFrameFactories;
	}

	public Rule(G baseGraph, E eventGraph, F framedGraph,
			Map<String, EdgeFrameFactory> edgeFrameFactories,
			Map<String, VertexFrameFactory> vertexFrameFactories,
			IRuleDelegate delegate) {
		this(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories);

		this.delegate = delegate;
	}

	public void setDelegate(IRuleDelegate delegate) {
		this.delegate = delegate;
	}
}
