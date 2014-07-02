package stdata.datamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import stdata.IContextProvider;
import stdata.INetworkProvider;
import stdata.datamodel.edges.EdgeFrameFactory;
import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.datamodel.vertices.SpatiotemporalContext;
import stdata.datamodel.vertices.VertexFrameFactory;
import stdata.geo.Geoshape;
import stdata.rules.IRuleRegistry;
import stdata.rules.RuleRegistry;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

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
	public G baseGraph;

	/** Event graph wrapper. */
	public EventGraph<G> eventGraph;

	/** Framed graph wrapper. */
	public FramedGraph<EventGraph<G>> framedGraph;

	/** EdgeFrame factories. */
	public Map<String, EdgeFrameFactory> edgeFrameFactories;

	/** VertexFrame factories. */
	public Map<String, VertexFrameFactory> vertexFrameFactories;

	/** The spatiotemporal rule registry interface. */
	public IRuleRegistry<G, EventGraph<G>, FramedGraph<EventGraph<G>>> ruleRegistry;

	/** The space-time position factory interface. */
	public ISpaceTimePositionFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>> stpFactory;

	/** The datum factory interface. */
	public IDatumFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>> datumFactory;

	/**
	 * A limited-scope factory to generate special vertices that represent the
	 * host's spatiotemporal context.
	 */
	protected SpatiotemporalContextFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>> stContextFactory;

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
		ruleRegistry = new RuleRegistry<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, eventGraph, framedGraph, edgeFrameFactories,
				vertexFrameFactories, contextProvider, networkProvider);
		
		// initialize element factories
		stpFactory = new SpaceTimePositionFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, framedGraph);
		datumFactory = new DatumFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, framedGraph, stpFactory, ruleRegistry);
		stContextFactory = new SpatiotemporalContextFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, framedGraph);

		// initialize element frame factory maps
		edgeFrameFactories = new HashMap<String, EdgeFrameFactory>();
		vertexFrameFactories = new HashMap<String, VertexFrameFactory>();
		
		vertexFrameFactories.put(ISpaceTimePositionFactory.class.getName(),
				(VertexFrameFactory) stpFactory);
		vertexFrameFactories.put(IDatumFactory.class.getName(),
				(VertexFrameFactory) datumFactory);

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
		// baseGraph.createKeyIndex(VertexFrameFactory.FRAMED_CLASS_KEY,
		// Vertex.class);
		// baseGraph.createKeyIndex(EdgeFrameFactory.FRAMED_CLASS_KEY,
		// Edge.class);
		baseGraph.createKeyIndex(FRAMED_CLASS_KEY, Element.class);
	}

	/**
	 * Initializes the graph's special spatiotemporal context vertex.
	 */
	private void initializeSpatiotemporalContext() {
		stContext = stContextFactory.addSpatiotemporalContext(
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
	 * Returns an iterator over all the framed vertices in the graph database
	 * with the provided frame class.
	 * 
	 * @param kind
	 *            a VertexFrame class.
	 * @return an iterator over the vertices framed as the provided class.
	 */
	public <F extends VertexFrame> Iterable<F> getFramedVertices(Class<F> kind) {
		Iterable<Vertex> vertices = baseGraph.getVertices(FRAMED_CLASS_KEY,
				kind.getName());
		Iterable<F> framedVertices = framedGraph.frameVertices(vertices, kind);

		return framedVertices;
	}

	/**
	 * Copies the provided graph into the spatiotemporal graph database,
	 * updating each datum type vertex's spatiotemporal metadata.
	 * 
	 * @param graph
	 *            the graph to copy from
	 */
	public void add(Graph graph) {
		// a cache for datum type vertices
		HashSet<Datum> data = new HashSet<Datum>();

		// add vertices
		for (Vertex fromVertex : graph.getVertices()) {
			Vertex toVertex = baseGraph.addVertex(fromVertex.getId());

			// check if vertex is datum type
			if (toVertex.getPropertyKeys().contains(FRAMED_CLASS_KEY)
					&& toVertex.getProperty(FRAMED_CLASS_KEY).equals(
							Datum.class.getName())) {
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
		SpaceTimePosition position = stpFactory.addSpaceTimePosition(
				contextProvider.getLocation(), contextProvider.getTimestamp(),
				contextProvider.getDomain());
		for (Datum datum : data) {
			datumFactory.append(datum, position);
		}
	}
}
