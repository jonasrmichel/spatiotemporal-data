package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.RuleProxyVertex;
import edu.utexas.ece.mpc.stdata.vertices.SpatiotemporalContextVertex;

public class SpatiotemporalContextFactory extends
		VertexFrameFactory<SpatiotemporalContextVertex> implements
		ISpatiotemporalContextFactory {

	public SpatiotemporalContextFactory() {
		super(SpatiotemporalContextVertex.class);
	}

	public SpatiotemporalContextFactory(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		super(SpatiotemporalContextVertex.class, baseGraph, framedGraph,
				ruleRegistry);
	}

	/* ISpatiotemporalContextFactory interface implementation. */

	@Override
	public SpatiotemporalContextVertex addSpatiotemporalContext(
			Geoshape location, long timestamp) {
		SpatiotemporalContextVertex stContext = addVertex(null);
		stContext.setLocation(location);
		stContext.setTimestamp(timestamp);

		return stContext;
	}
}
