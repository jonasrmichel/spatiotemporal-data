package stdata.datamodel.vertices;

import stdata.geo.Geoshape;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface SpatiotemporalContext extends VertexFrame {
	public static final String LOCATION_KEY = "location";
	public static final String TIMESTAMP_KEY = "timestamp";
	
	/** Location property. */
	@Property(LOCATION_KEY)
	public Geoshape getLocation();

	@Property(LOCATION_KEY)
	public void setLocation(Geoshape location);

	/** Timestamp property. */
	@Property(TIMESTAMP_KEY)
	public long getTimestamp();

	@Property(TIMESTAMP_KEY)
	public void setTimestamp(long timestamp);

}
