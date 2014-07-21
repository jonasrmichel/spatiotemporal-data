package edu.utexas.ece.mpc.stdata.datamodel.vertices;

import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.rules.IRuleDelegate;
import edu.utexas.ece.mpc.stdata.rules.Rule;

public interface RuleContainer extends VertexFrame, IRuleDelegate {
	/** The contained rule. */
	@Property("rule")
	public Rule getRule();

	@Property("rule")
	public void setRule(Rule rule);

	/** A rule may explicitly govern one or more datum. */
	@Adjacency(label = "governs")
	public Iterable<Datum> getGoverns();

	@Adjacency(label = "governs")
	public void setGoverns(Iterable<Datum> data);

	@Adjacency(label = "governs")
	public void addGoverns(Datum datum);

}
