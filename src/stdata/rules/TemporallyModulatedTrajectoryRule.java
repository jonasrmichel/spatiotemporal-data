package stdata.rules;

import java.util.Map;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.HostContext;
import stdata.datamodel.vertices.SpaceTimePosition;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public class TemporallyModulatedTrajectoryRule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends HostContextChangedRule<G, E, F> {

	/** The temporal trajectory resolution (seconds). */
	double temporalResolution;

	/** The reference location. */
	long referenceTime = -1L;

	public TemporallyModulatedTrajectoryRule(G baseGraph, E eventGraph,
			F framedGraph, long temporalResolution) {
		super(baseGraph, eventGraph, framedGraph);

		this.temporalResolution = temporalResolution;
	}

	public TemporallyModulatedTrajectoryRule(G baseGraph, E eventGraph,
			F framedGraph, IRuleDelegate delegate, long temporalResolution) {
		super(baseGraph, eventGraph, framedGraph, delegate);

		this.temporalResolution = temporalResolution;
	}

	@Override
	public void vertexAdded(Vertex vertex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexPropertyRemoved(Vertex vertex, String key,
			Object removedValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexRemoved(Vertex vertex, Map<String, Object> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeAdded(Edge edge) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgePropertyChanged(Edge edge, String key, Object oldValue,
			Object setValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgePropertyRemoved(Edge edge, String key, Object removedValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeRemoved(Edge edge, Map<String, Object> props) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hostLocationChanged(HostContext hostContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hostTimeChanged(HostContext hostContext) {
		Geoshape location = hostContext.getLocation();
		long timestamp = hostContext.getTimestamp();

		if (referenceTime >= 0
				&& (timestamp - referenceTime) % temporalResolution != 0)
			return;

		// trigger trajectory updates
		SpaceTimePosition pos;
		for (Datum datum : delegate.getGoverns()) {
			pos = (SpaceTimePosition) framedGraph.addVertex(null,
					SpaceTimePosition.class);
			pos.setLocation(location);
			pos.setTimestamp(timestamp);

			datum.add(pos);
		}
		
		// commit changes
		baseGraph.commit();

		referenceTime = timestamp;

	}

}
