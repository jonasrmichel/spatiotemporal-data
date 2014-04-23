package stdata.rules;

import stdata.datamodel.SpatiotemporalFrameInitializer;
import stdata.datamodel.vertices.HostContext;
import stdata.simulator.SimulationManager;
import stdata.simulator.Util;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public abstract class HostContextChangedRule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends GraphChangedRule<G, E, F> {

	public HostContextChangedRule(G baseGraph, E eventGraph, F framedGraph) {
		super(baseGraph, eventGraph, framedGraph);
	}

	public HostContextChangedRule(G baseGraph, E eventGraph, F framedGraph,
			IRuleDelegate delegate) {
		super(baseGraph, eventGraph, framedGraph, delegate);
	}

	@Override
	public void vertexPropertyChanged(Vertex vertex, String key,
			Object oldValue, Object setValue) {
		if (SimulationManager.debug)
			Util.report(HostContextChangedRule.class,
					"vertexPropertyChanged(): " + vertex.toString() + " " + key
							+ ": "
							+ (oldValue != null ? oldValue.toString() : "null")
							+ " --> "
							+ (setValue != null ? setValue.toString() : "null"));

		if (!vertex
				.getProperty(SpatiotemporalFrameInitializer.FRAMED_CLASS_KEY)
				.equals(HostContext.class.getName()))
			return;

		if (key.equals(HostContext.LOCATION_TRIGGER_KEY)) {
			// the host's location changed
			hostLocationChanged(framedGraph.frame(vertex, HostContext.class));

		} else if (key.equals(HostContext.TIMESTAMP_TRIGGER_KEY)) {
			// the host's time changed
			hostTimeChanged(framedGraph.frame(vertex, HostContext.class));
		}
	}

	/**
	 * Called when the host's geospatial location changes.
	 * 
	 * @param location
	 */
	public abstract void hostLocationChanged(HostContext hostContext);

	/**
	 * Called when the host's time changes.
	 * 
	 * @param time
	 */
	public abstract void hostTimeChanged(HostContext hostContext);

}
