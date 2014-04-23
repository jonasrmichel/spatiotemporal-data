package stdata.ibrdtn;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public class DumbLocationProviderImpl implements ILocationProvider{
	private Geoshape location;
	
	public DumbLocationProviderImpl(Geoshape location){
		this.location = location;
	}
	
	public Geoshape getLocation(String eid){
		return location;
	}

}
