package stdata.datamodel;

import java.util.Map;

import stdata.datamodel.edges.EdgeFrameFactory;
import stdata.datamodel.vertices.VertexFrameFactory;
import stdata.rules.IRuleRegistry;
import stdata.rules.RuleRegistry;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;

public abstract class SpatiotemporalDatabase<G extends TransactionalGraph & KeyIndexableGraph> {
	/**
	 * The database's instance identifier. (Permits multiptle local instances.)
	 */
	protected String instance;

	/** The database's graph home directory. */
	protected String graphDir;

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

	public SpatiotemporalDatabase(String instance, String graphDir) {
		this.instance = instance;
		this.graphDir = graphDir;

		// initialize the base graph implementation, wrappers, and indices
		initializeBaseGraph();
		initializeEventGraph();
		initializeFramedGraph();
		initializeFramedElementIndices();

		ruleRegistry = new RuleRegistry<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, eventGraph, framedGraph);
		stpFactory = new SpaceTimePositionFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>, VertexFrame>(
				baseGraph, framedGraph);
		datumFactory = new DatumFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, framedGraph, stpFactory, ruleRegistry);

		vertexFrameFactories.put(ISpaceTimePositionFactory.class.getName(),
				(VertexFrameFactory) stpFactory);
		vertexFrameFactories.put(IDatumFactory.class.getName(),
				(VertexFrameFactory) datumFactory);
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
	private void initializeFramedElementIndices() {
		baseGraph.createKeyIndex(VertexFrameFactory.FRAMED_CLASS_KEY,
				Vertex.class);
		baseGraph.createKeyIndex(EdgeFrameFactory.FRAMED_CLASS_KEY, Edge.class);
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
	 * Exposes the framed graph's addVertex() method.
	 * 
	 * @param id
	 *            the id of the newly created vertex.
	 * @param kind
	 *            the default annotated interface to frame the vertex as.
	 * @return a proxy object backed by the vertex and interpreted from the
	 *         perspective of the annotate interface.
	 */
	public <F> F addFramedVertex(final Object id, final Class<F> kind) {
		F v = framedGraph.addVertex(id, kind);

		// committ changes
		// baseGraph.commit();

		return v;
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
		// baseGraph.commit();

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
		// baseGraph.commit();

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
		Iterable<Vertex> vertices = baseGraph.getVertices(
				VertexFrameFactory.FRAMED_CLASS_KEY, kind.getName());
		Iterable<F> framedVertices = framedGraph.frameVertices(vertices, kind);

		return framedVertices;
	}
}
