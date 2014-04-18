package stdata.datamodel;

import stdata.datamodel.vertices.Datum;
import stdata.datamodel.vertices.SpaceTimePosition;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FrameInitializer;
import com.tinkerpop.frames.FramedGraph;


public class SpatiotemporalFrameInitializer implements FrameInitializer {
	/** The framed class type property key. */
	public static final String FRAMED_CLASS_KEY = "class";

	@Override
	public void initElement(Class<?> kind, FramedGraph<?> framedGraph,
			Element element) {
		// save the class name of the frame on the element
		element.setProperty(FRAMED_CLASS_KEY, kind.getName());
		
		if (kind.equals(Datum.class)) {
			// TODO: Datum initializers

		} else if (kind.equals(SpaceTimePosition.class)) {
			// TODO: SpaceTimePoint initializers
			
		}

	}

}
