package edu.utexas.ece.mpc.stdata.rules;

import edu.utexas.ece.mpc.stdata.datamodel.vertices.Datum;

public interface IRuleDelegate {

	/**
	 * Returns an iterator over the rule's governed data.
	 * 
	 * @return
	 */
	public Iterable<Datum> getGoverns();
}
