package stdata.ibrdtn;

import ibrdtn.api.object.SDNV;

import ibrdtn.api.APIException;
import ibrdtn.api.ExtendedClient;
import ibrdtn.api.object.Bundle;
import ibrdtn.api.object.Block;
import ibrdtn.api.object.EID;
import ibrdtn.api.object.PayloadBlock;
import ibrdtn.api.object.SingletonEndpoint;
import ibrdtn.example.api.Constants;
import ibrdtn.example.api.PayloadType;

import java.io.IOException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;

import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import stdata.datamodel.SpatiotemporalDatabase;
import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.HostContext;
import stdata.rules.SpatiallyModulatedTrajectoryRule;
import stdata.titan.TitanSpatiotemporalDatabase;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

public class IBRDTNHost {
	/** The host's identifier. */
	int identifier;

	/** Spatiotemporal database file paths. */
	String graphDir, indexDir;

	/** The host's geographic location provider. */
	ILocationProvider locationProvider;

	/** The host's spatiotemporal graph database. */
	SpatiotemporalDatabase<TitanGraph, Datum> spatiotemporalDB;

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

	private static final Logger logger = Logger.getLogger(IBRDTNHost.class
			.getName());

	public IBRDTNHost(int identifier, String eid, String graphDir,
			String indexDir, ILocationProvider locationProvider) {
		this.identifier = identifier;
		this.eid = eid;
		this.graphDir = graphDir;
		this.indexDir = indexDir;
		this.locationProvider = locationProvider;

		System.out.println(graphDir);
		System.out.println(indexDir);

		// initialize the spatiotemporal graph database instance
		spatiotemporalDB = new TitanSpatiotemporalDatabase<Datum>(eid,
				graphDir, indexDir, Datum.class);

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
			logger.log(Level.WARNING, "Could not connect to DTN daemon: {0}",
					e.getMessage());
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

	private Datum createDatum(Geoshape phenomenonLocation) {
		SpatiallyModulatedTrajectoryRule<FramedGraph<TitanGraph>, EventGraph<TitanGraph>> spatiallyModulatedRule = 
				new SpatiallyModulatedTrajectoryRule<FramedGraph<TitanGraph>, EventGraph<TitanGraph>>(
				spatiotemporalDB.framedGraph, spatiotemporalDB.eventGraph,
				trajectorySpatialResolution);
		Datum spatiallyModulatedDatum = spatiotemporalDB.datumFactory.addDatum(
				phenomenonLocation, phenomenonLocation, System.currentTimeMillis(),
				Integer.toString(identifier), null, spatiallyModulatedRule);
		spatiallyModulatedDatum.asVertex().setProperty("Text", "Hello World!");
		return spatiallyModulatedDatum;

		// If I understand correctly, at this point, this datum, which I also
		// have a handle
		// to is actually in the database, too.
	}

	
	private TrackingExtensionBlock createExtensionBlock(long interval){
		TrackingExtensionBlock toReturn = new TrackingExtensionBlock(interval);
		return toReturn;
	}
	
	private void extractAndSendBundles(String dest, TrackingExtensionBlock teb){
		Iterable<Datum> frames = spatiotemporalDB.getFramedVertices(Datum.class);
		for(Datum d : frames){
			JSONObject jo = null; 
			try{
				jo = d.marshal();
			}
			catch(JSONException je){
				je.printStackTrace();
			}
			if (jo != null) {
				try {
					byte[] sendData = jo.toString().getBytes("utf-8");
					sendBundle(dest, sendData, teb);
				}
				catch(UnsupportedEncodingException uee){
					uee.printStackTrace();
				}
			}
		}
	}

    /**
     * Application-level method to prep a bundle and send down to the send method,
     * which will actually chuck it through the DTN daemon.
     */
	private void sendBundle(String dest, byte[] data, TrackingExtensionBlock teb){
		System.out.println("Sending Bundle");

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

		bundle.appendBlock(teb.getExtensionBlock());
		System.out.println(teb.getExtensionBlock().getData().toString());
		bundle.appendBlock(new PayloadBlock(data));
		send(bundle);
	}

	/**
	 * Low level functions to send the bundle through the DTN daemon
	 */
	private void send(Bundle bundle) {
		logger.log(Level.INFO, "Sending {0}", bundle);

		final Bundle finalBundle = bundle;
		final ExtendedClient finalClient = this.exClient;
		
		LinkedList<Block> blocks = finalBundle.getBlocks();
		for(Block b : blocks){
			System.out.println("My bundle has a block of type: " + b.getType());
			System.out.println("That block's data is: " + b.getData());
		}
		System.out.println("Sending the following bunde: " + finalBundle.toString());
		
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
	
	
	static void clearGraphDirectory(String dir) {
		try {
			File folder = new File(dir);

			// delete any existing directory at this path
			if (folder.exists()) {
				if (!folder.isDirectory()) {
					System.err.println("Error: " + dir + " is not a directory");
					System.exit(1);
				}

				FileUtils.deleteDirectory(folder);
			}

			// create a new empty directory at this path
			if (!folder.mkdir()) {
				System.err.println("Error: could not create directory "
						+ folder.getAbsolutePath());
				System.exit(1);
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
    public static void main(String[] args){
    	if(args.length != 2){
    		System.out.println("Usage: java -jar stdataibrdtn.jar [send/respond] [destination].");
    		System.exit(0);
    	}
    	Geoshape defaultLocation = Geoshape.point(30.2500, 97.7500);
    	ILocationProvider locationProvider = new DumbLocationProviderImpl(defaultLocation);
    	IBRDTNHost.clearGraphDirectory("./graphDir/");
    	IBRDTNHost host = new IBRDTNHost(1, "ibr-1", "./graphDir/", "./indexDir/", locationProvider);
    	//this first method creates a datum and thinks its putting it in the database
    	Datum d = host.createDatum(defaultLocation);
    	//this second method just sends the datum returned from above using the datum's marshall method
    	//I'm doing it this way because I can't seem to recover stuff from the database
    	//host.hackSendBundle(args[0], d);
    	//Eventually, I think this should work
    	if(args[0].equals("send")){
    		TrackingExtensionBlock teb = host.createExtensionBlock(30);
    		host.extractAndSendBundles(args[1], teb);
    	}
    	
    	
    	/*SDNV test = new SDNV(1048);
    	System.out.println("value: " + test.getValue());
    	   StringBuilder sb = new StringBuilder();
    	   for(byte b: test.getBytes())
    	      sb.append(String.format("%02x", b&0xff));
    	   System.out.println(sb.toString());*/
    	   
    	/*IBRDTNHost host = new IBRDTNHost(1, "ibr-1", "/Users/christinejulien/hackathon/spatiotemporal-data/graphDir/", "/Users/christinejulien/hackathon/spatiotemporal-data/indexDir/", null);
    	TrackingExtensionBlock teb = host.createExtensionBlock(265);
    	SDNV test = new SDNV(43);
    	byte[] bytetest = new byte[1];
    	bytetest[0] = (byte)43;
    	SDNV test2 = new SDNV(bytetest);
    	System.out.println("test2 length: " + test2.length);
    	System.out.println("test2 value: " + test2.getValue());*/
    }


}
