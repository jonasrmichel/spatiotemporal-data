package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.vertices.SpatiotemporalContext;

public class SpatiotemporalContextFactory extends
		VertexFrameFactory<SpatiotemporalContext> implements
		ISpatiotemporalContextFactory {

	public SpatiotemporalContextFactory(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		super(baseGraph, framedGraph, ruleRegistry);
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
