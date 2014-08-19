package edu.utexas.ece.mpc.stdata.rules;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.VertexFrame;

public interface IRuleDelegate<V extends VertexFrame> {

	/**
	 * Returns an iterator over the rule's governed data.
	 * 
	 * @return an iterator over the rule's governed data.
	 */
	public Iterable<V> getGoverns();

	/**
	 * Returns the delegate's identifier in the graph database.
	 * 
	 * @return the identifier of the delegate graph element.
	 */
	public Object getId();

	/**
	 * Returns the vertex of the underlying delegate vertex.
	 * 
	 * @return the delegate vertex reference.
	 */
	public Vertex asVertex();
}
