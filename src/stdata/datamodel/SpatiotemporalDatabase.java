package stdata.datamodel;

import stdata.datamodel.vertices.Datum;
import stdata.rules.IRuleRegistry;
import stdata.rules.RuleRegistry;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphConfiguration;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.modules.AbstractModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;

public abstract class SpatiotemporalDatabase<G extends Graph, D extends Datum> {

	/** Internal index name. */
	public static final String INDEX = "spatiotemporal-index";

	/**
	 * The database's instance identifier. (Permits multiptle local instances.)
	 */
	protected String instance;

	/** The database's graph home directory. */
	protected String graphDir;

	/** The database's graph index directory. */
	protected String indexDir;

	/** The database's datum class type. */
	protected Class<D> datumClass;

	/** Base graph database. */
	protected G baseGraph;

	/** Event graph wrapper. */
	public EventGraph<G> eventGraph;

	/** Framed graph wrapper. */
	public FramedGraph<G> framedGraph;

	/** The spatiotemporal rule registry interface. */
	public IRuleRegistry ruleRegistry;

	/** The space-time position factory interface. */
	public ISpaceTimePositionFactory spaceTimePositionFactory;

	/** The datum factory interface. */
	public IDatumFactory<D> datumFactory;

	public SpatiotemporalDatabase(String instance, String graphDir,
			String indexDir, Class<D> datumClass) {
		this.instance = instance;
		this.graphDir = graphDir;
		this.indexDir = indexDir;
		this.datumClass = datumClass;

		// initialize the base graph implementation and wrappers
		initializeBaseGraph();
		initializeEventGraph();
		initializeFramedGraph();

		ruleRegistry = new RuleRegistry<G>(eventGraph, framedGraph);
		spaceTimePositionFactory = new SpaceTimePositionFactory<G>(framedGraph);
		datumFactory = new DatumFactory<G, D>(framedGraph, ruleRegistry,
				spaceTimePositionFactory, datumClass);
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
	 * Wraps the base graph for object framing.
	 */
	private void initializeFramedGraph() {
		FramedGraphFactory factory = new FramedGraphFactory(
				new JavaHandlerModule(), new AbstractModule() {
					public void doConfigure(FramedGraphConfiguration config) {
						config.addFrameInitializer(new SpatiotemporalFrameInitializer());
					}
				});
		framedGraph = factory.create(baseGraph);
	}

	/**
	 * Shuts down the database.
	 */
	public void shutdown() {
		eventGraph.shutdown();
		framedGraph.shutdown();
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
		return baseGraph.addVertex(id);
	}

	/**
	 * Exposes the framed graph's addVertex method.
	 * 
	 * @param id
	 *            the id of the newly created vertex.
	 * @param kind
	 *            the default annotated interface to frame the vertex as.
	 * @return a proxy object backed by the vertex and interpreted from the
	 *         perspective of the annotate interface.
	 */
	public <F> F addVertex(final Object id, final Class<F> kind) {
		return framedGraph.addVertex(id, kind);
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
		return baseGraph.addEdge(id, outVertex, inVertex, label);
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
		return (Iterable<F>) baseGraph
				.query()
				.has(SpatiotemporalFrameInitializer.FRAMED_CLASS_KEY,
						kind.getName()).vertices();
	}

}
