package stdata.ibrdtn;

import java.util.List;

public class SmartGeoBasedRoutingStrategy implements GeoRoutingStrategy{
	
	public GeoRoutingExtensionBlock createBlock(List<TrackingExtensionBlockEntry> entries, int marginOfError){
    	GeoRoutingExtensionBlock toReturn = new GeoRoutingExtensionBlock(marginOfError);
    	boolean findGeo = true;
    	for(TrackingExtensionBlockEntry e : entries){
    		if(findGeo && e.type.equals(TrackingExtensionBlockEntry.GEOTYPE)){
    			toReturn.addEntry(((GeoDataBlockEntry)e).getLatitude().getValue(),
					              ((GeoDataBlockEntry)e).getLongitude().getValue());
    			findGeo = false;
    		}
    		else if(!findGeo && e.type.equals(TrackingExtensionBlockEntry.HOPTYPE)){
    			//found the next hop; switch back to looking for geo
    			findGeo = true;
    		}
		}
		return toReturn;
	}

}
