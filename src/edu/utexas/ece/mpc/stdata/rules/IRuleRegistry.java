package edu.utexas.ece.mpc.stdata.rules;

import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;
import edu.utexas.ece.mpc.stdata.vertices.RuleContainerVertex;

public interface IRuleRegistry {

	/**
	 * Registers a new rule with no explicitly governed data.
	 * 
	 * @param rule
	 *            the rule to register.
	 * @return the rule's container in the graph database.
	 */
	public RuleContainerVertex registerRule(Rule rule);

	/**
	 * Registers a new rule that explicitly governs the provided datum.
	 * 
	 * @param rule
	 *            the rule to register.
	 * @param data
	 *            the datums that the rule governs.
	 * @return the rule's container in the graph database.
	 */
	public RuleContainerVertex registerRule(Rule rule, DatumVertex... data);
	
	
	
	public <V extends VertexFrame> RuleContainerVertex registerRule(Rule rule, V... vertices);
	
}
