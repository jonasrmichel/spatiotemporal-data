package stdata;

import java.util.Iterator;
import java.util.Map;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;

import com.tinkerpop.blueprints.Graph;

public interface INetworkProvider {
	
	/**
	 * Called to send a datum and its associated spatiotemporal metadata over a
	 * network connection.
	 * 
	 * @param datum
	 *            the datum to send.
	 * @param trajectory
	 *            the datum's spatiotemporal trajectory.
	 */
	public void send(Datum datum, Iterator<SpaceTimePosition> trajectory);

	/**
	 * Called to send multiple datums and their associated spatiotemporal
	 * metadata over a network connection.
	 * 
	 * @param data
	 *            a map of datums to their spatiotemporal trajectories to send.
	 */
	public void send(Map<Datum, Iterator<SpaceTimePosition>> data);

	/**
	 * Called to send a graph over a network connection.
	 * 
	 * @param graph
	 *            the graph to send.
	 */
	public void send(Graph graph);
}
