package stdata.rules;

import java.util.Map;

import stdata.datamodel.SpatiotemporalDatabase;
import stdata.datamodel.edges.EdgeFrameFactory;
import stdata.datamodel.vertices.SpatiotemporalContext;
import stdata.datamodel.vertices.VertexFrameFactory;
import stdata.simulator.SimulationManager;
import stdata.simulator.Util;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public abstract class SpatiotemporalContextRule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends GraphChangedRule<G, E, F> {

	public SpatiotemporalContextRule(G baseGraph, E eventGraph, F framedGraph,
			Map<String, EdgeFrameFactory> edgeFrameFactories,
			Map<String, VertexFrameFactory> vertexFrameFactories) {
		super(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories);
	}

	public SpatiotemporalContextRule(G baseGraph, E eventGraph, F framedGraph,
			Map<String, EdgeFrameFactory> edgeFrameFactories,
			Map<String, VertexFrameFactory> vertexFrameFactories,
			IRuleDelegate delegate) {
		super(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories, delegate);
	}

	@Override
	public void vertexPropertyChanged(Vertex vertex, String key, Object value) {
		if (SimulationManager.debug)
			Util.report(SpatiotemporalContextRule.class,
					"vertexPropertyChanged(): " + vertex.toString() + " " + key
							+ ": "
							+ (value != null ? value.toString() : "null"));

		if (!vertex.getProperty(SpatiotemporalDatabase.FRAMED_CLASS_KEY)
				.equals(SpatiotemporalContext.class.getName()))
			return;

		if (key.equals(SpatiotemporalContext.LOCATION_KEY)) {
			// the host's location changed
			locationChanged(framedGraph.frame(vertex,
					SpatiotemporalContext.class));

		} else if (key.equals(SpatiotemporalContext.TIMESTAMP_KEY)) {
			// the host's time changed
			timeChanged(framedGraph.frame(vertex, SpatiotemporalContext.class));
		}
	}

	/**
	 * Called when the host's geospatial location changes.
	 * 
	 * @param location
	 */
	public abstract void locationChanged(SpatiotemporalContext hostContext);

	/**
	 * Called when the host's time changes.
	 * 
	 * @param time
	 */
	public abstract void timeChanged(SpatiotemporalContext hostContext);

}
