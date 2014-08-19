package edu.utexas.ece.mpc.stdata.rules;

import com.tinkerpop.frames.VertexFrame;

public interface IRuleRegistry {

	/**
	 * Registers a new rule with no explicitly governed data.
	 * 
	 * @param rule
	 *            a rule to register.
	 * @return the rule's proxy in the graph database.
	 */
	public void registerRule(Rule rule);

	/**
	 * Registers a new rule that explicitly governs the provided datum.
	 * 
	 * @param rule
	 *            a rule to register.
	 * @param data
	 *            the datums that the rule governs.
	 * @return the rule's proxy in the graph database.
	 */
	// public RuleContainerVertex registerRule(Rule rule, DatumVertex... data);

	public <V extends VertexFrame> void registerRule(Rule rule, V... vertices);

}
