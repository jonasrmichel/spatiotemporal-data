package stdata.titan;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.INDEX_NAMESPACE;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_NAMESPACE;

import java.io.File;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import stdata.datamodel.SpatiotemporalDatabase;
import stdata.datamodel.SpatiotemporalFrameInitializer;
import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Vertex;

public class TitanSpatiotemporalDatabase<V extends Datum> extends
		SpatiotemporalDatabase<TitanGraph, V> {

	public TitanSpatiotemporalDatabase(String instance, String graphDir,
			String indexDir) {
		super(instance, graphDir, indexDir);
		// TODO Auto-generated constructor stub
	}

	/* SpatiotemporalDatabase abstract method implementations. */

	@Override
	protected void initializeBaseGraph() {
		String storageDirectory = graphDir + File.separator + instance;
		BaseConfiguration config = new BaseConfiguration();
		config.subset(STORAGE_NAMESPACE)
				.addProperty(STORAGE_BACKEND_KEY,
						"com.thinkaurelius.titan.diskstorage.berkeleyje.BerkeleyJEStoreManager");
		config.subset(STORAGE_NAMESPACE).addProperty(STORAGE_DIRECTORY_KEY,
				storageDirectory);

		// add index
		Configuration sub = config.subset(STORAGE_NAMESPACE)
				.subset(INDEX_NAMESPACE).subset(INDEX);
		sub.setProperty(INDEX_BACKEND_KEY,
				"com.thinkaurelius.titan.diskstorage.lucene.LuceneIndex");
		sub.setProperty(STORAGE_DIRECTORY_KEY,
				StorageConfig.getHomeDir(indexDir));

		// create the base graph
		baseGraph = TitanFactory.open(config);

		// configure indexing
		try {
			// vertex frame class index
			baseGraph.makeKey(SpatiotemporalFrameInitializer.FRAMED_CLASS_KEY)
					.dataType(String.class).indexed(INDEX, Vertex.class).make();

			// geographic location index
			baseGraph.makeKey(SpaceTimePosition.LOCATION_KEY)
					.dataType(Geoshape.class).indexed(INDEX, Vertex.class)
					.make();

		} catch (IllegalArgumentException e) {
			// key(s) already defined
			System.out.println("(One or more keys already defined.)");
		}

	}
}
