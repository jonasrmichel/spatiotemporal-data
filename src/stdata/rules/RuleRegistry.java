package stdata.rules;

import java.util.HashMap;
import java.util.Map;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.RuleContainer;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public class RuleRegistry<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>> implements
		IRuleRegistry<G, E, F> {
	G baseGraph;
	E eventGraph;
	F framedGraph;
	Map<Object, Rule<G, E, F>> rules;

	public RuleRegistry(G baseGraph, E eventGraph, F framedGraph) {
		this.baseGraph = baseGraph;
		this.eventGraph = eventGraph;
		this.framedGraph = framedGraph;
		rules = new HashMap<Object, Rule<G, E, F>>();
	}

	/* IRuleRegistry interface implementation. */

	@Override
	public RuleContainer registerRule(Rule<G, E, F> rule) {
		return registerRule(rule, null);
	}

	@Override
	public RuleContainer registerRule(Rule<G, E, F> rule, Datum datum) {
		// create the rule's container vertex
		RuleContainer ruleContainer = (RuleContainer) framedGraph.addVertex(null,
				RuleContainer.class);

		// set the rule's delegate (the container)
		rule.setDelegate(ruleContainer);

		// configure the graph references to governed data
		ruleContainer.addGoverns(datum);

		// register the rule
		rules.put(ruleContainer.asVertex().getId(), rule);
		
		// commit changes
		baseGraph.commit();

		return ruleContainer;
	}

}
