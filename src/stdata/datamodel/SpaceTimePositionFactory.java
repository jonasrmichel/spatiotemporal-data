package stdata.datamodel;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.datamodel.vertices.VertexFrameFactory;
import stdata.geo.Geoshape;

public class SpaceTimePositionFactory<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends VertexFrameFactory<G, E, F, SpaceTimePosition> implements
		ISpaceTimePositionFactory {

	public SpaceTimePositionFactory(G baseGraph, F framedGraph) {
		super(baseGraph, framedGraph);
		// TODO Auto-generated constructor stub
	}

	/* ISpaceTimePositionFactory interface implementation. */

	@Override
	public SpaceTimePosition addSpaceTimePosition(Geoshape location,
			long timestamp, String domain) {
		SpaceTimePosition pos = addFramedVertex(null, SpaceTimePosition.class);
		pos.setLocation(location);
		pos.setTimestamp(timestamp);
		pos.setDomain(domain);

		return pos;
	}

}
