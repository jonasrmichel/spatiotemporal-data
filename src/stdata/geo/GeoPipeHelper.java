package stdata.geo;

import stdata.datamodel.vertices.GeoVertex;

public class GeoPipeHelper {

	private GeoPipeHelper() {
	}

	public static boolean compareGeoVertices(GeoFilterPipe.Filter filter,
			final GeoVertex geoVertexA, final GeoVertex geoVertexB) {
		Geoshape geoshapeA = geoVertexA.getLocation();
		Geoshape geoshapeB = geoVertexB.getLocation();
		
		switch (filter) {
		case CONTAINS:
			return geoshapeA.contain(geoshapeB);
		case DISJOINT:
			return geoshapeA.disjoint(geoshapeB);
		case INTERSECTS:
			return geoshapeA.intersect(geoshapeB);
		case WITHIN:
			return geoshapeA.within(geoshapeB);
		default:
			throw new IllegalArgumentException(
					"Invalid state as no valid filter was provided");
		}
	}

}
