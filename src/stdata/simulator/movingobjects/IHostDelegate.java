package stdata.simulator.movingobjects;

import stdata.geo.Geoshape;

public interface IHostDelegate {

	/**
	 * Obtains the current geospatial location of the phenomenon moving object
	 * with the provided identifier at the given simulation time.
	 * 
	 * @param identifier
	 * @param time
	 * @return
	 */
	public Geoshape getPhenomenonLocation(int identifier, int time);
}
