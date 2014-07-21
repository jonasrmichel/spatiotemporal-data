package edu.utexas.ece.mpc.stdata.simulator;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import edu.utexas.ece.mpc.stdata.geo.Geoshape;

public class LocationManager implements ILocationManager {
	/** Holds the global list of mobility traces. */
	private List<List<Point>> traces;

	/** Holds the coordinate projection matrix. */
	private Geoshape[][] projection;

	/**
	 * Defines the min-max latitude-longitude bounds of the bounding box:
	 * -97.7405273914,30.2825938192,-97.7338540554,30.2883008152 generated
	 * using: http://boundingbox.klokantech.com/
	 */
	private static final double LATITUDE_N = 30.2883008152; // northern limit
															// (most positive),
															// y max
	private static final double LATITUDE_S = 30.2825938192; // southern limit
															// (least positive),
															// y min
	private static final double LONGITUDE_W = -97.7405273914; // western limit
																// (most
																// negative), x
																// min
	private static final double LONGITUDE_E = -97.7338540554; // eastern limit
																// (least
																// negative), x
																// max

	/**
	 * Defines the map's height and width in meters. Generated using
	 * http://andrew.hedges.name/experiments/haversine/ with above coordinates
	 * it's important that these are the same as the mobility generator map's
	 * and that one map unit == one meter
	 */
	public static final int MAP_HEIGHT = 634; // ~ 0.4 km2, ~ 0.16 mi2, 100
												// acres
	public static final int MAP_WIDTH = 643;

	/** the mobility trace file */
	// private static final String MOBILITY_TRACE_FILE = "mobility_traces/" +
	// Integer.toString(Settings.NUM_MOBILE_DEVICES) + "_nodes.txt";
	// private static final String MOBILITY_TRACE_FILE =
	// "mobility_traces/500_nodes.txt";

	public LocationManager(String traceFile) {
		this(traceFile, -1, -1);
	}

	public LocationManager(String traceFile, int startTime) {
		this(traceFile, startTime, -1);
	}

	public LocationManager(String traceFile, int startTime, int stopTime) {
		// create the coordinate projection matrix
		projection = new Geoshape[MAP_WIDTH][MAP_HEIGHT];

		// populate the projection matrix
		populateProjectionMatrix();

		// create the global list of traces
		traces = new ArrayList<List<Point>>();

		// load the global list of traces
		loadTraces(traceFile, startTime, stopTime);
	}

	/**
	 * Populates the coordinate projection matrix. This is may be performed once
	 * then used as a lookup table.
	 */
	private void populateProjectionMatrix() {
		double lon, lat;
		for (int x = 0; x < MAP_WIDTH; x++) {
			for (int y = 0; y < MAP_HEIGHT; y++) {
				// project (x, y) into the map's (lon, lat) coordinate system
				lon = getLongitude(x);
				lat = getLatitude(y);
				projection[x][y] = Geoshape.point(lat, lon);
			}
		}
	}

	/**
	 * Loads the global list of mobility traces.
	 */
	private void loadTraces(String traceFile, int startTime, int stopTime) {
		if (SimulationManager.debug)
			Util.report(LocationManager.class, "initializing");
		try {
			FileReader fileReader = new FileReader(traceFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line = null;
			// skip lines until we reach the column name header
			while ((line = bufferedReader.readLine()) != null) {
				if (SimulationManager.debug)
					Util.report(LocationManager.class, "skipping: " + line);

				if (line.trim()
						.replace("\n", "")
						.replace("\r", "")
						.equals("Time	Node	PositionX	PositionY	Speed	Direction Angle"))
					break; // found it!
			}

			// now process all subsequent lines (one trace's point per line)
			String[] cols = null;
			int time, identifier, posx, posy;
			Point point;
			List<Point> trace;
			while ((line = bufferedReader.readLine()) != null) {
				if (SimulationManager.debug)
					Util.report(LocationManager.class, "parsing line: "
							+ line);

				// split the line into its columns
				cols = line.split("\t");
				// retrieve the fields of interest
				time = Integer.parseInt(cols[0]) - 1; // trace time begins at 1
				identifier = Integer.parseInt(cols[1]);
				posx = Integer.parseInt(cols[2]);
				posy = Integer.parseInt(cols[3]);

				// if relevant, check if the current trace time is after the
				// start time
				if (startTime >= 0 && time < startTime)
					continue;

				// if relevant, check if the current trace time is before the
				// stop time
				if (stopTime > 0 && time > stopTime)
					return;

				if (SimulationManager.debug)
					Util.report(LocationManager.class, "parsed: "
							+ "time=" + Integer.toString(time)
							+ " identifier=" + Integer.toString(identifier)
							+ " posx=" + Integer.toString(posx) + " posy="
							+ Integer.toString(posy));

				// retrieve or create this node's trace within the global list
				// of traces
				try {
					trace = traces.get(identifier);
				} catch (IndexOutOfBoundsException e) {
					if (SimulationManager.debug)
						Util.report(
								LocationManager.class,
								"creating new trace for node "
										+ Integer.toString(identifier));

					trace = new ArrayList<Point>();
					traces.add(identifier, trace);
				}

				// create the point
				point = new Point(posx, posy);

				if (SimulationManager.debug)
					Util.report(
							LocationManager.class,
							"appending projected point ("
									+ Integer.toString(posx) + ","
									+ Integer.toString(posy) + ") --> ("
									+ Integer.toString(point.x) + ","
									+ Integer.toString(point.y) + ")");

				// append this point to the end of this objects's trace
				trace.add(point);
			}

			if (SimulationManager.debug)
				Util.report(LocationManager.class,
						"completed initialization");

			// close the file handle
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Performs a linear transformation on an integer point y to calculate the
	 * corresponding latitude within the map's bounds. This was helpful:
	 * http://mathforum.org/library/drmath/view/51833.html
	 * 
	 * @param y
	 * @return
	 */
	public static double getLatitude(int y) {
		return y * (LATITUDE_N - LATITUDE_S) / MAP_HEIGHT + LATITUDE_S;
	}

	/**
	 * Performs a linear transformation on an integer point x to calculate the
	 * corresponding longitude within the map's bounds. This was helpful:
	 * http://mathforum.org/library/drmath/view/51833.html
	 * 
	 * @param x
	 * @return
	 */
	public static double getLongitude(int x) {
		return x * (LONGITUDE_E - LONGITUDE_W) / MAP_WIDTH + LONGITUDE_W;
	}

	/**
	 * Retrieves the Cartesian point in the given mobile object's trace at the
	 * provided simulation time.
	 * 
	 * @param identifier
	 *            the unique identifier of a mobile object.
	 * @param time
	 *            the timestamp of the desired location.
	 * @return
	 */
	public Point getCartesianLocation(int identifier, int time)
			throws NoSuchElementException {
		return traces.get(identifier).get(time);
	}

	/**
	 * Returns the geospatial point for the provided Cartesian coordinates.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Geoshape getProjectedLocation(int x, int y) {
		return projection[x][y];
	}

	/* ILocationManager interface implementation. */

	@Override
	public int getNumTraces() {
		return traces.size();
	}

	@Override
	public Geoshape getLocation(int identifier, int time) {
		// get the cartesian location of this object at the given time
		Point p = getCartesianLocation(identifier, time);
		// retrieve the geospatial projected location
		return getProjectedLocation(p.x, p.y);
	}

	public static void main(String[] args) {
		LocationManager lm = new LocationManager(
				"mobility_traces/500_nodes.txt");
		int i = 0;
		while (true) {
			try {
				Point p = lm.getCartesianLocation(0, i++);
				System.out.println(Integer.toString(i) + ": ("
						+ Integer.toString(p.x) + "," + Integer.toString(p.y)
						+ ")");
			} catch (NoSuchElementException e) {
				System.out.println("FIN!");
				break;
			}
		}
	}
}
