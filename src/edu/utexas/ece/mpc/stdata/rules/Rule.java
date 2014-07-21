package edu.utexas.ece.mpc.stdata.rules;

import java.util.Map;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.INetworkProvider;
import edu.utexas.ece.mpc.stdata.datamodel.edges.EdgeFrameFactory;
import edu.utexas.ece.mpc.stdata.datamodel.vertices.VertexFrameFactory;

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

	/** A context provider . */
	protected IContextProvider contextProvider;

	/** A network provider through which to send data. */
	protected INetworkProvider networkProvider;

	/** The rule's delegate interface (a rule container vertex). */
	protected IRuleDelegate delegate = null;

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
	 * @param delegate
	 */
	protected void initialize(G baseGraph, E eventGraph, F framedGraph,
			Map<String, EdgeFrameFactory> edgeFrameFactories,
			Map<String, VertexFrameFactory> vertexFrameFactories,
			IContextProvider contextProvider, INetworkProvider networkProvider,
			IRuleDelegate delegate) {
		this.baseGraph = baseGraph;
		this.eventGraph = eventGraph;
		this.framedGraph = framedGraph;
		this.edgeFrameFactories = edgeFrameFactories;
		this.vertexFrameFactories = vertexFrameFactories;
		this.contextProvider = contextProvider;
		this.networkProvider = networkProvider;
		this.delegate = delegate;
	}
}
