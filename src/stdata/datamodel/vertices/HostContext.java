package stdata.datamodel.vertices;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

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

	@Property(LOCATION_TRIGGER_KEY)
	public boolean getLocationTrigger();

	/**
	 * Toggles the location trigger property to trigger spatial event listeners.
	 */
	@JavaHandler
	public void triggerLocationUpdate();

	/** Timestamp property. */
	@Property("timestamp")
	public long getTimestamp();

	@Property("timestamp")
	public void setTimestamp(long timestamp);

	@Property(TIMESTAMP_TRIGGER_KEY)
	public void setTimestampTrigger(boolean trigger);

	@Property(TIMESTAMP_TRIGGER_KEY)
	public boolean getTimestampTrigger();

	/**
	 * Toggles the timestamp trigger property to trigger temporal event
	 * listeners.
	 */
	@JavaHandler
	public void triggerTimestampUpdate();

	abstract class Impl implements JavaHandlerContext<Vertex>, HostContext {
		@Override
		@JavaHandler
		public void triggerLocationUpdate() {
			setLocationTrigger(!getLocationTrigger());
		}

		@Override
		@JavaHandler
		public void triggerTimestampUpdate() {
			setTimestampTrigger(!getTimestampTrigger());
		}
	}
}
