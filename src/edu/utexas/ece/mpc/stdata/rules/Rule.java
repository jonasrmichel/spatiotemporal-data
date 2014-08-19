package edu.utexas.ece.mpc.stdata.rules;

import java.util.ArrayList;
import java.util.Map;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.INetworkProvider;
import edu.utexas.ece.mpc.stdata.factories.EdgeFrameFactory;
import edu.utexas.ece.mpc.stdata.factories.VertexFrameFactory;
import edu.utexas.ece.mpc.stdata.vertices.RuleProxyVertex;

public abstract class Rule {
	/** The rule's base graph. */
	protected TransactionalGraph baseGraph;

	/** The rule's event graph wrapper. */
	protected EventGraph eventGraph;

	/** The rule's framed graph wrapper. */
	protected FramedGraph framedGraph;

	/** Holds a map of edge frame factories keyed on their class. */
	protected Map<Class, EdgeFrameFactory> edgeFrameFactories;

	/** Holds a map of vertex frame factories keyed on their class. */
	protected Map<Class, VertexFrameFactory> vertexFrameFactories;

	/** A context provider . */
	protected IContextProvider contextProvider;

	/** A network provider through which to send data. */
	protected INetworkProvider networkProvider;

	/** The rule's graph proxy. */
	protected RuleProxyVertex graphProxy = null;

	public Rule() {

	}

	/**
	 * Initializes the rule with references to all of its necessary parameters.
	 * A rule is initialized by the {@link RuleRegistry}; this avoids placing
	 * all these parameters in a big bloated constructor.
	 * 
	 * @param baseGraph
	 * @param eventGraph
	 * @param framedGraph
	 * @param edgeFrameFactories
	 * @param vertexFrameFactories
	 * @param contextProvider
	 * @param networkProvider
	 */
	protected void initialize(TransactionalGraph baseGraph,
			EventGraph eventGraph, FramedGraph framedGraph,
			Map<Class, EdgeFrameFactory> edgeFrameFactories,
			Map<Class, VertexFrameFactory> vertexFrameFactories,
			IContextProvider contextProvider, INetworkProvider networkProvider,
			RuleProxyVertex graphProxy) {
		this.baseGraph = baseGraph;
		this.eventGraph = eventGraph;
		this.framedGraph = framedGraph;
		this.edgeFrameFactories = edgeFrameFactories;
		this.vertexFrameFactories = vertexFrameFactories;
		this.contextProvider = contextProvider;
		this.networkProvider = networkProvider;
		this.graphProxy = graphProxy;
	}

	/**
	 * Re-attaches to the rule's proxy instance in the graph within the context
	 * of a transaction.
	 * 
	 * @return the rule's re-attached graph proxy.
	 */
	public RuleProxyVertex getGraphProxy() {
		if (framedGraph == null)
			return null;

		graphProxy = (RuleProxyVertex) framedGraph.getVertex(graphProxy
				.asVertex().getId(), RuleProxyVertex.class);

		return graphProxy;
	}

	/**
	 * Returns an iterator over the governed vertices framed as the provided
	 * type.
	 * 
	 * @param type
	 *            a {@link VertexFrame} derivative.
	 * @return an iterator over the rule's governed data framed as the provided
	 *         type.
	 */
	public <V extends VertexFrame> Iterable<V> getGovernedVertices(Class<V> type) {
		if (framedGraph == null)
			return null;

		ArrayList<V> governedVertices = new ArrayList<V>();

		Iterable<VertexFrame> governed = getGraphProxy().getGoverns();
		for (VertexFrame vertexFrame : governed) {
			try {
				governedVertices.add((V) framedGraph.frame(
						vertexFrame.asVertex(), type));

			} catch (Exception e) {
				continue;
			}
		}

		return governedVertices;
	}
}
