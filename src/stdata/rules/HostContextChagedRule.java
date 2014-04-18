package stdata.rules;

import stdata.datamodel.SpatiotemporalFrameInitializer;
import stdata.datamodel.vertices.HostContext;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;

public abstract class HostContextChagedRule<G extends FramedGraph<?>> extends
		GraphChangedRule<G> {

	public HostContextChagedRule(G graph, IRuleDelegate delegate) {
		super(graph, delegate);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void vertexPropertyChanged(Vertex vertex, String key,
			Object oldValue, Object setValue) {
		if (!vertex.getProperty(SpatiotemporalFrameInitializer.FRAMED_CLASS_KEY)
				.equals(HostContext.class.getName()))
			return;
		
		if (key.equals(HostContext.LOCATION_TRIGGER_KEY)) {
			// the host's location changed
			hostLocationChanged((HostContext) vertex);

		} else if (key.equals(HostContext.TIMESTAMP_TRIGGER_KEY)) {
			// the host's time changed
			hostTimeChanged((HostContext) vertex);
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
