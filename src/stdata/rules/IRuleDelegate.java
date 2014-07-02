package stdata.rules;

import stdata.datamodel.vertices.Datum;

public interface IRuleDelegate {

	/**
	 * Returns an iterator over the rule's governed data.
	 * 
	 * @return
	 */
	public Iterable<Datum> getGoverns();
}
