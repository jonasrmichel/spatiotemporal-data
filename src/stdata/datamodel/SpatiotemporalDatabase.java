package stdata.datamodel;

import java.util.Map;

import stdata.datamodel.edges.EdgeFrameFactory;
import stdata.datamodel.vertices.SpatiotemporalContext;
import stdata.datamodel.vertices.VertexFrameFactory;
import stdata.geo.Geoshape;
import stdata.rules.IRuleRegistry;
import stdata.rules.RuleRegistry;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

public abstract class SpatiotemporalDatabase<G extends TransactionalGraph & KeyIndexableGraph> {
	/** The framed class type property key. */
	public static final String FRAMED_CLASS_KEY = "class";

	/** The database's instance identifier. (Permits multiptle local instances.) */
	protected String instance;

	/** The database's graph home directory. */
	protected String graphDir;

	/** The delegate on which to make callbacks. */
	protected ISpatiotemporalDatabaseDelegate delegate;

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
	public ISpaceTimePositionFactory stpFactory;

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
			ISpatiotemporalDatabaseDelegate delegate) {
		this.instance = instance;
		this.graphDir = graphDir;
		this.delegate = delegate;

		// initialize the base graph implementation, wrappers, and indices
		initializeBaseGraph();
		initializeEventGraph();
		initializeFramedGraph();
		initializeFramedElementIndex();

		ruleRegistry = new RuleRegistry<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, eventGraph, framedGraph);
		stpFactory = new SpaceTimePositionFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>, VertexFrame>(
				baseGraph, framedGraph);
		datumFactory = new DatumFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, framedGraph, stpFactory, ruleRegistry);

		stContextFactory = new SpatiotemporalContextFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, framedGraph);

		vertexFrameFactories.put(ISpaceTimePositionFactory.class.getName(),
				(VertexFrameFactory) stpFactory);
		vertexFrameFactories.put(IDatumFactory.class.getName(),
				(VertexFrameFactory) datumFactory);

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
				delegate.getLocation(), delegate.getTimestamp());
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
	 * Exposes the base graph's addVertex() method.
	 * 
	 * @param id
	 *            the recommended object identifier.
	 * @return the newly created vertex.
	 */
	public Vertex addVertex(Object id) {
		Vertex v = baseGraph.addVertex(id);

		// commit changes
		// commit();

		return v;
	}

	/**
	 * Exposes the base graph's addEdge() method.
	 * 
	 * @param id
	 *            the recommended object identifier.
	 * @param outVertex
	 *            the vertex on the tail of the edge.
	 * @param inVertex
	 *            the vertex on the head of the edge.
	 * @param label
	 *            the label associated with the edge.
	 * @return the newly created edge.
	 */
	public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex,
			String label) {
		Edge e = baseGraph.addEdge(id, outVertex, inVertex, label);

		// commit changes
		// commit();

		return e;
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
}
