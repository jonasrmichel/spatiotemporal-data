package edu.utexas.ece.mpc.stdata.vertices;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;

public interface GeoVertex extends VertexFrame {
	/** Location property key. */
	public static final String LOCATION_KEY = "location";

	/** Location property. */
	@Property(LOCATION_KEY)
	public Geoshape getLocation();

	@Property(LOCATION_KEY)
	public void setLocation(Geoshape location);

}
