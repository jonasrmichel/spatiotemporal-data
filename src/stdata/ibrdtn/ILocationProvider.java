package stdata.ibrdtn;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public interface ILocationProvider {

	/**
	 * Returns the current geographic location for the IBR-DTN host with the
	 * provided eid.
	 * 
	 * @param eid
	 * @return
	 */
	public Geoshape getLocation(String eid);

}
