package stdata.rules;

import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public abstract class Rule<F extends FramedGraph<?>, E extends EventGraph<?>> {
	/** The rule's framed graph wrapper. */
	protected F framedGraph;
	
	/** The rule's event graph wrapper. */
	protected E eventGraph;
	
	/** The rule's delegate interface. */
	protected IRuleDelegate delegate = null;
	
	public Rule(F framedGraph, E eventGraph) {
		this.framedGraph = framedGraph;
		this.eventGraph = eventGraph;
	}
	
	public Rule(F framedGraph, E eventGraph, IRuleDelegate delegate) {
		this(framedGraph, eventGraph);
		
		this.delegate = delegate;
	}
	
	public void setDelegate(IRuleDelegate delegate) {
		this.delegate = delegate;
	}
}
