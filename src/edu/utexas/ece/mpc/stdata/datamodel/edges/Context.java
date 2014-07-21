package edu.utexas.ece.mpc.stdata.datamodel.edges;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Range;

import edu.utexas.ece.mpc.stdata.datamodel.vertices.Datum;

public interface Context extends EdgeFrame {
	@Property("relation")
	public String getRelation();

	@Property("relation")
	public void setRelation(String relation);

	@Domain
	public Datum getDatumTo();

	@Range
	public Datum getDatumFrom();

}
