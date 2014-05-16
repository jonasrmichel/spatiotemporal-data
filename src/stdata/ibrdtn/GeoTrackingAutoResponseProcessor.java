package stdata.ibrdtn;

import ibrdtn.api.ExtendedClient;
import ibrdtn.api.object.Block;
import ibrdtn.api.object.Bundle;
import ibrdtn.api.object.EID;
import ibrdtn.api.object.GroupEndpoint;
import ibrdtn.api.object.PayloadBlock;
import ibrdtn.api.object.SingletonEndpoint;
import ibrdtn.example.api.Constants;
import ibrdtn.example.data.Processor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeoTrackingAutoResponseProcessor implements Runnable{

    private static final Logger logger = Logger.getLogger(Processor.class.getName());
    private GeoEnvelope envelope;
    private final ExtendedClient client;
    private final ExecutorService executor;
    // choices are current HopBasedRoutingStrategy or GeoBasedRoutingStrategy
    // new choice: SmartGeoBasedRoutingStrategy -- only one geoentry per logical hop
    private GeoRoutingStrategy geoRoutingStrategy;

    public GeoTrackingAutoResponseProcessor(GeoEnvelope envelope, ExtendedClient client, ExecutorService executor) {
        this.envelope = envelope;
        this.client = client;
        this.executor = executor;
        geoRoutingStrategy = new HopBasedRoutingStrategy();
    }
    
    @Override
    public void run() {
        processMessage();
    }
	
    private void processMessage() {
    	System.out.println("Whew! Got the data to the right place.");
    	System.out.println("Bundle came from: " + envelope.getSource().toString());
    	GeoRoutingExtensionBlock greb = 
    			createGeoBlockFromTrackingBlock(envelope.getExtensionBlock());
		TrackingExtensionBlock teb = new TrackingExtensionBlock(30);
    	sendReturnBundle(new GroupEndpoint(envelope.getGroupID()), greb, teb);
    }
    
    private GeoRoutingExtensionBlock createGeoBlockFromTrackingBlock(TrackingExtensionBlock teb){
    	return geoRoutingStrategy.createBlock(teb.getEntries());
    }
    
    private void sendReturnBundle(EID destination, GeoRoutingExtensionBlock greb, 
    		                      TrackingExtensionBlock teb){
		System.out.println("Sending Bundle");

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
		bundle.appendBlock(greb.getExtensionBlock());
		System.out.println(greb.getExtensionBlock().getData().toString());
		bundle.appendBlock(new PayloadBlock(new String("Got your bundle!").getBytes()));
		send(bundle);
	}
    
    private void send(Bundle bundle) {
		logger.log(Level.INFO, "Sending {0}", bundle);

		final Bundle finalBundle = bundle;
		final ExtendedClient finalClient = this.client;
		
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
}
