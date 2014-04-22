package stdata.simulator.measurement;

/**
 * Created with the help of: http://www.johndcook.com/standard_deviation.html
 * 
 */
public class RunningStatistics {
	protected double minimum;
	protected double maximum;
	protected double sum;

	protected int n;
	protected double oldM, newM, oldS, newS;

	public RunningStatistics() {
		minimum = Double.MAX_VALUE;
		maximum = Double.MIN_VALUE;
		n = 0;
		sum = 0;
	}

	public String toDelimitedString(String delimiter, boolean prepended,
			boolean postpended) {
		StringBuilder sb = new StringBuilder();

		if (prepended)
			sb.append(delimiter);

		sb.append(Double.toString(getMinimum()) + delimiter);
		sb.append(Double.toString(getMaximum()) + delimiter);
		sb.append(Double.toString(getAverage()) + delimiter);
		sb.append(Double.toString(getVariance()) + delimiter);
		sb.append(Double.toString(getStdev()));

		if (postpended)
			sb.append(delimiter);

		return sb.toString();
	}

	public double getAverage() {
		return (n > 0) ? newM : 0d;
	}

	public double getMinimum() {
		return minimum;
	}

	public double getMaximum() {
		return maximum;
	}

	public double getStdev() {
		return Math.sqrt(getVariance());
	}

	public double getVariance() {
		return ((n > 1) ? newS / (n - 1) : 0d);
	}

	public void push(double value) {
		n++;

		minimum = Math.min(minimum, value);
		maximum = Math.max(maximum, value);
		sum += value;

		if (n == 1) {
			oldM = newM = value;
			oldS = 0d;

		} else {
			newM = oldM + (value - oldM) / n;
			newS = oldS + (value - oldM) * (value - newM);

			// set up for next iteration
			oldM = newM;
			oldS = newS;
		}

	}
}
