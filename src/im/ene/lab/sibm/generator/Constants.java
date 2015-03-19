package im.ene.lab.sibm.generator;

public class Constants {

	// reference
	// http://www.indexmundi.com/japan/demographics_profile.html
	
	public static int DEFAULT_MIN_AGE = 3;

	public static int DEFAULT_MAX_AGE = 85;

	private static float MAN_PER_WOMAN = 0.95f;
	
	// TODO sex rate by age, ref http://www.indexmundi.com/japan/demographics_profile.html
	public static float MAN_RATE = MAN_PER_WOMAN / (MAN_PER_WOMAN + 1);
	
	public static String[] GENDER = { "Male", "Female" };
	
}
