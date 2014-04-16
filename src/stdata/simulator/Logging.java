package stdata.simulator;

public class Logging {

	public static void report(Class clazz, String message) {
		System.out.println(clazz.getSimpleName() + ": " + message);
	}
}
