package stdata.ibrdtn;

import ibrdtn.api.object.PayloadBlock;
import ibrdtn.api.object.EID;
import ibrdtn.api.object.SingletonEndpoint;

public class GeoEnvelope {
	
	private TrackingExtensionBlock extensionBlock;
	private PayloadBlock payloadBlock;
	private SingletonEndpoint source;
	private EID destination;

	public GeoEnvelope(){}
	
	public void setExtensionBlock(TrackingExtensionBlock extensionBlock){
		this.extensionBlock = extensionBlock;
	}
	
	public void setPayloadBlock(PayloadBlock payloadBlock){
		this.payloadBlock = payloadBlock;
	}
	
	public TrackingExtensionBlock getExtensionBlock(){
		return extensionBlock;
	}
	
	public PayloadBlock getPayloadBlock(){
		return payloadBlock;
	}
	
	public void setSource(SingletonEndpoint source){
		this.source = source;
	}
	
	public void setDestination(EID destination){
		this.destination = destination;
	}
	
	public SingletonEndpoint getSource(){
		return source;
	}
	
	public EID getDestination(){
		return destination;
	}

}
