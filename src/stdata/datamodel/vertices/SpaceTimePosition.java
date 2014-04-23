package stdata.datamodel.vertices;

import java.util.Iterator;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONMode;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

public interface SpaceTimePosition extends VertexFrame {
	public static final String LOCATION_KEY = "location";
	public static final String TIMESTAMP_KEY = "timestamp";
	public static final String DOMAIN_KEY = "domain";

	/** Location property. */
	@Property(LOCATION_KEY)
	public Geoshape getLocation();

	@Property(LOCATION_KEY)
	public void setLocation(Geoshape location);

	// /** Latitude property. */
	// @Property("latitude")
	// public float getLatitude();
	//
	// @Property("latitude")
	// public void setLatitude(float latitude);
	//
	// /** Longitude property. */
	// @Property("longitude")
	// public float getLongitude();
	//
	// @Property("longitude")
	// public void setLongitude(float longitude);

	/** Timestamp property. */
	@Property(TIMESTAMP_KEY)
	public long getTimestamp();

	@Property(TIMESTAMP_KEY)
	public void setTimestamp(long timestamp);

	/** Logical ownership property. */
	@Property(DOMAIN_KEY)
	public String getDomain();

	@Property(DOMAIN_KEY)
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

	/**
	 * Marshals (serializes) the space-time position into JSON form.
	 * 
	 * @returns the JSON marshaled space-time position.
	 * @throws JSONException
	 */
	@JavaHandler
	public JSONObject marshal() throws JSONException;

	/**
	 * Unmarshals (deserializes) a JSON object into this space-time position.
	 * 
	 * @param json
	 *            a marshaled space-time position.
	 * @throws JSONException
	 */
	@JavaHandler
	public void unmarshal(JSONObject json) throws JSONException;

	abstract class Impl implements JavaHandlerContext<Vertex>,
			SpaceTimePosition {

		@Override
		@JavaHandler
		public JSONObject marshal() throws JSONException {
			JSONObject json = new JSONObject();

			// marshal the postion's properties
			for (String key : asVertex().getPropertyKeys()) {
				if (key.equals(LOCATION_KEY))
					continue; // location is a special case handled below

				json.put(key, asVertex().getProperty(key));
			}

			// marshal the location property
			json.put("latitude", getLocation().getPoint().getLatitude());
			json.put("longitude", getLocation().getPoint().getLongitude());

			return json;
		}

		@Override
		@JavaHandler
		public void unmarshal(JSONObject json) throws JSONException {
			// unmarshal the position's properties
			Iterator<String> keys = json.keys();
			String key;
			while (keys.hasNext()) {
				key = keys.next();
				if (key.equals("latitude") || key.equals("longitude")
						|| key.equals("_id") || key.equals("_type"))
					continue;

				asVertex().setProperty(key, json.get(key));
			}

			// unmarshal the position's location
			setLocation(Geoshape.point(json.getDouble("latitude"),
					json.getDouble("longitude")));
		}
	}
}
