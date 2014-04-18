package stdata.rules;

import java.util.List;

import stdata.datamodel.vertices.Datum;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public class RuleRegistry<T extends Graph> implements IRuleRegistry,
		IRuleDelegate {
	EventGraph<T> eventGraph;
	FramedGraph<T> framedGraph;
	List<Rule> rules;

	public RuleRegistry(EventGraph<T> eventGraph, FramedGraph<T> framedGraph) {
		this.eventGraph = eventGraph;
		this.framedGraph = framedGraph;
	}

	/* IRuleRegistry interface implementation. */

	@Override
	public void registerRule(Rule rule) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerRule(Datum datum, Rule rule) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerRules(List<Rule> rules) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerRules(Datum datum, List<Rule> rules) {
		// TODO Auto-generated method stub

	}

	/* IRuleDelegate interface implementation. */

}
