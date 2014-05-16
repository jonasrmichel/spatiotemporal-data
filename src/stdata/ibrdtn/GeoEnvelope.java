package stdata.ibrdtn;

import ibrdtn.api.object.PayloadBlock;
import ibrdtn.api.object.EID;
import ibrdtn.api.object.SingletonEndpoint;

public class GeoEnvelope {
	
	private TrackingExtensionBlock extensionBlock;
	private GeoRoutingExtensionBlock geoRoutingBlock;
	private SingletonEndpoint source;
	private EID destination;
	private String groupID;

	public GeoEnvelope(){}
	
	public void setExtensionBlock(TrackingExtensionBlock extensionBlock){
		this.extensionBlock = extensionBlock;
	}
	
	public void setGeoRoutingExtensionBlock(GeoRoutingExtensionBlock geoRoutingBlock){
		this.geoRoutingBlock = geoRoutingBlock;
	}
	
	public TrackingExtensionBlock getExtensionBlock(){
		return extensionBlock;
	}
	
	public GeoRoutingExtensionBlock getGeoRoutingExtensionBlock(){
		return geoRoutingBlock;
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
	
	public void setGroupID(String groupID){
		this.groupID = groupID;
	}
	
	public String getGroupID(){
		return groupID;
	}

}
