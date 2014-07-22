package edu.utexas.ece.mpc.stdata.rules;

import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;

public abstract class GraphChangedRule extends Rule implements
		GraphChangedListener {

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
