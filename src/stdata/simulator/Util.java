package stdata.simulator;

public class Util {

	public static void report(Class clazz, String message) {
		System.out.println(clazz.getSimpleName() + ": " + message);
	}

	public static boolean arrayContainsInt(int[] array, int value) {
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;

		return false;
	}
}
