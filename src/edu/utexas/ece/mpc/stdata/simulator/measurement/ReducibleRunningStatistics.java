package edu.utexas.ece.mpc.stdata.simulator.measurement;

public class ReducibleRunningStatistics extends RunningStatistics {

	public ReducibleRunningStatistics() {
		super();
	}

	/**
	 * Reduces another running statistics into this one.
	 * 
	 * @param statistics
	 */
	public void reduce(RunningStatistics statistics) {
		double delta = getAverage() - statistics.getAverage();
		double weight = (n > 0 || statistics.n > 0) ? (n * statistics.n)
				/ (n + statistics.n) : 0;

		newS += statistics.newS + delta * delta * weight;
		sum += statistics.sum;
		minimum = Math.min(minimum, statistics.minimum);
		maximum = Math.max(maximum, statistics.maximum);
		n += statistics.n;
		newM = n > 0 ? sum / n : 0;
	}

}
