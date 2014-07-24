package edu.utexas.ece.mpc.stdata;

import java.util.Iterator;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;

import edu.utexas.ece.mpc.stdata.vertices.Datum;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePosition;

public interface INetworkProvider {
	/**
	 * Called to send a datum over a network connection. This method may or may
	 * not attach spatiotemporal metadata by default.
	 * 
	 * @param type
	 *            the datum or derivative type.
	 * @param datum
	 *            the datum to send.
	 */
	public <D extends Datum> void send(Class<D> type, D datum);

	/**
	 * Called to send a datum over a network connection optionally along with
	 * its spatiotemporal metadata.
	 * 
	 * @param type
	 *            the datum or derivative type.
	 * @param datum
	 *            the datum to send.
	 * @param attachTrajectory
	 *            whether or not to send the datum's spatiotemporal trajectory
	 *            with the datum.
	 */
	public <D extends Datum> void send(Class<D> type, D datum,
			boolean attachTrajectory);

	/**
	 * Called to send a datum and its associated spatiotemporal metadata over a
	 * network connection.
	 * 
	 * @param type
	 *            the datum or derivative type.
	 * @param datum
	 *            the datum to send.
	 * @param trajectory
	 *            the datum's spatiotemporal trajectory.
	 */
	public <D extends Datum> void send(Class<D> type, D datum,
			Iterator<SpaceTimePosition> trajectory);

	/**
	 * Called to send multiple datums and their associated spatiotemporal
	 * metadata over a network connection.
	 * 
	 * @param type
	 *            the datum or derivative type.
	 * @param data
	 *            a map of datums to their spatiotemporal trajectories to send.
	 */
	public <D extends Datum> void send(Class<D> type,
			Map<D, Iterator<SpaceTimePosition>> data);

	/**
	 * Called to send a graph over a network connection.
	 * 
	 * @param graph
	 *            the graph to send.
	 */
	public void send(Graph graph);

}
