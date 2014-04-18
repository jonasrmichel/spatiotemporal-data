package stdata.rules;

import java.util.HashMap;
import java.util.Map;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.RuleContainer;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public class RuleRegistry<T extends Graph> implements IRuleRegistry {
	EventGraph<T> eventGraph;
	FramedGraph<T> framedGraph;
	Map<Object, Rule> rules;

	public RuleRegistry(EventGraph<T> eventGraph, FramedGraph<T> framedGraph) {
		this.eventGraph = eventGraph;
		this.framedGraph = framedGraph;
		rules = new HashMap<Object, Rule>();
	}

	/* IRuleRegistry interface implementation. */

	@Override
	public RuleContainer registerRule(Rule rule) {
		RuleContainer ruleContainer = framedGraph.addVertex(null,
				RuleContainer.class);

		rules.put(ruleContainer.asVertex().getId(), rule);

		return ruleContainer;
	}

	@Override
	public RuleContainer registerRule(Rule rule, Datum datum) {
		RuleContainer ruleContainer = framedGraph.addVertex(null,
				RuleContainer.class);

		ruleContainer.addGoverns(datum);

		rules.put(ruleContainer.asVertex().getId(), rule);

		return ruleContainer;
	}

}
