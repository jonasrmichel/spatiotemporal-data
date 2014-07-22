package edu.utexas.ece.mpc.stdata.datamodel;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.datamodel.vertices.SpatiotemporalContext;
import edu.utexas.ece.mpc.stdata.datamodel.vertices.VertexFrameFactory;
import edu.utexas.ece.mpc.stdata.geo.Geoshape;

public class SpatiotemporalContextFactory extends
		VertexFrameFactory<SpatiotemporalContext> implements
		ISpatiotemporalContextFactory {

	public SpatiotemporalContextFactory(TransactionalGraph baseGraph,
			FramedGraph framedGraph) {
		super(baseGraph, framedGraph);
	}

	/* ISpatiotemporalContextFactory interface implementation. */

	@Override
	public SpatiotemporalContext addSpatiotemporalContext(Geoshape location,
			long timestamp) {
		SpatiotemporalContext stContext = addVertex(null,
				SpatiotemporalContext.class);
		stContext.setLocation(location);
		stContext.setTimestamp(timestamp);

		return stContext;
	}
}
