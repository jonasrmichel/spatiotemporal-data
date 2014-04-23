package stdata.rules;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public abstract class Rule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>> {
	/** The rule's base graph. */
	protected G baseGraph;
	
	/** The rule's event graph wrapper. */
	protected E eventGraph;
	
	/** The rule's framed graph wrapper. */
	protected F framedGraph;

	/** The rule's delegate interface. */
	protected IRuleDelegate delegate = null;

	public Rule(G baseGraph, E eventGraph, F framedGraph) {
		this.baseGraph = baseGraph;
		this.eventGraph = eventGraph;
		this.framedGraph = framedGraph;
	}

	public Rule(G baseGraph, E eventGraph, F framedGraph, IRuleDelegate delegate) {
		this(baseGraph, eventGraph, framedGraph);

		this.delegate = delegate;
	}

	public void setDelegate(IRuleDelegate delegate) {
		this.delegate = delegate;
	}
}
