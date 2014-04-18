package stdata.simulator.movingobjects;

import stdata.simulator.ILocationManager;
import stdata.simulator.IMovingObjectDatabase;

public class Phenomenon extends MovingObject {

	public Phenomenon(int identifier, String type,
			ILocationManager locationManager, IMovingObjectDatabase database) {
		super(identifier, type, locationManager, database);
	}

	/* MovingObject abstract method implementations. */

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void step(int time) {
		// TODO Auto-generated method stub

	}

}
