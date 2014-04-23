package stdata.rules;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public abstract class SpatialRule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends Rule<G, E, F> {

	public SpatialRule(G baseGraph, E eventGraph, F framedGraph) {
		super(baseGraph, eventGraph, framedGraph);
		// TODO Auto-generated constructor stub
	}

	public SpatialRule(G baseGraph, E eventGraph, F framedGraph, IRuleDelegate delegate) {
		super(baseGraph, eventGraph, framedGraph, delegate);
		// TODO Auto-generated constructor stub
	}

}
