package stdata.rules;

import com.tinkerpop.frames.FramedGraph;

public abstract class SpatialRule<G extends FramedGraph<?>> extends Rule<G> {

	public SpatialRule(G graph) {
		super(graph);
		// TODO Auto-generated constructor stub
	}
	
	public SpatialRule(G graph, IRuleDelegate delegate) {
		super(graph, delegate);
		// TODO Auto-generated constructor stub
	}

}
