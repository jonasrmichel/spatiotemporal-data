package edu.utexas.ece.mpc.stdata.factories;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.vertices.SpatiotemporalContext;

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
	public SpatiotemporalContext addSpatiotemporalContext(Geoshape location,
			long timestamp);
}
