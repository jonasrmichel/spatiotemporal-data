package edu.utexas.ece.mpc.stdata.vertices;

import java.util.Iterator;
import java.util.Map;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;

public interface IDatumVertexDelegate {

	/** The conversion factor to convert kilometers to meters. */
	public static final int KM_TO_M = 10 ^ 3;

	/**
	 * Returns an iterator over the provided datum's spatiotemporal trajectory,
	 * ordered in descending temporal order (most to least recent).
	 * 
	 * @param datum
	 *            a datum.
	 * @return an iterator over the datum's trajectory.
	 */
	public Iterator<SpaceTimePositionVertex> getTrajectory(DatumVertex datum);

	/**
	 * Returns a map of datums to their associated spatiotemporal trajectories.
	 * 
	 * @param data
	 *            an iterator over a collection of datums.
	 * @return a map of the provided datums to their associated spatiotemporal
	 *         metadata.
	 */
	public Map<DatumVertex, Iterator<SpaceTimePositionVertex>> getTrajectories(
			Iterator<DatumVertex> data);

	/**
	 * Prepends the provided space-time position to the provided datum's
	 * trajectory (i.e., at the head of the trajectory).
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param position
	 *            will become the head of the datum's trajectory.
	 */
	public void prepend(DatumVertex datum, SpaceTimePositionVertex position);

	/**
	 * Generates and prepends a space-time position with the provided parameters
	 * to the provided datum's trajectory (i.e., at the head of the trajectory).
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param location
	 *            the spatial component of the space-time position.
	 * @param timestamp
	 *            the temporal component of the space-time position.
	 * @param domain
	 *            the logical component of the space-time position.
	 */
	public void prepend(DatumVertex datum, Geoshape location, long timestamp,
			String domain);

	/**
	 * Prepends the provided space-time position to the provided measured
	 * datum's trajectory and captures some extra measurable metadata.
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param position
	 *            will become the head of the datum's trajectory.
	 */
	public void prependMeasured(DatumVertex datum,
			SpaceTimePositionVertex position);

	/**
	 * Generates and prepends a space-time position with the provided parameters
	 * to the provided measurable datum's trajectory and captures some extra
	 * measurable metadata.
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param location
	 *            the spatial component of the space-time position.
	 * @param timestamp
	 *            the temporal component of the space-time position.
	 * @param domain
	 *            the logical component of the space-time position.
	 */
	public void prependMeasured(DatumVertex datum, Geoshape location,
			long timestamp, String domain);

	/**
	 * This is a "pseudo" prependMeasured() method -- it does not add a
	 * space-time position to the datum's trajectory, but instead updates the
	 * statistics that would be affected if a space-time position with the
	 * provided parameters was prepended.
	 * 
	 * @datum a measured datum.
	 * @param location
	 *            the geographic location of the would-be space-time position.
	 * @param timestamp
	 *            the timestamp of the would-be space-time position.
	 */
	public void prependMeasuredPseudo(DatumVertex datum, Geoshape location,
			long timestamp);

	/**
	 * Appends the provided space-time position to the provided datum's
	 * trajectory (i.e., at the tail of the trajectory).
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param position
	 *            will become the tail of the datum's trajectory.
	 */
	public void append(DatumVertex datum, SpaceTimePositionVertex position);

	/**
	 * Generates and appends a space-time position with the provided parameters
	 * to the provided datum's trajectory (i.e., at the tail of the trajectory).
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param location
	 *            the spatial component of the space-time position.
	 * @param timestamp
	 *            the temporal component of the space-time position.
	 * @param domain
	 *            the logical component of the space-time position.
	 */
	public void append(DatumVertex datum, Geoshape location, long timestamp,
			String domain);

	/**
	 * Appends the provided space-time position to the provided measured datum's
	 * trajectory and captures some extra measurable metadata.
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param position
	 *            will become the tail of the datum's trajectory.
	 */
	public void appendMeasured(DatumVertex datum,
			SpaceTimePositionVertex position);

	/**
	 * Generates and appends a space-time position with the provided parameters
	 * to the provided measurable datum's trajectory and captures some extra
	 * measurable metadata.
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param location
	 *            the spatial component of the space-time position.
	 * @param timestamp
	 *            the temporal component of the space-time position.
	 * @param domain
	 *            the logical component of the space-time position.
	 */
	public void appendMeasured(DatumVertex datum, Geoshape location,
			long timestamp, String domain);

	/**
	 * Automatically updates the provided measured datum's size property.
	 * 
	 * @param datum
	 *            a datum.
	 */
	public void updateSize(DatumVertex datum);

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
	public void updateLength(DatumVertex datum,
			SpaceTimePositionVertex fromPos, SpaceTimePositionVertex toPos);

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
	public void updateLength(DatumVertex datum, Geoshape fromLoc, Geoshape toLoc);

	/**
	 * Automatically updates the provided measured datum's age property.
	 * 
	 * @param datum
	 *            a measured datum.
	 * @param timestamp
	 *            represents the current time.
	 */
	public void updateAge(DatumVertex datum, long timestamp);

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
	public void updateDistanceHostCreation(DatumVertex datum, Geoshape hostLoc);

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
	public void updateDistancePhenomenonCreation(DatumVertex datum,
			Geoshape phenomenonLoc);

	/**
	 * Deletes the provided datum and its associated spatiotemporal metadata.
	 * 
	 * @param datum
	 *            the datum to delete.
	 */
	public void delete(DatumVertex datum);

	/**
	 * Deletes each of the datums in the provided iterator and their associated
	 * spatiotemporal metadata.
	 * 
	 * @param data
	 *            an iterator over a collection of datums to delete.
	 */
	public void delete(Iterator<DatumVertex> data);
}
