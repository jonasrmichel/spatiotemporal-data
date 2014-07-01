package stdata;

import stdata.geo.Geoshape;

public interface ContextProvider {
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

	/**
	 * Called to obtain the logical domain.
	 * 
	 * @return a String representing the logical domain.
	 */
	public String getDomain();
}
