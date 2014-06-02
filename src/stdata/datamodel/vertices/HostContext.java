package stdata.datamodel.vertices;

import stdata.geo.Geoshape;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface HostContext extends VertexFrame {
	/** Location-based event trigger property key. */
	public static final String LOCATION_TRIGGER_KEY = "location-trigger";

	/** Timestamp-based event trigger property key. */
	public static final String TIMESTAMP_TRIGGER_KEY = "timestamp-trigger";

	/** Location property. */
	@Property("location")
	public Geoshape getLocation();

	@Property("location")
	public void setLocation(Geoshape location);

	@Property(LOCATION_TRIGGER_KEY)
	public void setLocationTrigger(boolean trigger);

	@Property(LOCATION_TRIGGER_KEY)
	public boolean getLocationTrigger();

	/** Timestamp property. */
	@Property("timestamp")
	public long getTimestamp();

	@Property("timestamp")
	public void setTimestamp(long timestamp);

	@Property(TIMESTAMP_TRIGGER_KEY)
	public void setTimestampTrigger(boolean trigger);

	@Property(TIMESTAMP_TRIGGER_KEY)
	public boolean getTimestampTrigger();

}
