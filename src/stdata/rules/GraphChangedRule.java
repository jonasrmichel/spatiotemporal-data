package stdata.rules;

import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;
import com.tinkerpop.frames.FramedGraph;

public abstract class GraphChangedRule<F extends FramedGraph<?>, E extends EventGraph<?>>
		extends Rule<F, E> implements GraphChangedListener {

	public GraphChangedRule(F framedGraph, E eventGraph) {
		super(framedGraph, eventGraph);

		addListener();
	}

	public GraphChangedRule(F framedGraph, E eventGraph, IRuleDelegate delegate) {
		super(framedGraph, eventGraph, delegate);

		addListener();
	}

	/**
	 * Adds this rule as a graph changed listener in the event graph.
	 */
	public void addListener() {
		eventGraph.addListener(this);
	}

}
