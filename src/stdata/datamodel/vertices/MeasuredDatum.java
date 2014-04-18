package stdata.datamodel.vertices;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;

/**
 * A MeasuredDatum keeps track of some statistics about a Datum's trajectory.
 * For general purpose use, in practice these statistics would be attached to
 * trajectory edges and/or dynamically computed. For simplicity and speed,
 * they've been attached as extra Datum metadata here.
 * 
 */
public interface MeasuredDatum extends Datum {
	public static enum TriggerType {
		SPATIAL, TEMPORAL
	};

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

	abstract class Impl implements JavaHandlerContext<Vertex>, MeasuredDatum {
		// We need to re-implement this here because we can't reference the
		// super interface (Datum).
		@Override
		@JavaHandler
		public void add(SpaceTimePosition position) {
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
					.distance(getInitialPhenomenonLocation().getPoint()) * 1000);
		}
	}
}
