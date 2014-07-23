package edu.utexas.ece.mpc.stdata.rules;

import com.tinkerpop.frames.VertexFrame;

public interface IRuleDelegate<T extends VertexFrame> {

	/**
	 * Returns an iterator over the rule's governed data.
	 * 
	 * @return an iterator over the rule's governed data.
	 */
	public Iterable<T> getGoverns();
}
