package stdata.test;

import java.util.HashMap;
import java.util.LinkedHashMap;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;
import stdata.geo.Geoshape;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.IndexableGraph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.wrappers.event.EventGraph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class Playground {

	public static void main(String[] args) {
		// get a reference to the base graph
		Graph graph = new TinkerGraph();

		// wrap the base graph for partitioning
		// PartitionGraph<Graph> partitionGraph = new
		// PartitionGraph<Graph>(graph, "_partition", null);

		// indexing
		Index<Vertex> index = ((IndexableGraph) graph).createIndex(
				"test-index", Vertex.class);
		
		KeyIndexableGraph kig;
		GremlinPipeline gp;
		
		// wrap the base graph for event listening
		EventGraph<Graph> eventGraph = new EventGraph<Graph>(graph);
		// eventGraph.addListener(new SpatiotemporalGraphChangedListener());

		// wrap the event graph for framing
		FramedGraph<EventGraph<Graph>> framedGraph = new FramedGraph<EventGraph<Graph>>(eventGraph);

		// set write partition
		// partitionGraph.setWritePartition("data");

		// create a new datum
		Datum datum = framedGraph.addVertex(null, Datum.class);
		datum.asVertex().setProperty("key-1", "value-1");

		// update the index
		index.put("class", Datum.class.getName(), datum.asVertex());

		// set write partition
		// partitionGraph.setWritePartition("trajectories");

		// initialize the datum's trajectory
		SpaceTimePosition position = framedGraph.addVertex(null,
				SpaceTimePosition.class);
		// position.setLatitude(-40);
		// position.setLongitude(90);
		position.setLocation(Geoshape.point(-40, 90));
		position.setTimestamp(System.currentTimeMillis());

		datum.add(position);

		// update the index
		index.put("class", SpaceTimePosition.class.getName(),
				position.asVertex());

		// read explicit partitions
		// partitionGraph.addReadPartition("data");
		// System.out.println("data partition:");
		// for (Vertex v : partitionGraph.getVertices())
		// System.out.println("\t" + v.getPropertyKeys());
		// System.out.println();
		// partitionGraph.removeReadPartition("data");
		// partitionGraph.addReadPartition("trajectories");
		// System.out.println("trajectories partition:");
		// for (Vertex v : partitionGraph.getVertices())
		// System.out.println("\t" + v.getPropertyKeys());
		// partitionGraph.removeReadPartition("trajectories");

		// read index, O(log(n))
		System.out.println("index reading, O(log(n))...");
		System.out.println("indexed data vertices: ");
		for (Vertex v : index.get("class", Datum.class.getName()))
			System.out.println("\t" + v + " " + v.getPropertyKeys());
		System.out.println();
		System.out.println("indexed trajectory vertices: ");
		for (Vertex v : index.get("class", SpaceTimePosition.class.getName()))
			System.out.println("\t" + v + " " + v.getPropertyKeys());

		// read implicit (logical) partitions, O(n)
		System.out.println();
		System.out.println("graph scanning, O(n)...");
		System.out.println("data vertices:");
		for (Vertex v : framedGraph.getVertices("class", Datum.class.getName()))
			// requires linear scan of all vertices
			System.out.println("\t" + v + " " + v.getPropertyKeys());
		System.out.println();
		System.out.println("trajectories verticies:");
		for (Vertex v : framedGraph.getVertices("class",
				SpaceTimePosition.class.getName()))
			// requires linear scan of all vertices
			System.out.println("\t" + v + " " + v.getPropertyKeys());

		// element inspection
		System.out.println();
		System.out.println("Element inspection...");
		System.out.println("datum: " + datum);

		System.out.println("datum's trajectory head:"
				+ datum.getTrajectoryHead());

		System.out.println("datum's 'trajectory' edges:");
		for (Edge e : datum.asVertex().getEdges(Direction.BOTH, "trajectory"))
			System.out.println("\t" + e + " (OUT= "
					+ e.getVertex(Direction.OUT) + ", IN= "
					+ e.getVertex(Direction.IN) + ")");

		System.out.println("datum's entire trajectory:");
		for (SpaceTimePosition p : datum.getTrajectory())
			System.out.println("\t" + p);

		System.out.println();

		System.out.println("position: " + position);

		System.out.println("position's datum: " + position.getDatum());

		System.out.println("position's 'trajectory' edges:");
		for (Edge e : position.asVertex()
				.getEdges(Direction.BOTH, "trajectory"))
			System.out.println("\t" + e);

		System.out.println("position's 'trajectory-head' edges:");
		for (Edge e : position.asVertex().getEdges(Direction.BOTH,
				"trajectory-head"))
			System.out.println("\t" + e);
		
		HashMap<String, Integer> mmap = new LinkedHashMap<String, Integer>();
		mmap.put("hey", 1);
		mmap.put("you", 2);
		mmap.put("guys", 3);
		
		for (String k : mmap.keySet())
			System.out.println(k);
		for (Integer i : mmap.values())
			System.out.println(i);
	}
	
}
