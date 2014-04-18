package stdata.rules;

import java.util.Map;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.HostContext;
import stdata.datamodel.vertices.SpaceTimePosition;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;

public class SpatiallyModulatedTrajectoryRule<G extends FramedGraph<?>> extends
		HostContextChagedRule<G> {
	/** The spatial trajectory resolution (meters). */
	double spatialResolution;

	/** The reference location. */
	Geoshape referenceLocation = null;

	public SpatiallyModulatedTrajectoryRule(G graph, IRuleDelegate delegate,
			double spatialResolution) {
		super(graph, delegate);

		this.spatialResolution = spatialResolution;
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
		Geoshape location = hostContext.getLocation();
		long timestamp  = hostContext.getTimestamp();
		
		// note: Geoshape.Point.getLocation() is in kilometers.
		if (referenceLocation != null
				&& (location.getPoint().distance(referenceLocation.getPoint()) * 1000) < spatialResolution)
			return;

		// trigger trajectory updates
		SpaceTimePosition pos;
		for (Datum datum : delegate.getGoverns()) {
			pos = (SpaceTimePosition) graph.addVertex(null, SpaceTimePosition.class);
			pos.setLocation(location);
			pos.setTimestamp(timestamp);
			
			datum.add(pos);
		}
		
		referenceLocation = location;
	}

	@Override
	public void hostTimeChanged(HostContext hostContext) {
		// TODO Auto-generated method stub

	}

}
