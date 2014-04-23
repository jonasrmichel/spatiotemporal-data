package stdata.datamodel.vertices;

import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import stdata.datamodel.ISpaceTimePositionFactory;
import stdata.datamodel.edges.Context;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONMode;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONUtility;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

public interface Datum extends VertexFrame {

	/** Geospatial location of the sensed phenomenon this datum represents. */
	@Property("location")
	public Geoshape getLocation();

	@Property("location")
	public void setLocation(Geoshape location);

	/** The head of the datum's trajectory. */
	@Adjacency(label = "trajectory-head")
	public SpaceTimePosition getTrajectoryHead();

	@Adjacency(label = "trajectory-head")
	public void setTrajectoryHead(SpaceTimePosition position);

	/** The entire datum trajectory. */
	@Adjacency(label = "trajectory", direction = Direction.BOTH)
	public Iterable<SpaceTimePosition> getTrajectory();

	/** Contextual relations. */
	@Adjacency(label = "context")
	public Iterable<Datum> getContextData();

	@Incidence(label = "context")
	public Iterable<Context> getContext();

	@Adjacency(label = "context")
	public void setContextData(Iterable<Datum> data);

	@Adjacency(label = "context")
	public void addContextData(Datum datum);

	@Incidence(label = "context")
	public Context addContext(Datum datum);

	@Adjacency(label = "context")
	public void removeContextData(Datum datum);

	@Incidence(label = "context")
	public void removeContext(Context context);

	// TODO: encode get/can/is logic as GremlinGroovy annotations
	// https://github.com/tinkerpop/frames/wiki/Gremlin-Groovy

	/**
	 * Adds a new space-time position to the datum's trajectory (i.e., at the
	 * head of the trajectory).
	 * 
	 * @param position
	 *            will become the head of the datum's trajectory.
	 */
	@JavaHandler
	public void add(SpaceTimePosition position);

	/**
	 * Marshals (serializes) the datum into JSON form.
	 * 
	 * @returns the JSON marshaled datum.
	 * @throws JSONException
	 */
	@JavaHandler
	public JSONObject marshal() throws JSONException;

	/**
	 * Unmarshals (deserializes) a JSON object into this datum.
	 * 
	 * @param json
	 *            a marshaled datum.
	 * @param spaceTimePositionFactory
	 *            the space-time position factory interface.
	 * @throws JSONException
	 */
	@JavaHandler
	public void unmarshal(JSONObject json,
			ISpaceTimePositionFactory spaceTimePositionFactory)
			throws JSONException;

	abstract class Impl implements JavaHandlerContext<Vertex>, Datum {

		@Override
		@JavaHandler
		public void add(SpaceTimePosition position) {
			// configure the new space-time position
			position.setDatum(this);

			// place the new space-time position into the datum's trajectory
			SpaceTimePosition trajectoryHead = getTrajectoryHead();
			position.setPrevious(trajectoryHead);
			if (trajectoryHead != null)
				trajectoryHead.setNext(position);

			// update the datum's trajectory head
			setTrajectoryHead(position);
		}

		@JavaHandler
		@Override
		public JSONObject marshal() throws JSONException {
			JSONObject json = new JSONObject();

			// marshal the datum's properties
			for (String key : asVertex().getPropertyKeys()) {
				if (key.equals("location"))
					continue; // location is a special case handled below

				json.put(key, asVertex().getProperty(key));
			}

			// marshal the location property
			json.put("latitude", getLocation().getPoint().getLatitude());
			json.put("longitude", getLocation().getPoint().getLongitude());

			// marshal the datum's trajectory
			SpaceTimePosition pos = getTrajectoryHead();
			while (pos != null) {
				// marshal each space-time position
				json.accumulate("trajectory", pos.marshal());
				pos = pos.getPrevious();
			}

			return json;
		}

		@JavaHandler
		@Override
		public void unmarshal(JSONObject json,
				ISpaceTimePositionFactory spaceTimePositionFactory)
				throws JSONException {
			// unmarshal the datum's properties
			Iterator<String> keys = json.keys();
			String key;
			while (keys.hasNext()) {
				key = keys.next();
				if (key.equals("trajectory") || key.equals("latitude")
						|| key.equals("longitude") || key.equals("_id")
						|| key.equals("_type"))
					continue;

				asVertex().setProperty(key, json.get(key));
			}

			// unmarshal the datum's location
			setLocation(Geoshape.point(json.getDouble("latitude"),
					json.getDouble("longitude")));

			// unmarshal the datum's trajectory, adding them to the datum in
			// reverse order
			JSONArray trajectory = json.getJSONArray("trajectory");
			JSONObject posJson;
			SpaceTimePosition pos;
			for (int i = trajectory.length(); i > 0; i--) {
				posJson = trajectory.getJSONObject(i);
				pos = spaceTimePositionFactory.addSpaceTimePosition(posJson);
				add(pos);
			}
		}
	}

}
