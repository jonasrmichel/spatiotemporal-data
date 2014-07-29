package edu.utexas.ece.mpc.stdata.vertices;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.rules.IRuleDelegate;
import edu.utexas.ece.mpc.stdata.rules.Rule;

public interface RuleContainerVertex<V extends VertexFrame> extends VertexFrame,
		IRuleDelegate<V> {
	/** The contained rule. */
	@Property("rule")
	public Rule getRule();

	@Property("rule")
	public void setRule(Rule rule);

	/** A rule may explicitly govern one or more vertices. */
	@Adjacency(label = "governs")
	public Iterable<V> getGoverns();

	@Adjacency(label = "governs")
	public void setGoverns(Iterable<V> vertices);

	@Adjacency(label = "governs")
	public void addGoverns(V vertex);

}
