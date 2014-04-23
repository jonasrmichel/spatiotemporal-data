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

	/*
	 * Special simulation-specific properties and methods. We can't just extend
	 * the Datum interface because overridden methods cannot be seen!
	 */

	public static enum TriggerType {
		SPATIAL, TEMPORAL
	};

	/**
	 * A flag that enables indicates of all of the below "measurable" properties
	 * and methods should be used.
	 */
	@Property("is-measurable")
	public boolean getIsMeasurable();

	@Property("is-measurable")
	public void setIsMeasurable(boolean measurable);

	/** The sensed phenomenon's simulation identifier. */
	@Property("phenomenon-identifier")
	public int getPhenomenonIdentifier();

	@Property("phenomenon-identifier")
	public void setPhenomenonIdentifier(int identifier);

	/**
	 * Trigger type property indicates how this datum's trajectory updates are
	 * triggered.
	 */
	@Property("trigger-type")
	public TriggerType getTriggerType();

	@Property("trigger-type")
	public void setTriggerType(TriggerType triggerType);

	/**
	 * Creation time property is the time the datum was created.
	 */
	@Property("creation-time")
	public long getCreationTime();

	@Property("creation-time")
	public void setCreationTime(long timestamp);

	/**
	 * Creation location property is the geospatial position where the datum was
	 * created.
	 */
	@Property("creation-location")
	public Geoshape getCreationLocation();

	@Property("creation-location")
	public void setCreationLocation(Geoshape location);

	/**
	 * Size property measures the datum trajectory's size (# space-time
	 * positions).
	 */
	@Property("trajectory-size")
	public int getSize();

	@Property("trajectory-size")
	public void setSize(int size);

	/**
	 * Automatically updates the measured datum's size property.
	 */
	@JavaHandler
	public void updateSize();

	/**
	 * Length property measures the datum trajectory's length in meters (the sum
	 * of the distance between each space-time position).
	 */
	@Property("trajectory-length")
	public double getLength();

	@Property("trajectory-length")
	public void setLength(double length);

	/**
	 * Automatically the measured datum's length property.
	 * 
	 * @param pos1
	 * @param pos2
	 */
	@JavaHandler
	public void updateLength(SpaceTimePosition pos1, SpaceTimePosition pos2);

	/** Age property measures the datum's age. */
	@Property("age")
	public long getAge();

	@Property("age")
	public void setAge(long age);

	/**
	 * Automatically updates the measured datum's age property.
	 * 
	 * @param timestamp
	 *            represents the current time.
	 */
	@JavaHandler
	public void updateAge(long timestamp);

	/**
	 * Distance-host-creation property is the current distance of the host
	 * carrying this datum from the initial creation location (meters).
	 */
	@Property("distance-host-creation")
	public double getDistanceHostCreation();

	@Property("distance-host-creation")
	public void setDistanceHostCreation(double distance);

	/**
	 * Automatically updates the measured datum's distance-host-creation
	 * property (meters).
	 */
	@JavaHandler
	public void updateDistanceHostCreation(Geoshape hostLocation);

	/**
	 * Distance-phenomenon-creation property is the current distance of the
	 * phenomenon this datum represents from the datum's initial creation
	 * location (meters).
	 */
	@Property("distance-phenomenon-creation")
	public double getDistancePhenomenonCreation();

	@Property("distance-phenomenon-creation")
	public void setDistancePhenomenonCreation(double distance);

	/**
	 * Automatically updates the measured datum's distance-phenomenon-creation
	 * property (meters).
	 */
	@JavaHandler
	public void updateDistancePhenomenonCreation(Geoshape phenomenonLocation);

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
	 * Adds a new space-time position to the datum's trajectory and captures
	 * some extra measurable metadata.
	 * 
	 * @param position
	 */
	@JavaHandler
	public void addMeasured(SpaceTimePosition position);

	abstract class Impl implements JavaHandlerContext<Vertex>, Datum {

		@Override
		@JavaHandler
		public void add(SpaceTimePosition position) {
			if (getIsMeasurable()) {
				addMeasured(position);
				return;
			}

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

		@Override
		@JavaHandler
		public void addMeasured(SpaceTimePosition position) {
			// configure the new space-time position
			position.setDatum(this);

			// place the new space-time position into the datum's trajectory
			SpaceTimePosition trajectoryHead = getTrajectoryHead();
			position.setPrevious(trajectoryHead);
			if (trajectoryHead != null) {
				trajectoryHead.setNext(position);

				// update measurements
				updateSize();
				updateLength(position, trajectoryHead);
				// age, distance-host-creation, and distance-phenomenon-creation
				// are updated by the host carrying the datum each time step

			} else { // the trajectory is empty
				// initialize measurements
				setCreationTime(position.getTimestamp());
				setCreationLocation(position.getLocation());
				setSize(0);
				setLength(0);
				setAge(0);
				setDistanceHostCreation(0);
				setDistancePhenomenonCreation(0);
			}

			// update the datum's trajectory head
			setTrajectoryHead(position);
		}

		@Override
		@JavaHandler
		public void updateSize() {
			setSize(getSize() + 1);
		}

		@Override
		@JavaHandler
		public void updateLength(SpaceTimePosition pos1, SpaceTimePosition pos2) {
			// Geoshape.Point.distance() is in kilometers, convert to meters
			setLength(getLength()
					+ pos1.getLocation().getPoint()
							.distance(pos1.getLocation().getPoint()) * 1000);
		}

		@Override
		@JavaHandler
		public void updateAge(long timestamp) {
			setAge(timestamp - getCreationTime());
		}

		@Override
		@JavaHandler
		public void updateDistanceHostCreation(Geoshape hostLocation) {
			// Geoshape.Point.distance() is in kilometers, convert to meters
			setDistanceHostCreation(hostLocation.getPoint().distance(
					getCreationLocation().getPoint()) * 1000);
		}

		@Override
		@JavaHandler
		public void updateDistancePhenomenonCreation(Geoshape phenomenonLocation) {
			// Geoshape.Point.distance() is in kilometers, convert to meters
			setDistancePhenomenonCreation(phenomenonLocation.getPoint()
					.distance(getLocation().getPoint()) * 1000);
		}
	}

}
