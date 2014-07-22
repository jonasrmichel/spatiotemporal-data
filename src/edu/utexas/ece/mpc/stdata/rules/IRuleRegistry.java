package edu.utexas.ece.mpc.stdata.rules;

import edu.utexas.ece.mpc.stdata.datamodel.vertices.Datum;
import edu.utexas.ece.mpc.stdata.datamodel.vertices.RuleContainer;

public interface IRuleRegistry {

	/**
	 * Registers a new rule with no explicitly governed data.
	 * 
	 * @param rule
	 * @return the rule's container in the graph database.
	 */
	public RuleContainer registerRule(Rule rule);

	/**
	 * Registers a new rule that explicitly governs the provided datum.
	 * 
	 * @param rule
	 * @param datum
	 * @return the rule's container in the graph database.
	 */
	public RuleContainer registerRule(Rule rule, Datum datum);
}
