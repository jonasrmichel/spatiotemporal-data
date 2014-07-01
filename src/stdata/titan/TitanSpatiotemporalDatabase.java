package stdata.titan;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_BACKEND_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_NAMESPACE;

import java.io.File;

import org.apache.commons.configuration.BaseConfiguration;

import stdata.ContextProvider;
import stdata.NetworkProvider;
import stdata.datamodel.SpatiotemporalDatabase;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

public class TitanSpatiotemporalDatabase extends
		SpatiotemporalDatabase<TitanGraph> {

	public TitanSpatiotemporalDatabase(String instance, String graphDir,
			ContextProvider contextProvider, NetworkProvider networkProvider) {
		super(instance, graphDir, contextProvider, networkProvider);
		// TODO Auto-generated constructor stub
	}

	/* SpatiotemporalDatabase abstract method implementations. */

	@Override
	protected void initializeBaseGraph() {
		String homeDir = graphDir + File.separator + instance;
		BaseConfiguration config = new BaseConfiguration();
		config.subset(STORAGE_NAMESPACE)
				.addProperty(STORAGE_BACKEND_KEY,
						"com.thinkaurelius.titan.diskstorage.berkeleyje.BerkeleyJEStoreManager");
		config.subset(STORAGE_NAMESPACE).addProperty(STORAGE_DIRECTORY_KEY,
				homeDir);

		// create the base graph
		baseGraph = TitanFactory.open(config);
	}
}
