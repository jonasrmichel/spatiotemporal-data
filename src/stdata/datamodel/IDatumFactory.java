package stdata.datamodel;

import java.util.List;

import stdata.datamodel.vertices.Datum;
import stdata.rules.Rule;

public interface IDatumFactory {

	public Datum addDatum(double latitude, double longitude, long timestamp,
			String domain, List<Datum> context, Rule rule);
}
