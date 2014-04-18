package stdata.rules;

import com.tinkerpop.frames.FramedGraph;

public abstract class Rule<G extends FramedGraph<?>> {
	/** The rule's graph reference. */
	protected G graph;
	
	/** The rule's delegate interface. */
	protected IRuleDelegate delegate;
	
	public Rule(G graph, IRuleDelegate delegate) {
		this.graph = graph;
		this.delegate = delegate;
	}
}
