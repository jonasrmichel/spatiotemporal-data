package edu.utexas.ece.mpc.stdata.rules;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import edu.utexas.ece.mpc.stdata.factories.ISpaceTimePositionFactory;
import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePositionVertex;
import edu.utexas.ece.mpc.stdata.vertices.SpatiotemporalContextVertex;

public class TemporallyModulatedTrajectoryRule extends
		SpatiotemporalContextRule {

	/** The temporal trajectory resolution (seconds). */
	long temporalResolution;

	/** The reference time. */
	long referenceTime = -1L;

	public TemporallyModulatedTrajectoryRule(long temporalResolution) {
		super();

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
	public void locationChanged(SpatiotemporalContextVertex hostContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeChanged(SpatiotemporalContextVertex hostContext) {
		Geoshape location = hostContext.getLocation();
		long timestamp = hostContext.getTimestamp();

		// if (referenceTime >= 0
		// && (timestamp - referenceTime) % temporalResolution != 0)
		// return;

		// FIXME use uniform units!!!
		if (referenceTime >= 0
				&& (timestamp - referenceTime) < temporalResolution)
			return;

		// trigger trajectory updates
		SpaceTimePositionVertex pos;
		Iterable<DatumVertex> governedData = delegate.getGoverns();
		for (DatumVertex datum : governedData) {
			if (datum.getIsMeasurable()) {
				datum.getDelegate().prependMeasuredPseudo(datum, location, timestamp);

			} else {
				pos = (SpaceTimePositionVertex) vertexFrameFactories.get(
						SpaceTimePositionVertex.class).addVertex(null);
				pos.setLocation(location);
				pos.setTimestamp(timestamp);

				datum.getDelegate().prepend(datum, pos);
			}
		}

		// commit changes
		// baseGraph.commit();

		referenceTime = timestamp;

	}

}
