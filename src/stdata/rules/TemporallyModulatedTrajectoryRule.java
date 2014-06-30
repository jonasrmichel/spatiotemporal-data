package stdata.rules;

import java.util.Map;

import stdata.datamodel.ISpaceTimePositionFactory;
import stdata.datamodel.edges.EdgeFrameFactory;
import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpatiotemporalContext;
import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.datamodel.vertices.VertexFrameFactory;
import stdata.geo.Geoshape;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public class TemporallyModulatedTrajectoryRule<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends SpatiotemporalContextRule<G, E, F> {

	/** The temporal trajectory resolution (seconds). */
	double temporalResolution;

	/** The reference location. */
	long referenceTime = -1L;

	public TemporallyModulatedTrajectoryRule(G baseGraph, E eventGraph,
			F framedGraph, Map<String, EdgeFrameFactory> edgeFrameFactories,
			Map<String, VertexFrameFactory> vertexFrameFactories,
			long temporalResolution) {
		super(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories);

		this.temporalResolution = temporalResolution;
	}

	public TemporallyModulatedTrajectoryRule(G baseGraph, E eventGraph,
			F framedGraph, Map<String, EdgeFrameFactory> edgeFrameFactories,
			Map<String, VertexFrameFactory> vertexFrameFactories,
			IRuleDelegate delegate, long temporalResolution) {
		super(baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories, delegate);

		this.temporalResolution = temporalResolution;
	}

	@Override
	public void edgeAdded(Edge arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgePropertyChanged(Edge arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgePropertyRemoved(Edge arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edgeRemoved(Edge arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexAdded(Vertex arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexPropertyRemoved(Vertex arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexRemoved(Vertex arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void locationChanged(SpatiotemporalContext hostContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeChanged(SpatiotemporalContext hostContext) {
		Geoshape location = hostContext.getLocation();
		long timestamp = hostContext.getTimestamp();

		if (referenceTime >= 0
				&& (timestamp - referenceTime) % temporalResolution != 0)
			return;

		// trigger trajectory updates
		SpaceTimePosition pos;
		for (Datum datum : delegate.getGoverns()) {
			if (datum.getIsMeasurable()) {
				datum.getDelegate().appendMeasured(datum, location, timestamp);

			} else {
				pos = (SpaceTimePosition) vertexFrameFactories.get(
						ISpaceTimePositionFactory.class.getName())
						.addFramedVertex(null, SpaceTimePosition.class);
				pos.setLocation(location);
				pos.setTimestamp(timestamp);

				datum.getDelegate().append(datum, pos);
			}
		}

		// commit changes
		// baseGraph.commit();

		referenceTime = timestamp;

	}

}
