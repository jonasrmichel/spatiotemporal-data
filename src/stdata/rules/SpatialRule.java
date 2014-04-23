package stdata.rules;

import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public abstract class SpatialRule<F extends FramedGraph<?>, E extends EventGraph<?>>
		extends Rule<F, E> {

	public SpatialRule(F framedGraph, E eventGraph) {
		super(framedGraph, eventGraph);
		// TODO Auto-generated constructor stub
	}

	public SpatialRule(F framedGraph, E eventGraph, IRuleDelegate delegate) {
		super(framedGraph, eventGraph, delegate);
		// TODO Auto-generated constructor stub
	}

}
