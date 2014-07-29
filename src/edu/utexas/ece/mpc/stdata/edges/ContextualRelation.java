package edu.utexas.ece.mpc.stdata.edges;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Range;

import edu.utexas.ece.mpc.stdata.vertices.DatumVertex;

public interface ContextualRelation extends EdgeFrame {
	@Property("relation")
	public String getRelation();

	@Property("relation")
	public void setRelation(String relation);

	@Domain
	public DatumVertex getDatumTo();

	@Range
	public DatumVertex getDatumFrom();

}
