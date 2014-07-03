package stdata.geo;

import stdata.datamodel.vertices.GeoVertex;

import com.tinkerpop.gremlin.java.GremlinPipeline;

public class GeoGremlinPipeline<S, E> extends GremlinPipeline<S, E> {

	public GeoGremlinPipeline() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GeoGremlinPipeline(Object starts, boolean doQueryOptimization) {
		super(starts, doQueryOptimization);
		// TODO Auto-generated constructor stub
	}

	public GeoGremlinPipeline(Object starts) {
		super(starts);
		// TODO Auto-generated constructor stub
	}

	// Coordinate-based queries / predicates:
	// range x {segments} -> {segments}
	// 	contain
	// 	cover
	// 	covered by
	// 	cross
	// 	disjoint
	// 	intersect
	// 	intersect window
	// 	overlap
	// 	touch
	// 	within
	// 	within distance
	
	public GremlinPipeline<S, ? extends GeoVertex> hasCoordinateRelation(final GeoFilterPipe.Filter filter, final Object value) {
		return this.add(new CoordinateFilterPipe((GeoVertex) value, filter));
	}
	
	// Trajectory-based queries / predicates:
	// Topological:
	// range x {segments} -> {segments}
	// 	enter
	// 	leave
	// 	cross
	// 	bypass
	// Navigational:
	// {segments} -> int
	// {segments} -> real
	// {segments} -> bool
	//	traveled distance
	//	covered area (top, average)
	//	speed
	//	heading
	
}
