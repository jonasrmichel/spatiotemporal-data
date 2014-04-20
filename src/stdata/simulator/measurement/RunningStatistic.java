package stdata.simulator.measurement;

public class Statistics<T extends Object> {
	T average;
	T median;
	T minimum;
	T maximum;
	T stdev;

	public Statistics(T average, T median, T minimum, T maximum, T stdev) {
		this.average = average;
		this.median = median;
		this.minimum = minimum;
		this.maximum = maximum;
		this.stdev = stdev;
	}

	public String toDelimitedString(String delimiter, boolean prepended,
			boolean postpended) {
		StringBuilder sb = new StringBuilder();

		if (prepended)
			sb.append(delimiter);

		sb.append(average.toString() + delimiter);
		sb.append(median.toString() + delimiter);
		sb.append(minimum.toString() + delimiter);
		sb.append(maximum.toString() + delimiter);
		sb.append(stdev.toString());

		if (postpended)
			sb.append(delimiter);

		return sb.toString();
	}
}
