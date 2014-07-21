package edu.utexas.ece.mpc.stdata.titan;

import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_DIRECTORY_KEY;
import static com.thinkaurelius.titan.graphdb.configuration.GraphDatabaseConfiguration.STORAGE_NAMESPACE;

import java.io.File;

import org.apache.commons.configuration.BaseConfiguration;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

import edu.utexas.ece.mpc.stdata.IContextProvider;
import edu.utexas.ece.mpc.stdata.INetworkProvider;
import edu.utexas.ece.mpc.stdata.datamodel.SpatiotemporalDatabase;

public class TitanSpatiotemporalDatabase extends
		SpatiotemporalDatabase<TitanGraph> {

	public TitanSpatiotemporalDatabase(String instance, String graphDir,
			IContextProvider contextProvider, INetworkProvider networkProvider) {
		super(instance, graphDir, contextProvider, networkProvider);
		// TODO Auto-generated constructor stub
	}

	/* SpatiotemporalDatabase abstract method implementations. */

	@Override
	protected void initializeBaseGraph() {
		String homeDir = graphDir + File.separator + instance;
		File folder = new File(homeDir);
		if (!folder.exists())
			folder.mkdir();
		
		BaseConfiguration config = new BaseConfiguration();
//		config.subset(STORAGE_NAMESPACE)
//				.addProperty(STORAGE_BACKEND_KEY,
//						"com.thinkaurelius.titan.diskstorage.berkeleyje.BerkeleyJEStoreManager");
		config.subset(STORAGE_NAMESPACE).addProperty(STORAGE_DIRECTORY_KEY,
				homeDir);

		// create the base graph
		baseGraph = TitanFactory.open(config);
	}
}
