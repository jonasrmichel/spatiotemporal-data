package stdata.ibrdtn;

import java.util.List;

public class HopBasedRoutingStrategy implements GeoRoutingStrategy{

	public GeoRoutingExtensionBlock createBlock(List<TrackingExtensionBlockEntry> entries, int marginOfError){
    	GeoRoutingExtensionBlock toReturn = new GeoRoutingExtensionBlock(marginOfError);
		for(TrackingExtensionBlockEntry e : entries){
    		if(e.type.equals(TrackingExtensionBlockEntry.HOPTYPE)){
    			toReturn.addEntry(((HopDataBlockEntry)e).getEID());
    		}
    	}
		return toReturn;
	}
}
