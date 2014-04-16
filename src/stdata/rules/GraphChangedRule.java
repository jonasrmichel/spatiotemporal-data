package pervasivedata.rules;

import com.tinkerpop.blueprints.util.wrappers.event.listener.GraphChangedListener;

public abstract class GraphChangedRule extends Rule implements GraphChangedListener {

	public GraphChangedRule(RuleRegistry delegate) {
		super(delegate);
		// TODO Auto-generated constructor stub
	}

}
