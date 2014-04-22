package stdata.datamodel;

import stdata.datamodel.vertices.SpaceTimePosition;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.frames.FramedGraph;

public class SpaceTimePositionFactory<G extends Graph> implements ISpaceTimePositionFactory {
	FramedGraph<G> graph;

	public SpaceTimePositionFactory(FramedGraph<G> graph) {
		this.graph = graph;
	}

	/* ISpaceTimePositionFactory interface implementation. */
	@Override
	public SpaceTimePosition addSpaceTimePosition(Geoshape location,
			long timestamp, String domain) {
		SpaceTimePosition pos = graph.addVertex(null, SpaceTimePosition.class);
		pos.setLocation(location);
		pos.setTimestamp(timestamp);
		pos.setDomain(domain);
		
		return pos;
	}
}
