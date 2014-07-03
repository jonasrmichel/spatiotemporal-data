package stdata.datamodel.vertices;

import stdata.geo.Geoshape;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface GeoVertex extends VertexFrame {
	/** Location property key. */
	public static final String LOCATION_KEY = "location";

	/** Location property. */
	@Property(LOCATION_KEY)
	public Geoshape getLocation();

	@Property(LOCATION_KEY)
	public void setLocation(Geoshape location);

}
