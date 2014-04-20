package stdata.simulator.measurement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import stdata.datamodel.vertices.MeasuredDatum.TriggerType;

import com.thinkaurelius.titan.core.attribute.Geoshape;

public class Logger {
	/** Logfile settings. */
	public static final String OVERALL_LOGFILE_PREFIX = "overall";
	public static final String LOGFILE_EXTENSION = ".csv";
	public static final String LOGFILE_DELIMITER = ",";

	/** Logfile directory settings. */
	public static final String TRAJECTORY_LOGFILE_SUBDIR = "trajectories";
	public static final String HOST_LOGFILE_SUBDIR = "hosts";
	public static final String SIMULATION_LOGFILE_SUBDIR = "simulations";

	/**
	 * A trajectory logfile measures a particular trajectory each unit of
	 * simulation time.
	 * 
	 * Each line is a list of statistics corresponding to a simulation time.
	 * 
	 * Name: <simulationId>_<hostId>_<trajectoryId>_<TriggerType>.csv
	 */
	public static final String TRAJECTORY_LOGFILE_HEADER = "time,size,length,age,time_0,lat_0,lon_0,lat_p,lon_p,lat_h,lon_h,dist_h_0,dist_p_0";

	public static void appendTrajectoryMeasurement(String logDir,
			String simulation, int host, Object trajectory, TriggerType trigger,
			int time, int size, double length, int age, int time_0,
			Geoshape pos_0, Geoshape pos_p, Geoshape pos_h, double dist_h_0,
			double dist_p_0) {
		String name = simulation + "_" + Integer.toString(host) + "_"
				+ trajectory.toString() + "_"
				+ trigger.toString().toLowerCase();

		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(time)); // time
		sb.append(LOGFILE_DELIMITER + Integer.toString(size)); // size
		sb.append(LOGFILE_DELIMITER + Double.toString(length)); // length
		sb.append(LOGFILE_DELIMITER + Integer.toString(age)); // age
		sb.append(LOGFILE_DELIMITER + Integer.toString(time_0)); // time_0
		sb.append(LOGFILE_DELIMITER
				+ Float.toString(pos_0.getPoint().getLatitude())); // lat_0
		sb.append(LOGFILE_DELIMITER
				+ Float.toString(pos_0.getPoint().getLongitude())); // lon_0
		sb.append(LOGFILE_DELIMITER
				+ Float.toString(pos_p.getPoint().getLatitude())); // lat_p
		sb.append(LOGFILE_DELIMITER
				+ Float.toString(pos_p.getPoint().getLongitude())); // lon_p
		sb.append(LOGFILE_DELIMITER
				+ Float.toString(pos_h.getPoint().getLatitude())); // lat_h
		sb.append(LOGFILE_DELIMITER
				+ Float.toString(pos_h.getPoint().getLongitude())); // lon_h
		sb.append(LOGFILE_DELIMITER + Double.toString(dist_h_0)); // dish_h_0
		sb.append(LOGFILE_DELIMITER + Double.toString(dist_p_0)); // dist_p_0

		appendLogFileLine(logDir + File.pathSeparator
				+ TRAJECTORY_LOGFILE_SUBDIR, name, TRAJECTORY_LOGFILE_HEADER,
				sb.toString());
	}

	/**
	 * A host logfile aggregates over a particular host's trajectories each unit
	 * of simulation time.
	 * 
	 * Each line is a list of moving aggregates over all of a host's
	 * trajectories at a particular simulation time.
	 * 
	 * Name: <simulationId>_<hostId>_<TriggerType>.csv
	 */
	public static final String HOST_LOGFILE_HEADER = "time"
			+ ",db_size"
			+ ",size_min,size_max,size_avg,size_var,size_stdev"
			+ ",length_min,length_max,length_avg,length_var,length_stdev"
			+ ",age_min,age_max,age_avg,age_var,age_stdev"
			+ ",dist_h_0_min,dist_h_0_max,dist_h_0_avg,dist_h_0_var,dist_h_0_stdev"
			+ ",dist_p_0_min,dist_p_0_max,dist_p_0_avg,dist_p_0_var,dist_p_0_stdev";

	public static void appendHostMeasurement(String logDir, String simulation,
			int host, TriggerType trigger, int time, int db_size,
			RunningStatistics... statistics) {
		String name = simulation + "_" + Integer.toString(host) + "_"
				+ trigger.toString().toLowerCase();

		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(time));
		sb.append(LOGFILE_DELIMITER + Integer.toString(db_size));

		for (RunningStatistics s : statistics) {
			sb.append(s.toDelimitedString(LOGFILE_DELIMITER, true, false));
		}

		appendLogFileLine(logDir + File.pathSeparator + HOST_LOGFILE_SUBDIR,
				name, HOST_LOGFILE_HEADER, sb.toString());
	}

	/**
	 * A simulation logfile aggregates over a particular simulation's hosts each
	 * unit of simulation time.
	 * 
	 * Each line is a list of moving aggregates over all of the simulation's
	 * hosts at a particular simluation time.
	 * 
	 * Name: <simulationId>_<TriggerType>.csv
	 */
	public static final String SIMULATION_LOGFILE_HEADER = "time"
			+ ",db_size_min,db_size_max,db_size_avg,db_size_var,db_size_stdev"
			+ ",size_min,size_max,size_avg,size_var,size_stdev"
			+ ",length_min,length_max,length_avg,length_var,length_stdev"
			+ ",age_min,age_max,age_avg,age_var,age_stdev"
			+ ",dist_h_0_min,dist_h_0_max,dist_h_0_avg,dist_h_0_var,dist_h_0_stdev"
			+ ",dist_p_0_min,dist_p_0_max,dist_p_0_avg,dist_p_0_var,dist_p_0_stdev";

	public static void appendSimulationMeasurement(String logDir,
			String simulation, TriggerType trigger, int time,
			RunningStatistics... statistics) {
		String name = simulation + "_" + trigger.toString().toLowerCase();

		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(time));

		for (RunningStatistics s : statistics) {
			sb.append(s.toDelimitedString(LOGFILE_DELIMITER, true, false));
		}

		appendLogFileLine(logDir + File.pathSeparator
				+ SIMULATION_LOGFILE_SUBDIR, name, SIMULATION_LOGFILE_HEADER,
				sb.toString());
	}

	/**
	 * An overall logfile aggregates over the entire simulation time for each
	 * simulation.
	 * 
	 * Each line is a list of aggregates for a particular simulation over it's
	 * whole duration.
	 * 
	 * Name: overall_<TriggerType>.csv
	 */
	public static final String OVERALL_LOGFILE_HEADER = "db_size_min,db_size_max,db_size_avg,db_size_var,db_size_stdev"
			+ ",size_min,size_max,size_avg,size_var,size_stdev"
			+ ",length_min,length_max,length_avg,length_var,length_stdev"
			+ ",age_min,age_max,age_avg,age_var,age_stdev"
			+ ",dist_h_0_min,dist_h_0_max,dist_h_0_avg,dist_h_0_var,dist_h_0_stdev"
			+ ",dist_p_0_min,dist_p_0_max,dist_p_0_avg,dist_p_0_var,dist_p_0_stdev";

	public static void appendOverallMeasurement(String logDir,
			TriggerType trigger, RunningStatistics... statistics) {
		String name = "overall_" + trigger.toString().toLowerCase();

		StringBuilder sb = new StringBuilder();

		int statCount = 0;
		for (RunningStatistics s : statistics) {
			if (statCount++ != 0)
				sb.append(s.toDelimitedString(LOGFILE_DELIMITER, false, false));
			else
				sb.append(s.toDelimitedString(LOGFILE_DELIMITER, true, false));
		}

		appendLogFileLine(logDir, name, OVERALL_LOGFILE_HEADER, sb.toString());
	}

	private static void appendLogFileLine(String dir, String name,
			String header, String line) {
		try {
			PrintWriter out = null;
			File logdir = new File(dir);
			File logfile = new File(dir + File.separator + name
					+ LOGFILE_EXTENSION);

			// check if the directory exists
			if (!logdir.exists())
				logdir.mkdir();

			// check if the logfile exists
			if (!logfile.exists() && !logfile.isDirectory()) {
				logfile.createNewFile();
				out = new PrintWriter(new BufferedWriter(new FileWriter(
						logfile, true)));
				out.println(header);
			}

			if (out == null)
				out = new PrintWriter(new BufferedWriter(new FileWriter(
						logfile, true)));

			out.println(line);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
