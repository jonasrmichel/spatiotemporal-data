package edu.utexas.ece.mpc.stdata.vertices;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.VertexFrame;

public interface RuleProxyVertex extends VertexFrame {

	/** A rule may explicitly govern one or more vertices. */
	@Adjacency(label = "governs")
	public Iterable<VertexFrame> getGoverns();

	@Adjacency(label = "governs")
	public void setGoverns(Iterable<VertexFrame> vertices);

	@Adjacency(label = "governs")
	public void addGoverns(final VertexFrame vertex);

}
