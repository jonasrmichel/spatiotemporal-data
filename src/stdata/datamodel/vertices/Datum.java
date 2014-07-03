package stdata.datamodel.vertices;

import stdata.datamodel.IDatumDelegate;
import stdata.datamodel.edges.Context;
import stdata.geo.Geoshape;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.VertexFrame;

public interface Datum extends GeoVertex {
	/** The delegate to make callbacks on. */
	@Property("delegate")
	public IDatumDelegate getDelegate();

	@Property("delegate")
	public void setDelegate(IDatumDelegate delegate);

	/** The head of the datum's trajectory. */
	@Adjacency(label = "trajectory-head")
	public SpaceTimePosition getTrajectoryHead();

	@Adjacency(label = "trajectory-head")
	public void setTrajectoryHead(SpaceTimePosition position);

	/** The unordered datum trajectory. */
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
	 * Previous location property is the geospatial position where the datum was
	 * was most recently.
	 */
	@Property("previous-location")
	public Geoshape getPreviousLocation();

	@Property("previous-location")
	public void setPreviousLocation(Geoshape location);

	/**
	 * Size property measures the datum trajectory's size (# space-time
	 * positions).
	 */
	@Property("trajectory-size")
	public int getSize();

	@Property("trajectory-size")
	public void setSize(int size);

	/**
	 * Length property measures the datum trajectory's length in meters (the sum
	 * of the distance between each space-time position).
	 */
	@Property("trajectory-length")
	public double getLength();

	@Property("trajectory-length")
	public void setLength(double length);

	/** Age property measures the datum's age. */
	@Property("age")
	public long getAge();

	@Property("age")
	public void setAge(long age);

	/**
	 * Distance-host-creation property is the current distance of the host
	 * carrying this datum from the initial creation location (meters).
	 */
	@Property("distance-host-creation")
	public double getDistanceHostCreation();

	@Property("distance-host-creation")
	public void setDistanceHostCreation(double distance);

	/**
	 * Distance-phenomenon-creation property is the current distance of the
	 * phenomenon this datum represents from the datum's initial creation
	 * location (meters).
	 */
	@Property("distance-phenomenon-creation")
	public double getDistancePhenomenonCreation();

	@Property("distance-phenomenon-creation")
	public void setDistancePhenomenonCreation(double distance);

}
