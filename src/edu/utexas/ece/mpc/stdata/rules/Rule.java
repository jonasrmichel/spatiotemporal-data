package edu.utexas.ece.mpc.stdata.rules;

import java.util.Map;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.INetworkProvider;
import edu.utexas.ece.mpc.stdata.datamodel.edges.EdgeFrameFactory;
import edu.utexas.ece.mpc.stdata.datamodel.vertices.VertexFrameFactory;

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
	protected void initialize(TransactionalGraph baseGraph,
			EventGraph eventGraph, FramedGraph framedGraph,
			Map<Class, EdgeFrameFactory> edgeFrameFactories,
			Map<Class, VertexFrameFactory> vertexFrameFactories,
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
