package stdata.rules;

import java.util.Map;

import stdata.datamodel.edges.EdgeFrameFactory;
import stdata.datamodel.vertices.VertexFrameFactory;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;
import com.tinkerpop.frames.FramedGraph;

public abstract class GraphChangedRule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends Rule<G, E, F> implements GraphChangedListener {

	public GraphChangedRule(G baseGraph, E eventGraph, F framedGraph,
			Map<String, EdgeFrameFactory> edgeFrameFactories,
			Map<String, VertexFrameFactory> vertexFrameFactories) {
		super(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories);

		addListener();
	}

	public GraphChangedRule(G baseGraph, E eventGraph, F framedGraph,
			Map<String, EdgeFrameFactory> edgeFrameFactories,
			Map<String, VertexFrameFactory> vertexFrameFactories,
			IRuleDelegate delegate) {
		super(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories, delegate);

		addListener();
	}

	/**
	 * Adds this rule as a graph changed listener in the event graph.
	 */
	public void addListener() {
		eventGraph.addListener(this);
	}

}
