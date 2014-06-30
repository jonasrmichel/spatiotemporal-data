package stdata.datamodel;

import stdata.datamodel.vertices.SpatiotemporalContext;
import stdata.datamodel.vertices.VertexFrameFactory;
import stdata.geo.Geoshape;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public class SpatiotemporalContextFactory<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends VertexFrameFactory<G, E, F, SpatiotemporalContext> implements
		ISpatiotemporalContextFactory {

	public SpatiotemporalContextFactory(G baseGraph, F framedGraph) {
		super(baseGraph, framedGraph);
	}

	/* ISpatiotemporalContextFactory interface implementation. */

	@Override
	public SpatiotemporalContext addSpatiotemporalContext(Geoshape location,
			long timestamp) {
		SpatiotemporalContext stContext = addFramedVertex(null,
				SpatiotemporalContext.class);
		stContext.setLocation(location);
		stContext.setTimestamp(timestamp);

		return stContext;
	}
}
