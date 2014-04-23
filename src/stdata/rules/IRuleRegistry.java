package stdata.rules;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.RuleContainer;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public interface IRuleRegistry<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>> {

	/**
	 * Registers a new rule with no explicitly governed data.
	 * 
	 * @param rule
	 * @return the rule's container in the graph database.
	 */
	public RuleContainer registerRule(Rule<G, E, F> rule);

	/**
	 * Registers a new rule that explicitly governs the provided datum.
	 * 
	 * @param rule
	 * @param datum
	 * @return the rule's container in the graph database.
	 */
	public RuleContainer registerRule(Rule<G, E, F> rule, Datum datum);
}
