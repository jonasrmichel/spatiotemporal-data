package stdata.geo;

import java.util.NoSuchElementException;

import stdata.datamodel.vertices.GeoVertex;

import com.tinkerpop.pipes.AbstractPipe;

public class CoordinateFilterPipe extends AbstractPipe<GeoVertex, GeoVertex> implements GeoFilterPipe<GeoVertex> {
	
	private final GeoVertex geoVertex;
	private final GeoFilterPipe.Filter filter;

	public CoordinateFilterPipe(final GeoVertex geoVertex, final GeoFilterPipe.Filter filter) {
		this.geoVertex = geoVertex;
		this.filter = filter;
	}

	@Override
	protected GeoVertex processNextStart() throws NoSuchElementException {
		while (true) {
			final GeoVertex geoVertex = this.starts.next();
			if (GeoPipeHelper.compareGeoVertices(this.filter, geoVertex, this.geoVertex))
				return geoVertex;
		}
	}

}
