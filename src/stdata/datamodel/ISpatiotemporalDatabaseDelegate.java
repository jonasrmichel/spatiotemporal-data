package stdata.datamodel;

import stdata.geo.Geoshape;

public interface ISpatiotemporalDatabaseDelegate {

	/**
	 * Called to obtain the current geographic location.
	 * 
	 * @return a geographic point representing the current location.
	 */
	public Geoshape getLocation();

	/**
	 * Called to obtain the current time.
	 * 
	 * @return the current time.
	 */
	public long getTimestamp();
}
