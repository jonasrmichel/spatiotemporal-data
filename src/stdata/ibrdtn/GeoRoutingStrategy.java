package stdata.ibrdtn;

import java.util.List;

public interface GeoRoutingStrategy {
	public GeoRoutingExtensionBlock createBlock(List<TrackingExtensionBlockEntry> entries, int marginOfError);
}
