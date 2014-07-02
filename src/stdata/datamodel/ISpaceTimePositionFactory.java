package stdata.datamodel;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.geo.Geoshape;

public interface ISpaceTimePositionFactory<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>> {

	/**
	 * Creates a new space-time position with the the provided parameters.
	 * 
	 * @param location
	 *            a geographic location.
	 * @param timestamp
	 *            a timestamp.
	 * @param domain
	 *            a logical domain.
	 * @return the created space-time position.
	 */
	public SpaceTimePosition addSpaceTimePosition(Geoshape location,
			long timestamp, String domain);
}
