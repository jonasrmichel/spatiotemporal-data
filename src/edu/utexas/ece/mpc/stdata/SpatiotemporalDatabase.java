package edu.utexas.ece.mpc.stdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

import edu.utexas.ece.mpc.stdata.factories.DatumFactory;
import edu.utexas.ece.mpc.stdata.factories.EdgeFrameFactory;
import edu.utexas.ece.mpc.stdata.factories.IDatumFactory;
import edu.utexas.ece.mpc.stdata.factories.ISpaceTimePositionFactory;
import edu.utexas.ece.mpc.stdata.factories.ISpatiotemporalContextFactory;
import edu.utexas.ece.mpc.stdata.factories.SpaceTimePositionFactory;
import edu.utexas.ece.mpc.stdata.factories.SpatiotemporalContextFactory;
import edu.utexas.ece.mpc.stdata.factories.VertexFrameFactory;
import edu.utexas.ece.mpc.stdata.geo.Geoshape;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;
import edu.utexas.ece.mpc.stdata.rules.RuleRegistry;
import edu.utexas.ece.mpc.stdata.vertices.Datum;
import edu.utexas.ece.mpc.stdata.vertices.SpaceTimePosition;
import edu.utexas.ece.mpc.stdata.vertices.SpatiotemporalContext;

public abstract class SpatiotemporalDatabase<G extends TransactionalGraph & KeyIndexableGraph> {
	/** The framed class type property key. */
	public static final String FRAMED_CLASS_KEY = "class";

	/** The database's instance identifier. (Permits multiple local instances.) */
	protected String instance;

	/** The database's graph home directory. */
	protected String graphDir;

	/** The context provider interface. */
	protected IContextProvider contextProvider;

	/** The network provider interface. */
	protected INetworkProvider networkProvider;

	/** Base graph database. */
	protected G baseGraph;

	/** Event graph wrapper. */
	protected EventGraph<G> eventGraph;

	/** Framed graph wrapper. */
	protected FramedGraph<EventGraph<G>> framedGraph;

	/** EdgeFrame factories. */
	protected Map<Class, EdgeFrameFactory> edgeFrameFactories;

	/** VertexFrame factories. */
	protected Map<Class, VertexFrameFactory> vertexFrameFactories;

	/** The spatiotemporal rule registry interface. */
	protected IRuleRegistry ruleRegistry;

	/**
	 * A special vertex that provides the graph database with access to the
	 * host's geospatial location and notion of time.
	 */
	protected SpatiotemporalContext stContext;

	public SpatiotemporalDatabase(String instance, String graphDir,
			IContextProvider contextProvider, INetworkProvider networkProvider) {
		this.instance = instance;
		this.graphDir = graphDir;
		this.contextProvider = contextProvider;
		this.networkProvider = networkProvider;

		// initialize the base graph implementation, wrappers, and indices
		initializeBaseGraph();
		initializeEventGraph();
		initializeFramedGraph();
		initializeFramedElementIndex();

		// initialize rule registry
		ruleRegistry = new RuleRegistry(baseGraph, eventGraph, framedGraph,
				edgeFrameFactories, vertexFrameFactories, contextProvider,
				networkProvider);

		// initialize the built-in framed element factories
		SpaceTimePositionFactory stpFactory = new SpaceTimePositionFactory(
				baseGraph, framedGraph, ruleRegistry);
		addVertexFrameFactory(SpaceTimePosition.class,
				(VertexFrameFactory) stpFactory);

		DatumFactory<Datum> rawDatumFactory = new DatumFactory<Datum>(
				Datum.class, baseGraph, framedGraph, ruleRegistry,
				contextProvider, stpFactory);
		addVertexFrameFactory(Datum.class, (VertexFrameFactory) rawDatumFactory);

		SpatiotemporalContextFactory stContextFactory = new SpatiotemporalContextFactory(
				baseGraph, framedGraph, ruleRegistry);
		addVertexFrameFactory(SpatiotemporalContext.class,
				(VertexFrameFactory) stContextFactory);

		// initialize the local notion of space-time context
		initializeSpatiotemporalContext();
	}

	/**
	 * Initializes the base graph database implementation.
	 */
	protected abstract void initializeBaseGraph();

	/**
	 * Wraps the base graph for event listening.
	 */
	private void initializeEventGraph() {
		eventGraph = new EventGraph<G>(baseGraph);
	}

	/**
	 * Wraps the event graph for object framing.
	 */
	private void initializeFramedGraph() {
		framedGraph = new FramedGraph<EventGraph<G>>(eventGraph);
	}

	/**
	 * Initializes the framed element index.
	 */
	private void initializeFramedElementIndex() {
		// only vertex indexing is supported
		baseGraph.createKeyIndex(FRAMED_CLASS_KEY, Vertex.class);
	}

	/**
	 * Initializes the graph's special spatiotemporal context vertex.
	 */
	private void initializeSpatiotemporalContext() {
		stContext = getSpatiotemporalContextFactory().addSpatiotemporalContext(
				contextProvider.getLocation(), contextProvider.getTimestamp());
	}

	/**
	 * Sets the spatial component of the graph's special spatiotemporal context
	 * vertex.
	 * 
	 * @param location
	 *            a geospatial location.
	 */
	public void setSpatialContext(Geoshape location) {
		stContext.setLocation(location);
	}

	/**
	 * Sets the temporal component of the graph's special spatiotemporal context
	 * vertex.
	 * 
	 * @param timestamp
	 *            a timestamp.
	 */
	public void setTemporalContext(long timestamp) {
		stContext.setTimestamp(timestamp);
	}

	/**
	 * Returns an iterator over all the framed vertices in the graph database
	 * with the provided frame class.
	 * 
	 * @param kind
	 *            a VertexFrame class.
	 * @return an iterator over the vertices framed as the provided class.
	 */
	public <F extends VertexFrame> Iterable<F> getFramedVertices(Class<F> kind) {
		Iterable<Vertex> vertices = baseGraph.getVertices(FRAMED_CLASS_KEY,
				kind);

		Iterable<F> framedVertices = framedGraph.frameVertices(vertices, kind);

		return framedVertices;
	}

	/**
	 * Returns the factory associated with the provided edge frame type.
	 * 
	 * @param type
	 *            an edge frame type.
	 * @return the edge frame factory associated with the provided type.
	 */
	public <T extends EdgeFrame> EdgeFrameFactory<T> getEdgeFrameFactory(
			Class<T> type) {
		return edgeFrameFactories.get(type);
	}

	/**
	 * Registers a edge vertex factory with the database.
	 * 
	 * @param type
	 *            the {@link EdgeFrame} class the factory produces.
	 * @param vertexFactory
	 *            the {@link EdgeFrameFactory} to register.
	 */
	public <T extends EdgeFrame> void addEdgeFrameFactory(Class<T> type,
			EdgeFrameFactory edgeFactory) {
		if (edgeFrameFactories == null)
			edgeFrameFactories = new HashMap<Class, EdgeFrameFactory>();

		if (!edgeFactory.initialized())
			edgeFactory.initialize(baseGraph, framedGraph, ruleRegistry);

		edgeFrameFactories.put(type, edgeFactory);
	}

	/**
	 * Returns the factory associated with the provided vertex frame type.
	 * 
	 * @param type
	 *            an edge frame type.
	 * @return the edge frame factory associated with the provided type.
	 */
	public <T extends VertexFrame> VertexFrameFactory<T> getVertexFrameFactory(
			Class<T> type) {
		return vertexFrameFactories.get(type);
	}

	/**
	 * Registers a frame vertex factory with the database.
	 * 
	 * @param type
	 *            the {@link VertexFrame} class the factory produces.
	 * @param vertexFactory
	 *            the {@link VertexFrameFactory} to register.
	 */
	public <T extends VertexFrame> void addVertexFrameFactory(Class<T> type,
			VertexFrameFactory vertexFactory) {
		if (vertexFrameFactories == null)
			vertexFrameFactories = new HashMap<Class, VertexFrameFactory>();

		if (!vertexFactory.initialized())
			vertexFactory.initialize(baseGraph, framedGraph, ruleRegistry);

		vertexFrameFactories.put(type, vertexFactory);
	}

	/**
	 * Returns the rule registry interface.
	 * 
	 * @return the database's rule registry interface.
	 */
	public IRuleRegistry getRuleRegistry() {
		return ruleRegistry;
	}

	/**
	 * Returns the space-time position factory interface.
	 * 
	 * @return the database's space-time position factory interface.
	 */
	public ISpaceTimePositionFactory getSpaceTimePositionFactory() {
		return (SpaceTimePositionFactory) getVertexFrameFactory(SpaceTimePosition.class);
	}

	/**
	 * Returns the raw datum factory interface.
	 * 
	 * @return the database's datum factory interface.
	 */
	public IDatumFactory<Datum> getRawDatumFactory() {
		return (DatumFactory<Datum>) getVertexFrameFactory(Datum.class);
	}

	/**
	 * Returns the spatiotemporal context factory interface.
	 * 
	 * @return the database's spatiotemporal factory interface.
	 */
	private ISpatiotemporalContextFactory getSpatiotemporalContextFactory() {
		return (ISpatiotemporalContextFactory) getVertexFrameFactory(SpatiotemporalContext.class);
	}

	/**
	 * Commits any pending changes on the graph.
	 */
	public void commit() {
		baseGraph.stopTransaction(Conclusion.SUCCESS);
	}

	/**
	 * Shuts down the database.
	 */
	public void shutdown() {
		framedGraph.shutdown();
		eventGraph.shutdown();
		baseGraph.shutdown();
	}

	/**
	 * Copies the provided graph into the spatiotemporal graph database,
	 * updating each datum type vertex's spatiotemporal metadata.
	 * 
	 * @param graph
	 *            the graph to copy from
	 */
	public <D extends Datum> void add(Graph graph) {
		// a cache for datum type vertices
		HashSet<Datum> data = new HashSet<Datum>();

		// add vertices
		for (Vertex fromVertex : graph.getVertices()) {
			Vertex toVertex = baseGraph.addVertex(fromVertex.getId());

			// check if the vertex is datum type or derivative
			if (toVertex.getPropertyKeys().contains(FRAMED_CLASS_KEY)
					&& Datum.class.isAssignableFrom((Class<D>) toVertex
							.getProperty(FRAMED_CLASS_KEY))) {
				data.add(framedGraph.frame(toVertex, Datum.class));
			}

			ElementHelper.copyProperties(fromVertex, toVertex);
		}

		// add edges
		for (Edge fromEdge : graph.getEdges()) {
			Vertex outVertex = baseGraph.getVertex(fromEdge.getVertex(
					Direction.OUT).getId());
			Vertex inVertex = baseGraph.getVertex(fromEdge.getVertex(
					Direction.IN).getId());
			Edge toEdge = baseGraph.addEdge(fromEdge.getId(), outVertex,
					inVertex, fromEdge.getLabel());
			ElementHelper.copyProperties(fromEdge, toEdge);
		}

		// update datum's spatiotemporal metadata
		SpaceTimePosition position = getSpaceTimePositionFactory()
				.addSpaceTimePosition(contextProvider.getLocation(),
						contextProvider.getTimestamp(),
						contextProvider.getDomain());
		for (Datum datum : data) {
			getRawDatumFactory().append(datum, position);
		}
	}
}
