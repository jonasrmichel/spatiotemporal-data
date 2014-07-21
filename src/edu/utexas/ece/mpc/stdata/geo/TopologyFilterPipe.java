package edu.utexas.ece.mpc.stdata.geo;

import com.tinkerpop.pipes.Pipe;

public interface TopologyFilterPipe<S> extends Pipe<S, S> {
	enum Filter {
		ENTERS, LEAVES, CROSSES, BYPASSES
	}
}
