package edu.utexas.ece.mpc.stdata.rules;

import java.util.Map;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventTransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.INetworkProvider;
import edu.utexas.ece.mpc.stdata.factories.EdgeFrameFactory;
import edu.utexas.ece.mpc.stdata.factories.VertexFrameFactory;

public abstract class GraphChangedRule extends Rule implements
		GraphChangedListener {

	public GraphChangedRule() {
		super();
	}

	/**
	 * Adds this rule as a graph changed listener in the event graph.
	 */
	public void addListener() {
		eventGraph.addListener(this);
	}

	@Override
	protected void initialize(TransactionalGraph baseGraph,
			EventTransactionalGraph eventGraph, FramedGraph framedGraph,
			Map<Class, EdgeFrameFactory> edgeFrameFactories,
			Map<Class, VertexFrameFactory> vertexFrameFactories,
			IContextProvider contextProvider, INetworkProvider networkProvider,
			IRuleDelegate delegate) {
		super.initialize(baseGraph, eventGraph, framedGraph,
				edgeFrameFactories, vertexFrameFactories, contextProvider,
				networkProvider, delegate);

		addListener();
	}
}
