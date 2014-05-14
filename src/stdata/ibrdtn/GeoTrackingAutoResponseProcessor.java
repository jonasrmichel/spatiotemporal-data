package stdata.ibrdtn;

import ibrdtn.api.ExtendedClient;
import ibrdtn.api.object.Block;
import ibrdtn.api.object.Bundle;
import ibrdtn.api.object.EID;
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

    public GeoTrackingAutoResponseProcessor(GeoEnvelope envelope, ExtendedClient client, ExecutorService executor) {
        this.envelope = envelope;
        this.client = client;
        this.executor = executor;
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
    	sendReturnBundle(envelope.getSource().toString(), greb);
    }
    
    private GeoRoutingExtensionBlock createGeoBlockFromTrackingBlock(TrackingExtensionBlock teb){
    	GeoRoutingExtensionBlock toReturn = new GeoRoutingExtensionBlock();
    	List<TrackingExtensionBlockEntry> trackingEntries = teb.getEntries();
    	// this one retraces the network hops
    	/*for(TrackingExtensionBlockEntry e : trackingEntries){
    		if(e.type.equals(TrackingExtensionBlockEntry.HOPTYPE)){
    			toReturn.addEntry(((HopDataBlockEntry)e).getEID());
    		}
    	}*/
    	// this one retraces the locations
    	for(TrackingExtensionBlockEntry e : trackingEntries){
    		if(e.type.equals(TrackingExtensionBlockEntry.GEOTYPE)){
    			toReturn.addEntry(((GeoDataBlockEntry)e).getLatitude().getValue(),
    					          ((GeoDataBlockEntry)e).getLongitude().getValue());
    		}
    	}
    	return toReturn;
    }
    
    private void sendReturnBundle(String dest, GeoRoutingExtensionBlock greb){
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

		bundle.appendBlock(greb.getExtensionBlock());
		System.out.println(greb.getExtensionBlock().getData().toString());
		bundle.appendBlock(envelope.getPayloadBlock());
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
					//finalClient.send(finalBundle);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Unable to send bundle", e);
				}
			}
		});
	}
}
