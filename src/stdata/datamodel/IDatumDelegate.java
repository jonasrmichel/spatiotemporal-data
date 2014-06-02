package stdata.datamodel;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.geo.Geoshape;

public interface IDatumDelegate {

	/** The conversion factor to convert kilmeters to meters. */
	public static final int KM_TO_M = 10 ^ 3;

	/**
	 * Appends the provided space-time position to the provided datum's
	 * trajectory (i.e., at the head of the trajectory).
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param position
	 *            will become the head of the datum's trajectory.
	 */
	public void append(Datum datum, SpaceTimePosition position);

	/**
	 * Appends the provided space-time position to the provided measured datum's
	 * trajectory and captures some extra measurable metadata.
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param position
	 *            will become the head of the datum's trajectory.
	 */
	public void appendMeasured(Datum datum, SpaceTimePosition position);

	/**
	 * This is a "faux" addMeasured() method -- it does not add a space-time
	 * position to the datum's trajectory, but instead updates the statistics
	 * that would be affected if a space-time position with the provided
	 * parameters was added.
	 * 
	 * @datum a measured datum.
	 * @param location
	 *            the geographic location of the would-be space-time position.
	 * @param timestamp
	 *            the timestamp of the would-be space-time position.
	 */
	public void appendMeasured(Datum datum, Geoshape location, long timestamp);

	/**
	 * Automatically updates the provided measured datum's size property.
	 * 
	 * @param datum
	 *            a datum.
	 */
	public void updateSize(Datum datum);

	/**
	 * Automatically updates the provided measured datum's length property with
	 * the geographic distance between the two provided space-time positions.
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param fromPos
	 *            the space-time position to begin measuring from.
	 * @param toPos
	 *            the space-time position to measure to.
	 */
	public void updateLength(Datum datum, SpaceTimePosition fromPos,
			SpaceTimePosition toPos);

	/**
	 * Automatically updates the provided measured datum's length property with
	 * the geographic distance between the two provided geographic locations.
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param fromLoc
	 *            the geographic location to begin measuring from.
	 * @param toLoc
	 *            the geographic location to measure to.
	 */
	public void updateLength(Datum datum, Geoshape fromLoc, Geoshape toLoc);

	/**
	 * Automatically updates the provided measured datum's age property.
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param timestamp
	 *            represents the current time.
	 */
	public void updateAge(Datum datum, long timestamp);

	/**
	 * Automatically updates the provided measured datum's
	 * distance-host-creation property (meters).
	 * 
	 * @param datum
	 *            a measured datum.
	 * 
	 * @param hostLoc
	 *            the host device's geographic location.
	 */
	public void updateDistanceHostCreation(Datum datum, Geoshape hostLoc);

	/**
	 * Automatically updates the provided measured datum's
	 * distance-phenomenon-creation property (meters).
	 * 
	 * @param datum
	 *            a measured datum.
	 * 
	 * @param phenomenonLoc
	 *            the datum's phenomenon's geographic location.
	 */
	public void updateDistancePhenomenonCreation(Datum datum,
			Geoshape phenomenonLoc);
}
