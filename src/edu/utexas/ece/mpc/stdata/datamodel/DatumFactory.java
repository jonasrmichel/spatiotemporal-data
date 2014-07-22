package edu.utexas.ece.mpc.stdata.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.datamodel.vertices.Datum;
import edu.utexas.ece.mpc.stdata.datamodel.vertices.SpaceTimePosition;
import edu.utexas.ece.mpc.stdata.datamodel.vertices.VertexFrameFactory;
import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.rules.Rule;

public class DatumFactory<G extends TransactionalGraph, E extends EventGraph<G>, F extends FramedGraph<EventGraph<G>>>
		extends VertexFrameFactory<G, E, F, Datum> implements
		IDatumFactory<G, E, F>, IDatumDelegate {
	private ISpaceTimePositionFactory stpFactory;
	private IRuleRegistry<G, E, F> ruleRegistry;

	public DatumFactory(G baseGraph, F framedGraph,
			ISpaceTimePositionFactory stpFactory,
			IRuleRegistry<G, E, F> ruleRegistry) {
		super(baseGraph, framedGraph);

		this.stpFactory = stpFactory;
		this.ruleRegistry = ruleRegistry;
	}

	/* IDatumFactory interface implementation. */

	@Override
	public Datum addDatum(Geoshape phenomenonLoc, Geoshape hostLoc,
			long timestamp, String domain, List<Datum> context,
			boolean measurable, Rule<G, E, F> rule) {
		// create the datum
		Datum datum = addVertex(null, Datum.class);
		datum.setDelegate(this);
		datum.setIsMeasurable(measurable);

		// initialize the datum's spatiotemporal trajectory
		if (!measurable) {
			SpaceTimePosition pos = stpFactory.addSpaceTimePosition(hostLoc,
					timestamp, domain);

			append(datum, pos);

		} else {
			appendMeasured(datum, hostLoc, timestamp);

		}

		datum.setLocation(phenomenonLoc);

		if (context != null)
			datum.setContextData((Iterable<Datum>) context);

		// commit changes
		// baseGraph.commit();

		// register the datum's rule
		ruleRegistry.registerRule(rule, datum);

		return datum;
	}

	/* IDatumDelegate interface implementation. */

	@Override
	public Iterator<SpaceTimePosition> getTrajectory(Datum datum) {
		ArrayList<SpaceTimePosition> trajectory = new ArrayList<SpaceTimePosition>();
		SpaceTimePosition position = datum.getTrajectoryHead();
		while (position != null) {
			trajectory.add(position);
			position = position.getPrevious();
		}

		return trajectory.iterator();
	}

	@Override
	public Map<Datum, Iterator<SpaceTimePosition>> getTrajectories(
			Iterator<Datum> data) {
		Map<Datum, Iterator<SpaceTimePosition>> trajectories = new HashMap<Datum, Iterator<SpaceTimePosition>>();
		Datum datum;
		while (data.hasNext()) {
			datum = data.next();
			trajectories.put(datum, getTrajectory(datum));
		}

		return trajectories;
	}

	@Override
	public void append(Datum datum, SpaceTimePosition position) {
		if (datum.getIsMeasurable()) {
			appendMeasured(datum, position);
			return;
		}

		// configure the new space-time position
		position.setDatum(datum);

		// place the new space-time position into the datum's trajectory
		SpaceTimePosition trajectoryHead = datum.getTrajectoryHead();
		position.setPrevious(trajectoryHead);
		if (trajectoryHead != null)
			trajectoryHead.setNext(position);

		// update the datum's trajectory head
		datum.setTrajectoryHead(position);
	}

	@Override
	public void appendMeasured(Datum datum, SpaceTimePosition position) {
		// configure the new space-time position
		position.setDatum(datum);

		// place the new space-time position into the datum's trajectory
		SpaceTimePosition trajectoryHead = datum.getTrajectoryHead();
		position.setPrevious(trajectoryHead);
		if (trajectoryHead != null) {
			trajectoryHead.setNext(position);

			// update measurements
			updateSize(datum);
			updateLength(datum, position, trajectoryHead);
			// age, distance-host-creation, and distance-phenomenon-creation
			// are updated by the host carrying the datum each time step

		} else { // the trajectory is empty
			// initialize measurements
			datum.setCreationTime(position.getTimestamp());
			datum.setCreationLocation(position.getLocation());
			datum.setSize(0);
			datum.setLength(0);
			datum.setAge(0);
			datum.setDistanceHostCreation(0);
			datum.setDistancePhenomenonCreation(0);
		}

		// update the datum's trajectory head
		datum.setTrajectoryHead(position);
	}

	@Override
	public void appendMeasured(Datum datum, Geoshape location, long timestamp) {
		try {
			updateSize(datum);
			updateLength(datum, location, datum.getPreviousLocation());

		} catch (Exception e) {
			datum.setCreationTime(timestamp);
			datum.setCreationLocation(location);
			datum.setSize(1);
			datum.setLength(0);
			datum.setAge(0);
			datum.setDistanceHostCreation(0);
			datum.setDistancePhenomenonCreation(0);

		} finally {
			datum.setPreviousLocation(location);

		}
	}

	@Override
	public void updateSize(Datum datum) {
		datum.setSize(datum.getSize() + 1);
	}

	@Override
	public void updateLength(Datum datum, SpaceTimePosition fromPos,
			SpaceTimePosition toPos) {
		double distance = fromPos.getLocation().getPoint()
				.distance(toPos.getLocation().getPoint())
				* KM_TO_M;

		datum.setLength(datum.getLength() + distance);
	}

	@Override
	public void updateLength(Datum datum, Geoshape fromLoc, Geoshape toLoc) {
		double distance = fromLoc.getPoint().distance(toLoc.getPoint())
				* KM_TO_M;

		datum.setLength(datum.getLength() + distance);
	}

	@Override
	public void updateAge(Datum datum, long timestamp) {
		datum.setAge(timestamp - datum.getCreationTime());
	}

	@Override
	public void updateDistanceHostCreation(Datum datum, Geoshape hostLoc) {
		double distance = hostLoc.getPoint().distance(
				datum.getCreationLocation().getPoint())
				* KM_TO_M;

		datum.setDistanceHostCreation(distance);
	}

	@Override
	public void updateDistancePhenomenonCreation(Datum datum,
			Geoshape phenomenonLoc) {
		double distance = phenomenonLoc.getPoint().distance(
				datum.getLocation().getPoint())
				* KM_TO_M;

		datum.setDistancePhenomenonCreation(distance);
	}

	@Override
	public void delete(Datum datum) {
		// delete the datum's spatiotemporal metadata
		for (SpaceTimePosition position : datum.getTrajectory()) {
			// delete all edges incident to this space-time position
			for (Edge edge : position.asVertex().getEdges(Direction.BOTH))
				baseGraph.removeEdge(edge);

			// delete this space-time position
			baseGraph.removeVertex(position.asVertex());
		}

		// delete all edges incident to the datum
		for (Edge edge : datum.asVertex().getEdges(Direction.BOTH))
			baseGraph.removeEdge(edge);

		// delete the datum
		baseGraph.removeVertex(datum.asVertex());
	}

	@Override
	public void delete(Iterator<Datum> data) {
		Datum datum;
		while (data.hasNext()) {
			datum = data.next();
			delete(datum);
		}
	}
}
