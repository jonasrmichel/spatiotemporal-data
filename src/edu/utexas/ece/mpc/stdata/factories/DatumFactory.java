package edu.utexas.ece.mpc.stdata.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.rules.Rule;
import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;
import edu.utexas.ece.mpc.stdata.vertices.IDatumVertexDelegate;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePositionVertex;

public class DatumFactory<D extends DatumVertex> extends VertexFrameFactory<D>
		implements IDatumFactory<D>, IDatumVertexDelegate {
	protected IContextProvider contextProvider;
	protected ISpaceTimePositionFactory stpFactory;

	public DatumFactory(Class<D> type) {
		super(type);
	}

	public DatumFactory(Class<D> type, TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry,
			IContextProvider contextProvider,
			ISpaceTimePositionFactory stpFactory) {
		super(type, baseGraph, framedGraph, ruleRegistry);

		this.contextProvider = contextProvider;
		this.stpFactory = stpFactory;
	}

	public void initialize(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry,
			IContextProvider contextProvider,
			ISpaceTimePositionFactory stpFactory) {
		initialize(baseGraph, framedGraph, ruleRegistry);

		this.contextProvider = contextProvider;
		this.stpFactory = stpFactory;
	}

	private D addDatum(boolean measurable, Geoshape phenomenonLoc,
			List<D> context, Rule... rules) {
		// create the datum
		D datum = addVertex(null, rules);
		datum.setDelegate(this);
		datum.setIsMeasurable(measurable);

		// initialize the datum's spatiotemporal trajectory
		if (!measurable) {
			prepend(datum, contextProvider.getLocation(),
					contextProvider.getTimestamp(), contextProvider.getDomain());

		} else {
			prependMeasuredPseudo(datum, contextProvider.getLocation(),
					contextProvider.getTimestamp());

		}

		datum.setLocation(phenomenonLoc);

		if (context != null)
			datum.setContextData((Iterable<DatumVertex>) context);

		// // register the datum's rule
		// ruleRegistry.registerRule(rule, datum);

		// commit changes
		// baseGraph.commit();

		return datum;
	}

	/* IDatumFactory interface implementation. */

	@Override
	public D addDatum(Geoshape phenomenonLoc, List<D> context, Rule... rules) {
		return addDatum(false, phenomenonLoc, context, rules);
	}

	@Override
	public D addMeasurableDatum(Geoshape phenomenonLoc, List<D> context,
			Rule... rules) {
		return addDatum(true, phenomenonLoc, context, rules);
	}

	/* IDatumVertexDelegate interface implementation. */

	@Override
	public Iterator<SpaceTimePositionVertex> getTrajectory(DatumVertex datum) {
		if (datum == null)
			return null;

		ArrayList<SpaceTimePositionVertex> trajectory = new ArrayList<SpaceTimePositionVertex>();
		SpaceTimePositionVertex position = datum.getTrajectoryHead();
		while (position != null) {
			trajectory.add(position);
			position = position.getPrevious();
		}

		return trajectory.iterator();
	}

	@Override
	public Map<DatumVertex, Iterator<SpaceTimePositionVertex>> getTrajectories(
			Iterator<DatumVertex> data) {
		if (data == null)
			return null;

		Map<DatumVertex, Iterator<SpaceTimePositionVertex>> trajectories = new HashMap<DatumVertex, Iterator<SpaceTimePositionVertex>>();
		DatumVertex datum;
		while (data.hasNext()) {
			datum = data.next();
			trajectories.put(datum, getTrajectory(datum));
		}

		return trajectories;
	}

	@Override
	public void prepend(DatumVertex datum, SpaceTimePositionVertex position) {
		if (datum == null)
			return;

		if (datum.getIsMeasurable()) {
			prependMeasured(datum, position);
			return;
		}

		// configure the new space-time position
		position.setDatum(datum);

		// place the new space-time position into the datum's trajectory
		SpaceTimePositionVertex trajectoryHead = datum.getTrajectoryHead();
		position.setPrevious(trajectoryHead);
		if (trajectoryHead != null) {
			trajectoryHead.setNext(position);

		} else { // the trajectory is empty
			// tail == head
			datum.setTrajectoryTail(position);
		}

		// update the datum's trajectory head
		datum.setTrajectoryHead(position);
	}

	@Override
	public void prepend(DatumVertex datum, Geoshape location, long timestamp,
			String domain) {
		if (datum == null)
			return;

		SpaceTimePositionVertex position = stpFactory.addSpaceTimePosition(
				location, timestamp, domain);
		prepend(datum, position);
	}

	@Override
	public void prependMeasured(DatumVertex datum,
			SpaceTimePositionVertex position) {
		if (datum == null)
			return;

		// configure the new space-time position
		position.setDatum(datum);

		// place the new space-time position into the datum's trajectory
		SpaceTimePositionVertex trajectoryHead = datum.getTrajectoryHead();
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

			// tail == head
			datum.setTrajectoryTail(position);
		}

		// update the datum's trajectory head
		datum.setTrajectoryHead(position);
	}

	@Override
	public void prependMeasured(DatumVertex datum, Geoshape location,
			long timestamp, String domain) {
		if (datum == null)
			return;

		SpaceTimePositionVertex position = stpFactory.addSpaceTimePosition(
				location, timestamp, domain);
		prependMeasured(datum, position);
	}

	@Override
	public void prependMeasuredPseudo(DatumVertex datum, Geoshape location,
			long timestamp) {
		if (datum == null)
			return;

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
	public void append(DatumVertex datum, SpaceTimePositionVertex position) {
		if (datum == null)
			return;

		if (datum.getIsMeasurable()) {
			appendMeasured(datum, position);
			return;
		}

		// configure the new space-time position
		position.setDatum(datum);

		// place the new space-time position into the datum's trajectory
		SpaceTimePositionVertex trajectoryTail = datum.getTrajectoryTail();
		position.setNext(trajectoryTail);
		if (trajectoryTail != null) {
			trajectoryTail.setPrevious(position);

		} else { // the trajectory is empty
			// head == tail
			datum.setTrajectoryHead(position);
		}

		// update the datum's trajectory tail
		datum.setTrajectoryTail(position);
	}

	@Override
	public void append(DatumVertex datum, Geoshape location, long timestamp,
			String domain) {
		if (datum == null)
			return;

		SpaceTimePositionVertex position = stpFactory.addSpaceTimePosition(
				location, timestamp, domain);
		append(datum, position);
	}

	@Override
	public void appendMeasured(DatumVertex datum,
			SpaceTimePositionVertex position) {
		if (datum == null)
			return;

		// configure the new space-time position
		position.setDatum(datum);

		// place the new space-time position into the datum's trajectory
		SpaceTimePositionVertex trajectoryTail = datum.getTrajectoryTail();
		position.setNext(trajectoryTail);
		if (trajectoryTail != null) {
			trajectoryTail.setPrevious(position);

			// update measurements
			updateSize(datum);
			updateLength(datum, position, trajectoryTail);
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

			// head == tail
			datum.setTrajectoryHead(position);
		}

		// update the datum's trajectory tail
		datum.setTrajectoryTail(position);
	}

	@Override
	public void appendMeasured(DatumVertex datum, Geoshape location,
			long timestamp, String domain) {
		if (datum == null)
			return;

		SpaceTimePositionVertex position = stpFactory.addSpaceTimePosition(
				location, timestamp, domain);
		appendMeasured(datum, position);
	}

	@Override
	public void updateSize(DatumVertex datum) {
		if (datum == null)
			return;

		datum.setSize(datum.getSize() + 1);
	}

	@Override
	public void updateLength(DatumVertex datum,
			SpaceTimePositionVertex fromPos, SpaceTimePositionVertex toPos) {
		if (datum == null)
			return;

		double distance = fromPos.getLocation().getPoint()
				.distance(toPos.getLocation().getPoint())
				* KM_TO_M;

		datum.setLength(datum.getLength() + distance);
	}

	@Override
	public void updateLength(DatumVertex datum, Geoshape fromLoc, Geoshape toLoc) {
		if (datum == null)
			return;

		double distance = fromLoc.getPoint().distance(toLoc.getPoint())
				* KM_TO_M;

		datum.setLength(datum.getLength() + distance);
	}

	@Override
	public void updateAge(DatumVertex datum, long timestamp) {
		if (datum == null)
			return;

		datum.setAge(timestamp - datum.getCreationTime());
	}

	@Override
	public void updateDistanceHostCreation(DatumVertex datum, Geoshape hostLoc) {
		if (datum == null)
			return;

		double distance = hostLoc.getPoint().distance(
				datum.getCreationLocation().getPoint())
				* KM_TO_M;

		datum.setDistanceHostCreation(distance);
	}

	@Override
	public void updateDistancePhenomenonCreation(DatumVertex datum,
			Geoshape phenomenonLoc) {
		if (datum == null)
			return;

		double distance = phenomenonLoc.getPoint().distance(
				datum.getLocation().getPoint())
				* KM_TO_M;

		datum.setDistancePhenomenonCreation(distance);
	}

	@Override
	public void delete(DatumVertex datum) {
		if (datum == null)
			return;

		// delete the datum's spatiotemporal metadata
		for (SpaceTimePositionVertex position : datum.getTrajectory()) {
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
	public void delete(Iterator<DatumVertex> data) {
		if (data == null)
			return;

		DatumVertex datum;
		while (data.hasNext()) {
			datum = data.next();
			delete(datum);
		}
	}
}
