package stdata.titan;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_NAMESPACE;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_NAMESPACE;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import stdata.datamodel.SpatiotemporalFrameInitializer;
import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanGraphQuery;
import com.thinkaurelius.titan.core.attribute.Geo;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphConfiguration;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.AbstractModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;

public class TitanTest {
	public static final String INDEX = "test-index";

	public static void main(String[] args) {
		BaseConfiguration config = new BaseConfiguration();
		config.subset(STORAGE_NAMESPACE)
				.addProperty(STORAGE_BACKEND_KEY,
						"com.thinkaurelius.titan.diskstorage.berkeleyje.BerkeleyJEStoreManager");
		config.subset(STORAGE_NAMESPACE).addProperty(STORAGE_DIRECTORY_KEY,
				StorageConfig.getHomeDir());

		// add index
		Configuration sub = config.subset(STORAGE_NAMESPACE)
				.subset(INDEX_NAMESPACE).subset(INDEX);
		sub.setProperty(INDEX_BACKEND_KEY,
				"com.thinkaurelius.titan.diskstorage.lucene.LuceneIndex");
		sub.setProperty(STORAGE_DIRECTORY_KEY,
				StorageConfig.getHomeDir("lucene"));

		// create the graph
		TitanGraph titanGraph = TitanFactory.open(config);

		// configure indexing
		try {
			titanGraph.makeKey("location").dataType(Geoshape.class)
					.indexed(INDEX, Vertex.class).make();
		} catch (IllegalArgumentException e) {
			// key already defined
			System.out.println("(Key already defined.)");
		}

		// wrap the titan graph for framing
		FramedGraphFactory factory = new FramedGraphFactory(
				new JavaHandlerModule(), new AbstractModule() {
					public void doConfigure(FramedGraphConfiguration config) {
						config.addFrameInitializer(new SpatiotemporalFrameInitializer());
					}
				});
		FramedGraph<TitanGraph> framedGraph = factory.create(titanGraph);

		// populate the framed graph
		Datum datum = framedGraph.addVertex(null, Datum.class);
		datum.asVertex().setProperty("key-1", "value-1");

		SpaceTimePosition position = framedGraph.addVertex(null,
				SpaceTimePosition.class);
		position.setLocation(Geoshape.point(-40, 90));
		position.setTimestamp(System.currentTimeMillis());

		datum.add(position);

		titanGraph.commit();

		// read graph
		System.out.println("titanGraph vertices:");
		for (Vertex v : titanGraph.getVertices())
			System.out.println("\t" + v + ", " + v.getPropertyKeys());

		System.out.println("framedGraph vertices:");
		for (Vertex v : framedGraph.getVertices())
			System.out.println("\t" + v + ", " + v.getPropertyKeys());

		System.out.println("Datum vertices:");
		for (Vertex v : titanGraph.query().has("class", Datum.class.getName())
				.vertices()) {
			Datum d = framedGraph.getVertex(v, Datum.class);
			System.out.println("\t" + d + ", " + d.toString());
		}

		TitanGraphQuery q = titanGraph.query().has("location", Geo.WITHIN,
				Geoshape.circle(-40.1, 90.1, 100));
		System.out.println("Geo.WITHIN vertices:");
		for (Vertex v : q.vertices())
			System.out.println("\t" + v + ", " + v.getPropertyKeys());

		titanGraph.shutdown();

		System.out.println("Done.");
	}
}
