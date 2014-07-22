package edu.utexas.ece.mpc.stdata.rules;

import edu.utexas.ece.mpc.stdata.vertices.Datum;
import edu.utexas.ece.mpc.stdata.vertices.RuleContainer;

public interface IRuleRegistry {

	/**
	 * Registers a new rule with no explicitly governed data.
	 * 
	 * @param rule
	 *            the rule to register.
	 * @return the rule's container in the graph database.
	 */
	public RuleContainer registerRule(Rule rule);

	/**
	 * Registers a new rule that explicitly governs the provided datum.
	 * 
	 * @param rule
	 *            the rule to register.
	 * @param data
	 *            the datums that the rule governs.
	 * @return the rule's container in the graph database.
	 */
	public RuleContainer registerRule(Rule rule, Datum... data);
}
