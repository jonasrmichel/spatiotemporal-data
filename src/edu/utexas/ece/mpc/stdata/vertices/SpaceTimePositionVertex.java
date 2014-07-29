package edu.utexas.ece.mpc.stdata.vertices;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;

public interface SpaceTimePositionVertex extends GeoVertex {
//	/** Latitude property. */
//	@Property("latitude")
//	public float getLatitude();
//
//	@Property("latitude")
//	public void setLatitude(float latitude);
//
//	/** Longitude property. */
//	@Property("longitude")
//	public float getLongitude();
//
//	@Property("longitude")
//	public void setLongitude(float longitude);

	/** Timestamp property. */
	@Property("timestamp")
	public long getTimestamp();

	@Property("timestamp")
	public void setTimestamp(long timestamp);

	/** Logical ownership property. */
	@Property("domain")
	public String getDomain();

	@Property("domain")
	public void setDomain(String domain);
	
	/** Datum whose trajectory this space-time position is part of. */
	@Adjacency(label = "trajectory")
	public DatumVertex getDatum();

	@Adjacency(label = "trajectory")
	public void setDatum(DatumVertex datum);

	/** Forward and backward directions in the trajectory. */
	@Adjacency(label = "next", direction = Direction.OUT)
	public SpaceTimePositionVertex getNext();

	@Adjacency(label = "next", direction = Direction.OUT)
	public void setNext(SpaceTimePositionVertex position);

	@Adjacency(label = "previous", direction = Direction.IN)
	public SpaceTimePositionVertex getPrevious();

	@Adjacency(label = "previous", direction = Direction.IN)
	public void setPrevious(SpaceTimePositionVertex position);

	// TODO: encode get/can/is logic as GremlinGroovy annotations
	// https://github.com/tinkerpop/frames/wiki/Gremlin-Groovy

}
