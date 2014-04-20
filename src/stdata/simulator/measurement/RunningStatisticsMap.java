package stdata.simulator.measurement;

import java.util.HashMap;
import java.util.Map;

public class RunningStatisticsMap {
	public static final String DB_SIZE_KEY = "db_size";
	public static final String SIZE_KEY = "size";
	public static final String LENGTH_KEY = "length";
	public static final String AGE_KEY = "age";
	public static final String DIST_H_0_KEY = "dist_h_0";
	public static final String DIST_P_0_KEY = "dist_p_0";

	public static final String[] TRAJECTORY_LEVEL_KEYS = { SIZE_KEY,
			LENGTH_KEY, AGE_KEY, DIST_H_0_KEY, DIST_P_0_KEY };

	public static final String[] HOST_LEVEL_KEYS = { SIZE_KEY, LENGTH_KEY,
			AGE_KEY, DIST_H_0_KEY, DIST_P_0_KEY };

	public static final String[] SIMULATION_LEVEL_KEYS = { DB_SIZE_KEY,
			SIZE_KEY, LENGTH_KEY, AGE_KEY, DIST_H_0_KEY, DIST_P_0_KEY };

	public static final String[] OVERALL_KEYS = { DB_SIZE_KEY, SIZE_KEY,
			LENGTH_KEY, AGE_KEY, DIST_H_0_KEY, DIST_P_0_KEY };

	Map<String, RunningStatistics> map;

	public RunningStatisticsMap(String[] keys) {
		map = new HashMap<String, RunningStatistics>();

		for (String key : keys)
			map.put(key, new RunningStatistics());
	}

	/**
	 * Pushes the provided value onto the running statistics holder with the
	 * provided key.
	 * 
	 * @param key
	 * @param value
	 */
	public void push(String key, double value) {
		map.get(key).push(value);
	}

	/**
	 * Returns the running statistics holder with the provided key.
	 * 
	 * @param key
	 * @return
	 */
	public RunningStatistics get(String key) {
		return map.get(key);
	}

}
