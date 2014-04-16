package stdata.simulator;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public class MovingObject {
	
	/** The object's unique identifier. */
	int identifier;
	
	/** The object's current geographic location. */
	Geoshape location;
	
	/** An interface for a spatial (moving object) database. */
	IMovingObjectDatabase mod;
	
	
	public MovingObject(int identifier, IMovingObjectDatabase mod) {
		this.identifier = identifier;
		this.mod = mod;
	}
	
	public void advance(long timestamp) {
		// TODO Auto-generated method stub
	}
}
