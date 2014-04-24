package stdata.datamodel;

import stdata.rules.IRuleRegistry;
import stdata.rules.RuleRegistry;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphConfiguration;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.AbstractModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;

public abstract class SpatiotemporalDatabase<G extends TransactionalGraph> {
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

	/** The spatiotemporal rule registry interface. */
	public IRuleRegistry<G, EventGraph<G>, FramedGraph<EventGraph<G>>> ruleRegistry;

	/** The datum factory interface. */
	public IDatumFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>> datumFactory;

	public SpatiotemporalDatabase(String instance, String graphDir) {
		this.instance = instance;
		this.graphDir = graphDir;

		// initialize the base graph implementation and wrappers
		initializeBaseGraph();
		initializeEventGraph();
		initializeFramedGraph();

		ruleRegistry = new RuleRegistry<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, eventGraph, framedGraph);
		datumFactory = new DatumFactory<G, EventGraph<G>, FramedGraph<EventGraph<G>>>(
				baseGraph, framedGraph, ruleRegistry);
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
		FramedGraphFactory factory = new FramedGraphFactory(
				new JavaHandlerModule(), new AbstractModule() {
					public void doConfigure(FramedGraphConfiguration config) {
						config.addFrameInitializer(new SpatiotemporalFrameInitializer());
					}
				});
		framedGraph = factory.create(eventGraph);
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
//		baseGraph.commit();

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
//		baseGraph.commit();

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
//		baseGraph.commit();

		return e;
	}

	/**
	 * Returns an iterator over all the framed vertices in the graph database
	 * with the provided frame class.
	 * 
	 * @param kind
	 *            a VertexFrame class.
	 * @return
	 */
	public <F extends VertexFrame> Iterable<F> getFramedVertices(Class<F> kind) {
		Iterable<Vertex> vertices = baseGraph
				.query()
				.has(SpatiotemporalFrameInitializer.FRAMED_CLASS_KEY,
						kind.getName()).vertices();
		Iterable<F> framedVertices = framedGraph.frameVertices(vertices, kind);

		return framedVertices;
	}
}
