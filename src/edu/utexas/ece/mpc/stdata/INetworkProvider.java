package edu.utexas.ece.mpc.stdata;

import java.util.Iterator;
import java.util.Map;

import com.tinkerpop.blueprints.Graph;

import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePositionVertex;

public interface INetworkProvider {
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
	 * @param rules
	 *            rules associated with the datum.
	 */
	public <D extends DatumVertex> void send(Class<D> type, D datum,
			boolean attachTrajectory, Rule... rules);

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
	 * @param rules
	 *            rules associated with the datum.
	 */
	public <D extends DatumVertex> void send(Class<D> type, D datum,
			Iterator<SpaceTimePositionVertex> trajectory, Rule... rules);

}
