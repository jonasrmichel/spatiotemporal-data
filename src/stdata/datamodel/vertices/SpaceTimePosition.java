package stdata.datamodel.vertices;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface SpaceTimePosition extends VertexFrame {

	/** Place property. */
	@Property("location")
	public Geoshape getLocation();
	
	@Property("location")
	public void setLocation(Geoshape location);
	
//	/** Latitude property. */
//	@Property("latitude")
//	public double getLatitude();
//
//	@Property("latitude")
//	public void setLatitude(double latitude);
//
//	/** Longitude property. */
//	@Property("longitude")
//	public double getLongitude();
//
//	@Property("longitude")
//	public void setLongitude(double longitude);

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
	public Datum getDatum();

	@Adjacency(label = "trajectory")
	public void setDatum(Datum datum);

	/** Forward and backward directions in the trajectory. */
	@Adjacency(label = "next", direction = Direction.OUT)
	public SpaceTimePosition getNext();

	@Adjacency(label = "next", direction = Direction.OUT)
	public void setNext(SpaceTimePosition position);

	@Adjacency(label = "previous", direction = Direction.IN)
	public SpaceTimePosition getPrevious();

	@Adjacency(label = "previous", direction = Direction.IN)
	public void setPrevious(SpaceTimePosition position);

	// TODO: encode get/can/is logic as GremlinGroovy annotations
	// https://github.com/tinkerpop/frames/wiki/Gremlin-Groovy

}
