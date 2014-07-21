package edu.utexas.ece.mpc.stdata.geo;

import com.tinkerpop.pipes.Pipe;

public interface GeoFilterPipe<S> extends Pipe<S, S> {
	enum Filter {
		CONTAINS, DISJOINT, INTERSECTS, WITHIN
	}
	// Don't have these: COVERS, COVERED_BY, CROSSES, OVERLAPS, TOUCHES
}
