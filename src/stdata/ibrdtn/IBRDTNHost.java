package stdata.ibrdtn;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import ibrdtn.api.APIException;
import ibrdtn.api.ExtendedClient;
import ibrdtn.api.object.Bundle;
import ibrdtn.api.object.EID;
import ibrdtn.api.object.PayloadBlock;
import ibrdtn.api.object.SingletonEndpoint;
import ibrdtn.example.api.Constants;
import ibrdtn.example.api.PayloadType;
import stdata.datamodel.SpatiotemporalDatabase;
import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.HostContext;
import stdata.rules.SpatiallyModulatedTrajectoryRule;
import stdata.titan.TitanSpatiotemporalDatabase;
import stdata.datamodel.vertices.MeasuredDatum;
import stdata.datamodel.vertices.MeasuredDatum.TriggerType;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.thinkaurelius.titan.graphdb.vertices.StandardVertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONException;

public class IBRDTNHost {
	/** The host's identifier. */
	int identifier;
	
	/** Spatiotemporal database file paths. */
	String graphDir, indexDir;

	/** The host's geographic location provider. */
	ILocationProvider locationProvider;

	/** The host's spatiotemporal graph database. */
	SpatiotemporalDatabase<TitanGraph, MeasuredDatum> spatiotemporalDB;
	
	/** The spatial resolution (meters) of spatially-modulated trajectories. */
	double trajectorySpatialResolution = 10;

	/**
	 * The host's special context vertex that provides the graph database with
	 * access to the host's location and current time.
	 */
	HostContext hostContext;

	/** The host's connection to the IBR-DTN daemon. */
	private ExtendedClient exClient = null;
	private ExecutorService executor;
	protected String eid;
	protected PayloadType payloadType = PayloadType.BYTE;
	private IBRBundleHandler handler = null;

	private static final Logger logger = Logger.getLogger(IBRDTNHost.class.getName());

	public IBRDTNHost(int identifier, String eid, String graphDir, String indexDir,
			ILocationProvider locationProvider) {
		this.identifier = identifier;
		this.eid = eid;
		this.graphDir = graphDir;
		this.indexDir = indexDir;
		this.locationProvider = locationProvider;

		System.out.println(graphDir);
		System.out.println(indexDir);
		
		// initialize the spatiotemporal graph database instance
		spatiotemporalDB = new TitanSpatiotemporalDatabase<MeasuredDatum>(eid,
				graphDir, indexDir, MeasuredDatum.class);

		// create a special vertex in the spatiotemporal database to store the
		// host's position notion of time
		hostContext = spatiotemporalDB.addVertex(null, HostContext.class);

		// init connection to IBR-DTN daemon		
		executor = Executors.newSingleThreadExecutor();
		exClient = new ExtendedClient();
		handler = new IBRBundleHandler(exClient, executor, payloadType);
		exClient.setHandler(handler);
		exClient.setHost(Constants.HOST);
		exClient.setPort(Constants.PORT);
		connect();
	}

    /**
     * Opens our connection to the DTN daemon.
     */
	private void connect() {
		try {
			exClient.open();
			logger.log(Level.FINE, "Successfully connected to DTN daemon");
			exClient.setEndpoint(eid);
			logger.log(Level.INFO, "Endpoint ''{0}'' registered.", eid);

		} catch (APIException e) {
			logger.log(Level.WARNING, "API error: {0}", e.getMessage());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Could not connect to DTN daemon: {0}", e.getMessage());
		}
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
	
	private void createDatum(Geoshape phenomenonLocation){
		SpatiallyModulatedTrajectoryRule<FramedGraph<TitanGraph>> spatiallyModulatedRule = 
				new SpatiallyModulatedTrajectoryRule<FramedGraph<TitanGraph>>(
				spatiotemporalDB.framedGraph, trajectorySpatialResolution);
		MeasuredDatum spatiallyModulatedDatum;
		spatiallyModulatedDatum = spatiotemporalDB.datumFactory.addDatum(
				phenomenonLocation, phenomenonLocation, System.currentTimeMillis(),
				Integer.toString(identifier), null, spatiallyModulatedRule);
		// configure the MeasuredDatum properties
		spatiallyModulatedDatum.setPhenomenonIdentifier(identifier);
		spatiallyModulatedDatum.setTriggerType(TriggerType.SPATIAL);
		
		//If I understand correctly, at this point, this datum, which I also have a handle
		//to is actually in the database, too.
	}

    /**
     * Application-level method to prep a bundle and send down to the send method,
     * which will actually chuck it through the DTN daemon.
     */
	private void sendBundle(String dest){
		//first thing's first. Let's grab a bundle to send.
		Iterable<MeasuredDatum> frames = spatiotemporalDB.getFramedVertices(MeasuredDatum.class);
		for(Object datum : frames){
			System.out.println(datum);
			System.out.println(datum.getClass());
			/*JSONObject jo;
			try{
				jo = datum.marshall();
			}
			catch(JSONException je){
				logger.log(Level.WARNING, "Couldn't serialize object.");
			}*/
			System.out.println("Yay! I've got my bytes!");
		}
		if(!frames.iterator().hasNext()){
			System.out.println("Whoops! We did something wrong!");
		}
		
		EID destination = new SingletonEndpoint(dest);
		Bundle bundle = new Bundle(destination, Constants.LIFETIME);
		bundle.setPriority(Bundle.Priority.valueOf("NORMAL"));
		bundle.setFlag(Bundle.Flags.CUSTODY_REPORT, false);
		bundle.setFlag(Bundle.Flags.DELETION_REPORT, false);
		bundle.setFlag(Bundle.Flags.RECEPTION_REPORT, false);
		bundle.setFlag(Bundle.Flags.FORWARD_REPORT, false);
		bundle.setFlag(Bundle.Flags.DELIVERY_REPORT, false);
		bundle.setFlag(Bundle.Flags.COMPRESSION_REQUEST, false);

		// DTNSEC
		bundle.setFlag(Bundle.Flags.DTNSEC_REQUEST_ENCRYPT, false);
		bundle.setFlag(Bundle.Flags.DTNSEC_REQUEST_SIGN, false);

		String text = "Hello world!";
		bundle.appendBlock(new PayloadBlock(text.getBytes()));
		send(bundle);
	}
	
    /**
     * Low level functions to send the bundle through the DTN daemon
     */
	private void send(Bundle bundle) {
		logger.log(Level.INFO, "Sending {0}", bundle);

		final Bundle finalBundle = bundle;
		final ExtendedClient finalClient = this.exClient;

		executor.execute(new Runnable() {
			@Override
			public void run() {

				try {
					finalClient.send(finalBundle);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Unable to send bundle", e);
				}
			}
		});
	}
	
    public static void main(String[] args){
    	if(args.length != 1){
    		System.out.println("You must provide a destination!");
    		System.exit(0);
    	}
    	Geoshape defaultLocation = Geoshape.point(30.2500, 97.7500);
    	ILocationProvider locationProvider = new DumbLocationProviderImpl(defaultLocation);
    	IBRDTNHost host = new IBRDTNHost(1, "ibr-1", "/Users/christinejulien/hackathon/spatiotemporal-data/graphDir/", "/Users/christinejulien/hackathon/spatiotemporal-data/indexDir/", locationProvider);
    	host.createDatum(defaultLocation);
    	host.sendBundle(args[0]);
    }

}
