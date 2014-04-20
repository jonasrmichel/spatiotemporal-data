package stdata.rules;

import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;
import com.tinkerpop.frames.FramedGraph;

public abstract class GraphChangedRule<G extends FramedGraph<?>> extends Rule<G>
		implements GraphChangedListener {

	public GraphChangedRule(G graph) {
		super(graph);
		// TODO Auto-generated constructor stub
	}
	
	public GraphChangedRule(G graph, IRuleDelegate delegate) {
		super(graph, delegate);
		// TODO Auto-generated constructor stub
	}

}
