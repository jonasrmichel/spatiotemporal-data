package stdata.datamodel.vertices;

import stdata.rules.IRuleDelegate;
import stdata.rules.Rule;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

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
