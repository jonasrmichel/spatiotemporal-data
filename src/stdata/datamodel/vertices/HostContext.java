package stdata.datamodel.vertices;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface HostContext extends VertexFrame {
	public static final String LOCATION_TRIGGER_KEY = "location-trigger";
	public static final String TIMESTAMP_TRIGGER_KEY = "timestamp";

	/** Location property. */
	@Property("location")
	public Geoshape getLocation();

	@Property("location")
	public void setLocation(Geoshape location);

	@Property(LOCATION_TRIGGER_KEY)
	public void setLocationTrigger(boolean trigger);

	/** Timestamp property. */
	@Property("timestamp")
	public long getTimestamp();

	@Property("timestamp")
	public void setTimestamp(long timestamp);

	@Property(TIMESTAMP_TRIGGER_KEY)
	public void setTimestampTrigger(boolean trigger);
}
