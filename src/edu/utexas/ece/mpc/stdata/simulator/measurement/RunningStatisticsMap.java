package edu.utexas.ece.mpc.stdata.simulator.measurement;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RunningStatisticsMap<T extends RunningStatistics> {
	/** Statistic keys. */
	public static final String DB_SIZE_KEY = "db_size";
	public static final String SIZE_KEY = "size";
	public static final String LENGTH_KEY = "length";
	public static final String AGE_KEY = "age";
	public static final String DIST_H_0_KEY = "dist_h_0";
	public static final String DIST_H_0_PER_SIZE_KEY = "dist_h_0_per_size";
	public static final String DIST_H_0_PER_LENGTH_KEY = "dist_h_0_per_length";
	public static final String DIST_H_0_PER_AGE_KEY = "dist_h_0_per_age";
	public static final String DIST_P_0_KEY = "dist_p_0";
	public static final String DIST_P_0_PER_SIZE_KEY = "dist_p_0_per_size";
	public static final String DIST_P_0_PER_LENGTH_KEY = "dist_p_0_per_length";
	public static final String DIST_P_0_PER_AGE_KEY = "dist_p_0_per_age";

	/** Statistic key arrays. */
	public static final String[] TRAJECTORY_LEVEL_KEYS = { SIZE_KEY,
			LENGTH_KEY, AGE_KEY, DIST_H_0_KEY, DIST_H_0_PER_SIZE_KEY,
			DIST_H_0_PER_LENGTH_KEY, DIST_H_0_PER_AGE_KEY, DIST_P_0_KEY,
			DIST_P_0_PER_SIZE_KEY, DIST_P_0_PER_LENGTH_KEY,
			DIST_P_0_PER_AGE_KEY };

	public static final String[] HOST_LEVEL_KEYS = { SIZE_KEY, LENGTH_KEY,
			AGE_KEY, DIST_H_0_KEY, DIST_H_0_PER_SIZE_KEY,
			DIST_H_0_PER_LENGTH_KEY, DIST_H_0_PER_AGE_KEY, DIST_P_0_KEY,
			DIST_P_0_PER_SIZE_KEY, DIST_P_0_PER_LENGTH_KEY,
			DIST_P_0_PER_AGE_KEY };

	public static final String[] SIMULATION_LEVEL_KEYS = { DB_SIZE_KEY,
			SIZE_KEY, LENGTH_KEY, AGE_KEY, DIST_H_0_KEY, DIST_H_0_PER_SIZE_KEY,
			DIST_H_0_PER_LENGTH_KEY, DIST_H_0_PER_AGE_KEY, DIST_P_0_KEY,
			DIST_P_0_PER_SIZE_KEY, DIST_P_0_PER_LENGTH_KEY,
			DIST_P_0_PER_AGE_KEY };

	public static final String[] OVERALL_KEYS = { DB_SIZE_KEY, SIZE_KEY,
			LENGTH_KEY, AGE_KEY, DIST_H_0_KEY, DIST_H_0_PER_SIZE_KEY,
			DIST_H_0_PER_LENGTH_KEY, DIST_H_0_PER_AGE_KEY, DIST_P_0_KEY,
			DIST_P_0_PER_SIZE_KEY, DIST_P_0_PER_LENGTH_KEY,
			DIST_P_0_PER_AGE_KEY };

	/** The running statistics class type. */
	private Class<T> type;

	/** Statistic map. */
	private Map<String, T> map;

	public RunningStatisticsMap(Class<T> type, String[] keys) {
		// !!!: preservation of insertion order is imperative, so we use a
		// LinkedHashMap
		map = new LinkedHashMap<String, T>();

		for (String key : keys) {
			try {
				map.put(key, type.newInstance());

			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	 * Returns the statistics map's key set.
	 * 
	 * @return
	 */
	public Set<String> getKeys() {
		return map.keySet();
	}

	/**
	 * Returns an ordered array of running statistics per the order of keys
	 * provided at creation.
	 * 
	 * @return
	 */
	public Collection<T> getRunningStatistics() {
		return map.values();
	}

	public Set<Entry<String, T>> getEntries() {
		return map.entrySet();
	}

	/**
	 * Returns the running statistics holder with the provided key.
	 * 
	 * @param key
	 * @return
	 */
	public T get(String key) {
		return map.get(key);
	}

	public static RunningStatistics[] getRunningStatisticsArray(
			RunningStatisticsMap<RunningStatistics> statistcs) {
		Collection<RunningStatistics> coll = statistcs.getRunningStatistics();
		return coll.toArray(new RunningStatistics[coll.size()]);
	}
	
	public static ReducibleRunningStatistics[] getReducibleRunningStatisticsArray(
			RunningStatisticsMap<ReducibleRunningStatistics> statistcs) {
		Collection<ReducibleRunningStatistics> coll = statistcs.getRunningStatistics();
		return coll.toArray(new ReducibleRunningStatistics[coll.size()]);
	}
}
