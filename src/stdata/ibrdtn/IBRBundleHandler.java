package stdata.ibrdtn;

import ibrdtn.api.APIException;
import ibrdtn.api.ExtendedClient;
import ibrdtn.api.object.Block;
import ibrdtn.api.object.Bundle;
import ibrdtn.api.object.BundleID;
import ibrdtn.api.object.PayloadBlock;
import ibrdtn.api.sab.Custody;
import ibrdtn.api.sab.StatusReport;
import ibrdtn.example.api.PayloadType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;


//public class IBRBundleHandler extends AbstractAPIHandler{
public class IBRBundleHandler implements ibrdtn.api.sab.CallbackHandler  {
	private static final Logger logger = Logger.getLogger(IBRBundleHandler.class.getName());
	
	
	
	//NEW STUFF
    protected PipedInputStream is;
    protected PipedOutputStream os;
    protected ExtendedClient client;
    protected ExecutorService executor;
    protected Bundle bundle = null;
    protected PayloadBlock workingPayloadBlock = null;
    protected TrackingExtensionBlock workingExtensionBlock = null;
    protected GeoRoutingExtensionBlock workingGeoExtensionBlock = null;
    protected PayloadType messageType;
    protected Thread t;
    protected GeoEnvelope envelope;
    protected String groupID;
    protected byte[] bytes;
    

	public IBRBundleHandler(ExtendedClient client, ExecutorService executor, 
			                PayloadType messageType, String groupID) {
		this.client = client;
		this.executor = executor;
		this.messageType = messageType;
		this.groupID = groupID;
	}

	//NEW STUFF
	/**
     * Marks the Bundle currently in the register as delivered.
     */
    protected void markDelivered() {
        final BundleID finalBundleID = new BundleID(bundle);
        final ExtendedClient finalClient = this.client;

        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Example: add message to database
                // Message msg = new Message(received.source, received.destination, playfile);
                // msg.setCreated(received.timestamp);
                // msg.setReceived(new Date());
                // _database.put(Folder.INBOX, msg);

                try {
                    // Mark bundle as delivered...                    
                    finalClient.markDelivered(finalBundleID);
                    logger.log(Level.FINE, "Delivered: {0}", finalBundleID);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Unable to mark bundle as delivered.", e);
                }
            }
        });
    }

    /**
     * Loads the given bundle from the queue into the register and initiates the file transfer.
     */
    protected void loadAndGet(BundleID bundleId) {
        final BundleID finalBundleId = bundleId;
        final ExtendedClient exClient = this.client;

        envelope = new GeoEnvelope();
        
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    exClient.loadBundle(finalBundleId);
                    exClient.getBundle();
                    logger.log(Level.INFO, "New bundle loaded");
                } catch (APIException e) {
                    logger.log(Level.WARNING, "Failed to load next bundle");
                }
            }
        });
    }

    /**
     * Loads the next bundle from the queue into the register and initiates transfer of the Bundle's meta data.
     */
    protected void loadAndGetInfo(BundleID bundleId) {
        final BundleID finalBundleId = bundleId;
        final ExtendedClient exClient = this.client;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    exClient.loadBundle(finalBundleId);
                    exClient.getBundleInfo();
                    logger.log(Level.INFO, "New bundle loaded, getting meta data");
                } catch (APIException e) {
                    logger.log(Level.WARNING, "Failed to load next bundle");
                }
            }
        });
    }

    /**
     * Initiates transfer of the Bundle's payload. Requires loading the Bundle into the register first.
     */
    protected void getPayload() {
        final ExtendedClient finalClient = this.client;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.log(Level.INFO, "Requesting payload");
                    finalClient.getPayload();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Unable to mark bundle as delivered.", e);
                }
            }
        });
    }

	
	
	@Override
	public void notify(final BundleID id) {
		loadAndGet(id);
	}

	@Override
	public void notify(StatusReport r) {
		logger.log(Level.INFO, r.toString());
	}

	@Override
	public void notify(Custody c) {
		logger.log(Level.INFO, c.toString());
	}

	@Override
	public void startBundle(Bundle bundle) {
		System.out.println("Receiving the following bundle: " + bundle.toString());
		logger.log(Level.FINE, "Receiving: {0}", bundle);
		this.bundle = bundle;
	}

	@Override
	public void endBundle() {
		// logger.log(Level.INFO, "Received: {0}:", bundle);

		/*
		 * Bundle needs to be marked delivered before processing starts concurrently, as the transfer of new Bundles 
		 * might interfere otherwise.
		 */
		markDelivered();
		System.out.println("Received the following bundle: " + bundle.toString());
	
		List<Block> blocks = bundle.getBlocks();
		boolean hasTracking = false;
		boolean hasGeo = false;
		for(Block b : blocks){
			if(b.getType() == TrackingExtensionBlock.type){
				hasTracking = true;
			}
			else if (b.getType() == GeoRoutingExtensionBlock.type){
				hasGeo = true;
			}
		}

		//if I received a tracking extension block but NOT a geoRouting extension block, send a response
		//if(envelope.getExtensionBlock() != null && envelope.getGeoRoutingExtensionBlock() == null){
		if(hasTracking && !hasGeo){
			envelope.setSource(bundle.getSource());
			envelope.setDestination(bundle.getDestination());
			envelope.setGroupID(groupID);
			executor.execute(new GeoTrackingAutoResponseProcessor(envelope, client, executor));
		}
	}

	@Override
	public void startBlock(Block block) {
		System.out.println("Block type: " + block.getType());
		//logger.log(Level.FINE, "Receiving: {0}", block.toString());
		//bundle.appendBlock(block);
		if(block.getType() == PayloadBlock.type){
			this.workingPayloadBlock = (PayloadBlock)block;
		}
		else if(block.getType() == TrackingExtensionBlock.type){
			this.workingExtensionBlock = new TrackingExtensionBlock();
		}
		else if(block.getType() == GeoRoutingExtensionBlock.type){
			this.workingGeoExtensionBlock = new GeoRoutingExtensionBlock();
		}
	}

	@Override
	public void endBlock() {
		logger.log(Level.FINE, "Ending block");
		if(workingPayloadBlock != null){
			bundle.appendBlock(workingPayloadBlock);
			workingPayloadBlock = null;
		}
		else if(workingExtensionBlock != null){
			bundle.appendBlock(workingExtensionBlock.getExtensionBlock());
			workingExtensionBlock = null;
		}
		else if(workingGeoExtensionBlock != null){
			bundle.appendBlock(workingGeoExtensionBlock.getExtensionBlock());
			workingGeoExtensionBlock = null;
		}
	}

	@Override
	public OutputStream startPayload() {
		//logger.log(Level.INFO, "Receiving payload");
		/*
		 * For a detailed description of how different streams affect efficiency, consult:
		 * 
		 * code.google.com/p/io-tools
		 */
		is = new PipedInputStream();
		try {
			os = new PipedOutputStream(is);

			// Concurrently read data from stream lest the buffer runs full and creates a deadlock situation
			t = new Thread(new ShimPipedStreamReader());
			t.start();

		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Opening pipes failed", ex);
		}

		return os;
	}

	@Override
	public void endPayload() {
		if (os != null) {
			try {
				t.join();
			} catch (Exception ex) {
				logger.log(Level.SEVERE, null, ex);
			} finally {
				try {
					if (os != null) {
						os.close();
					}
				} catch (IOException ex) {
					logger.log(Level.SEVERE, "Failed to close streams", ex);
				}
			}
		}
	}

	@Override
	public void progress(long pos, long total) {
		logger.log(Level.INFO, "Payload: {0} of {1} bytes", new Object[]{pos, total});
	}

	class ShimPipedStreamReader implements Runnable {

		@Override
		public void run() {
			ByteArrayOutputStream buffer;
			try {
				buffer = new ByteArrayOutputStream();
				int nRead;
				byte[] data = new byte[16384];
				while ((nRead = is.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				buffer.flush();
				bytes = buffer.toByteArray();
				StringBuilder sb = new StringBuilder();
				for (byte b : bytes) {
					sb.append(String.format("%02X ", b));
				}
				if(workingPayloadBlock != null){
					workingPayloadBlock.setData(data);
				}
				else if(workingExtensionBlock != null){
					System.out.println("bytes received: " + sb.toString());
					workingExtensionBlock.setData(bytes);
					envelope.setExtensionBlock(workingExtensionBlock);
				}
				else if(workingGeoExtensionBlock != null){
					System.out.println("bytes received: " + sb.toString());
					workingGeoExtensionBlock.setData(bytes);
					envelope.setGeoRoutingExtensionBlock(workingGeoExtensionBlock);
				}
				logger.log(Level.INFO, "Block Data received: \n\t{0} [{1}]",
						new Object[]{sb.toString(), new String(bytes)});
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "Unable to decode payload");
			}
		}
	}



}
