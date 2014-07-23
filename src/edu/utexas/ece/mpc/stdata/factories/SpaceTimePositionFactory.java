package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePosition;

public class SpaceTimePositionFactory extends
		VertexFrameFactory<SpaceTimePosition> implements
		ISpaceTimePositionFactory {

	public SpaceTimePositionFactory() {
		super(SpaceTimePosition.class);
	}

	public SpaceTimePositionFactory(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		super(SpaceTimePosition.class, baseGraph, framedGraph, ruleRegistry);
		// TODO Auto-generated constructor stub
	}

	/* ISpaceTimePositionFactory interface implementation. */

	@Override
	public SpaceTimePosition addSpaceTimePosition(Geoshape location,
			long timestamp, String domain) {
		SpaceTimePosition pos = addVertex(null);
		pos.setLocation(location);
		pos.setTimestamp(timestamp);
		pos.setDomain(domain);

		return pos;
	}

}
