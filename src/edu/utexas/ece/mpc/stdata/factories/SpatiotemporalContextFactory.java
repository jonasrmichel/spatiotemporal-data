package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.vertices.SpatiotemporalContext;

public class SpatiotemporalContextFactory extends
		VertexFrameFactory<SpatiotemporalContext> implements
		ISpatiotemporalContextFactory {

	public SpatiotemporalContextFactory() {
		super(SpatiotemporalContext.class);
	}

	public SpatiotemporalContextFactory(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		super(SpatiotemporalContext.class, baseGraph, framedGraph, ruleRegistry);
	}

	/* ISpatiotemporalContextFactory interface implementation. */

	@Override
	public SpatiotemporalContext addSpatiotemporalContext(Geoshape location,
			long timestamp) {
		SpatiotemporalContext stContext = addVertex(null);
		stContext.setLocation(location);
		stContext.setTimestamp(timestamp);

		return stContext;
	}
}
