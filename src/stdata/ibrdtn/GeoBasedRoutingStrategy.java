package stdata.ibrdtn;

import java.util.List;

public class GeoBasedRoutingStrategy implements GeoRoutingStrategy{

	public GeoRoutingExtensionBlock createBlock(List<TrackingExtensionBlockEntry> entries, int marginOfError){
    	GeoRoutingExtensionBlock toReturn = new GeoRoutingExtensionBlock(marginOfError);
    	for(TrackingExtensionBlockEntry e : entries){
    		if(e.type.equals(TrackingExtensionBlockEntry.GEOTYPE)){
    			toReturn.addEntry(((GeoDataBlockEntry)e).getLatitude().getValue(),
					          ((GeoDataBlockEntry)e).getLongitude().getValue());
    		}
		}
		return toReturn;
	}
	
}
