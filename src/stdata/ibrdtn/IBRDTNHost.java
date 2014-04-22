package stdata.ibrdtn;

import ibrdtn.example.api.APIHandlerType;
import ibrdtn.example.api.DTNClient;
import ibrdtn.example.api.PayloadType;
import stdata.datamodel.SpatiotemporalDatabase;
import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.HostContext;
import stdata.titan.TitanSpatiotemporalDatabase;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Geoshape;

public class IBRDTNHost {
	/** Spatiotemporal database file paths. */
	String graphDir, indexDir;

	/** The host's geographic location provider. */
	ILocationProvider locationProvider;

	/** The host's spatiotemporal graph database. */
	SpatiotemporalDatabase<TitanGraph, Datum> spatiotemporalDB;

	/**
	 * The host's special context vertex that provides the graph database with
	 * access to the host's location and current time.
	 */
	HostContext hostContext;

	/** The host's connection to the IBR-DTN daemon. */
	DTNClient dtnClient;
	String eid;
	PayloadType payloadType = PayloadType.BYTE;
	APIHandlerType handlerType = APIHandlerType.PASSTHROUGH;

	public IBRDTNHost(String eid, String graphDir, String indexDir,
			ILocationProvider locationProvider) {
		this.eid = eid;
		this.graphDir = graphDir;
		this.indexDir = indexDir;
		this.locationProvider = locationProvider;

		// initialize the spatiotemporal graph database instance
		spatiotemporalDB = new TitanSpatiotemporalDatabase<Datum>(eid,
				graphDir, indexDir, Datum.class);

		// create a special vertex in the spatiotemporal database to store the
		// host's position notion of time
		hostContext = spatiotemporalDB.addVertex(null, HostContext.class);

		// init connection to IBR-DTN daemon
		dtnClient = new DTNClient(eid, payloadType, handlerType);
	}

	/**
	 * Safely shuts down the IBR-DTN host.
	 */
	public void shutdown() {
		// shutdown the spatiotemporal database
		spatiotemporalDB.shutdown();
	}

	/**
	 * Returns the host's current geographic location.
	 * 
	 * @return
	 */
	public Geoshape getLocation() {
		return hostContext.getLocation();
	}

	/**
	 * Sets the host's current location.
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setLocation(double latitude, double longitude) {
		hostContext.setLocation(Geoshape.point(latitude, longitude));
	}

	/**
	 * Returns the host's current notion of time.
	 * 
	 * @return
	 */
	public long getTime() {
		return hostContext.getTimestamp();
	}

	/**
	 * Sets the host's current notion of time.
	 * 
	 * @param timestamp
	 */
	public void setTime(long timestamp) {
		hostContext.getTimestamp();
	}

}
