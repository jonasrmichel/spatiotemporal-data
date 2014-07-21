package edu.utexas.ece.mpc.stdata.util;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.ElementHelper;
import com.tinkerpop.blueprints.util.GraphHelper;

public class SpatiotemporalGraphHelper extends GraphHelper {

	/**
	 * Create an in-memory graph comprising the provided vertices and edges.
	 * 
	 * @param vertices
	 *            the subgraph's vertices.
	 * @param edges
	 *            the subgraph's edges.
	 * @return an in-memory graph comprising the vertices and edges.
	 */
	public static Graph subgraph(final Iterable<Vertex> vertices,
			final Iterable<Edge> edges) {
		Graph subgraph = new TinkerGraph();
		for (final Vertex fromVertex : vertices) {
			final Vertex toVertex = subgraph.addVertex(fromVertex.getId());
			ElementHelper.copyProperties(fromVertex, toVertex);
		}
		for (final Edge fromEdge : edges) {
			final Vertex outVertex = subgraph.getVertex(fromEdge.getVertex(
					Direction.OUT).getId());
			final Vertex inVertex = subgraph.getVertex(fromEdge.getVertex(
					Direction.IN).getId());
			final Edge toEdge = subgraph.addEdge(fromEdge.getId(), outVertex,
					inVertex, fromEdge.getLabel());
			ElementHelper.copyProperties(fromEdge, toEdge);
		}

		return subgraph;
	}
}
