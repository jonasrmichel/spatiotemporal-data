package edu.utexas.ece.mpc.stdata.rules;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;
import com.tinkerpop.frames.FramedGraph;

public abstract class GraphChangedRule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends Rule<G, E, F> implements GraphChangedListener {

	public GraphChangedRule() {
		addListener();
	}

	/**
	 * Adds this rule as a graph changed listener in the event graph.
	 */
	public void addListener() {
		eventGraph.addListener(this);
	}

}
