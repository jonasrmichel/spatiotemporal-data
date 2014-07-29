package edu.utexas.ece.mpc.stdata.rules;

import com.tinkerpop.blueprints.Vertex;

import edu.utexas.ece.mpc.stdata.SpatiotemporalDatabase;
import edu.utexas.ece.mpc.stdata.vertices.SpatiotemporalContextVertex;

public abstract class SpatiotemporalContextRule extends GraphChangedRule {

	public SpatiotemporalContextRule() {
		super();
	}

	@Override
	public void vertexPropertyChanged(Vertex vertex, String key, Object value) {
		if (!vertex.getProperty(SpatiotemporalDatabase.FRAMED_CLASS_KEY)
				.equals(SpatiotemporalContextVertex.class))
			return;

		if (key.equals(SpatiotemporalContextVertex.LOCATION_KEY)) {
			// the host's location changed
			locationChanged((SpatiotemporalContextVertex) framedGraph.frame(vertex,
					SpatiotemporalContextVertex.class));

		} else if (key.equals(SpatiotemporalContextVertex.TIMESTAMP_KEY)) {
			// the host's time changed
			timeChanged((SpatiotemporalContextVertex) framedGraph.frame(vertex,
					SpatiotemporalContextVertex.class));
		}
	}

	/**
	 * Called when the host's geospatial location changes.
	 * 
	 * @param location
	 */
	public abstract void locationChanged(SpatiotemporalContextVertex hostContext);

	/**
	 * Called when the host's time changes.
	 * 
	 * @param time
	 */
	public abstract void timeChanged(SpatiotemporalContextVertex hostContext);

}
