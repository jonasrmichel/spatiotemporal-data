package stdata.datamodel.edges;

import stdata.datamodel.vertices.Datum;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Range;

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
