package stdata.datamodel.vertices;

import stdata.datamodel.edges.Context;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
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

	}

}
