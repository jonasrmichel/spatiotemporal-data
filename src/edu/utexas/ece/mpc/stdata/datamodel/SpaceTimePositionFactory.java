package edu.utexas.ece.mpc.stdata.datamodel;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.datamodel.vertices.SpaceTimePosition;
import edu.utexas.ece.mpc.stdata.datamodel.vertices.VertexFrameFactory;
import edu.utexas.ece.mpc.stdata.geo.Geoshape;

public class SpaceTimePositionFactory extends
		VertexFrameFactory<SpaceTimePosition> implements
		ISpaceTimePositionFactory {

	public SpaceTimePositionFactory(TransactionalGraph baseGraph, FramedGraph framedGraph) {
		super(baseGraph, framedGraph);
		// TODO Auto-generated constructor stub
	}

	/* ISpaceTimePositionFactory interface implementation. */

	@Override
	public SpaceTimePosition addSpaceTimePosition(Geoshape location,
			long timestamp, String domain) {
		SpaceTimePosition pos = addVertex(null, SpaceTimePosition.class);
		pos.setLocation(location);
		pos.setTimestamp(timestamp);
		pos.setDomain(domain);

		return pos;
	}

}
