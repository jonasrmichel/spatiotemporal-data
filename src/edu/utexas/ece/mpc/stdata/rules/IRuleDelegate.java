package edu.utexas.ece.mpc.stdata.rules;

import edu.utexas.ece.mpc.stdata.vertices.Datum;

public interface IRuleDelegate {

	/**
	 * Returns an iterator over the rule's governed data.
	 * 
	 * @return an iterator over the rule's governed data.
	 */
	public Iterable<Datum> getGoverns();
}
