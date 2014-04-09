package pervasivedata.datamodel;

import pervasivedata.datamodel.vertices.Datum;
import pervasivedata.datamodel.vertices.SpaceTimePosition;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.frames.FrameInitializer;
import com.tinkerpop.frames.FramedGraph;


public class MyFrameInitializer implements FrameInitializer {

	@Override
	public void initElement(Class<?> kind, FramedGraph<?> framedGraph,
			Element element) {
		// save the class name of the frame on the element
		element.setProperty("class", kind.getName());
		
		if (kind.equals(Datum.class)) {
			// TODO: Datum initializers

		} else if (kind.equals(SpaceTimePosition.class)) {
			// TODO: SpaceTimePoint initializers
			
		}

	}

}
