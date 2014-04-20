package stdata.rules;

import com.tinkerpop.frames.FramedGraph;

public abstract class Rule<G extends FramedGraph<?>> {
	/** The rule's graph reference. */
	protected G graph;
	
	/** The rule's delegate interface. */
	protected IRuleDelegate delegate = null;
	
	public Rule(G graph) {
		this.graph = graph;
	}
	
	public Rule(G graph, IRuleDelegate delegate) {
		this.graph = graph;
		this.delegate = delegate;
	}
	
	public void setDelegate(IRuleDelegate delegate) {
		this.delegate = delegate;
	}
}
