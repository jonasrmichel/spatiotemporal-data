package edu.utexas.ece.mpc.stdata.rules;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import edu.utexas.ece.mpc.stdata.factories.ISpaceTimePositionFactory;
import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.vertices.Datum;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePosition;
import edu.utexas.ece.mpc.stdata.vertices.SpatiotemporalContext;

public class SpatiallyModulatedTrajectoryRule extends SpatiotemporalContextRule {
	/** The spatial trajectory resolution (meters). */
	double spatialResolution;

	/** The reference location. */
	Geoshape referenceLocation = null;

	public SpatiallyModulatedTrajectoryRule(double spatialResolution) {
		super();

		this.spatialResolution = spatialResolution;
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
		Geoshape location = hostContext.getLocation();
		long timestamp = hostContext.getTimestamp();

		// note: Geoshape.Point.getLocation() is in kilometers.
		if (referenceLocation != null
				&& (location.getPoint().distance(referenceLocation.getPoint()) * 1000) < spatialResolution)
			return;

		// trigger trajectory updates
		SpaceTimePosition pos;
		Iterable<Datum> goverenedData = delegate.getGoverns();
		for (Datum datum : goverenedData) {
			if (datum.getIsMeasurable()) {
				datum.getDelegate().appendMeasured(datum, location, timestamp);

			} else {
				pos = (SpaceTimePosition) vertexFrameFactories.get(
						SpaceTimePosition.class).addVertex(null,
						SpaceTimePosition.class);
				pos.setLocation(location);
				pos.setTimestamp(timestamp);

				datum.getDelegate().append(datum, pos);
			}
		}

		// commit changes
		// baseGraph.commit();

		referenceLocation = location;
	}

	@Override
	public void timeChanged(SpatiotemporalContext hostContext) {
		// TODO Auto-generated method stub

	}

}
