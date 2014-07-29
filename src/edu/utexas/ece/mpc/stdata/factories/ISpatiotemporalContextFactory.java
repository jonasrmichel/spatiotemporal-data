package edu.utexas.ece.mpc.stdata.factories;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.vertices.SpatiotemporalContextVertex;

public interface ISpatiotemporalContextFactory {

	/**
	 * Creates a new special vertex that captures spatial and temporal context.
	 * 
	 * @param location
	 *            a geospatial point.
	 * @param timestamp
	 *            a time.
	 * @return the newly created spatiotemporal context vertex.
	 */
	public SpatiotemporalContextVertex addSpatiotemporalContext(Geoshape location,
			long timestamp);
}
