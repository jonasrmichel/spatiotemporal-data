package stdata.rules;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;
import com.tinkerpop.frames.FramedGraph;

public abstract class GraphChangedRule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends Rule<G, E, F> implements GraphChangedListener {

	public GraphChangedRule(G baseGraph, E eventGraph, F framedGraph) {
		super(baseGraph, eventGraph, framedGraph);

		addListener();
	}

	public GraphChangedRule(G baseGraph, E eventGraph, F framedGraph,
			IRuleDelegate delegate) {
		super(baseGraph, eventGraph, framedGraph, delegate);

		addListener();
	}

	/**
	 * Adds this rule as a graph changed listener in the event graph.
	 */
	public void addListener() {
		eventGraph.addListener(this);
	}

}
