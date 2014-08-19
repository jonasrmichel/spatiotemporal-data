package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.RuleProxyVertex;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePositionVertex;

public class SpaceTimePositionFactory extends
		VertexFrameFactory<SpaceTimePositionVertex> implements
		ISpaceTimePositionFactory {

	public SpaceTimePositionFactory() {
		super(SpaceTimePositionVertex.class);
	}

	public SpaceTimePositionFactory(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		super(SpaceTimePositionVertex.class, baseGraph, framedGraph,
				ruleRegistry);
		// TODO Auto-generated constructor stub
	}

	/* ISpaceTimePositionFactory interface implementation. */

	@Override
	public SpaceTimePositionVertex addSpaceTimePosition(Geoshape location,
			long timestamp, String domain) {
		SpaceTimePositionVertex pos = addVertex(null);
		pos.setLocation(location);
		pos.setTimestamp(timestamp);
		pos.setDomain(domain);

		return pos;
	}

}
