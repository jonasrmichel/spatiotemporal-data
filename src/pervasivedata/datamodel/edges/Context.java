package pervasivedata.datamodel.edges;

import pervasivedata.datamodel.vertices.Datum;

import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.InVertex;
import com.tinkerpop.frames.OutVertex;
import com.tinkerpop.frames.Property;

public interface Context extends EdgeFrame {

	@Property("relation")
	public String getRelation();

	@Property("relation")
	public void setRelation(String relation);

	@InVertex
	public Datum getDatumTo();

	@OutVertex
	public Datum getDatumFrom();

}
