package pervasivedata.datamodel;

import java.util.List;

import pervasivedata.datamodel.vertices.Datum;
import pervasivedata.rules.Rule;

public interface IDatumFactory {

	public Datum addDatum(double latitude, double longitude, long timestamp,
			String domain, List<Datum> context, List<Rule> rules);
}
