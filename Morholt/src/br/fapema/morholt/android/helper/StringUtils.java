package br.fapema.morholt.android.helper;

public class StringUtils {
	public static boolean isBlank (String string) {
		return string == null || string.isEmpty();
	}
	public static boolean isNotBlank (String string) {
		return !isBlank(string);
	}
	public static boolean equalsOrBothBlank(String s1, String s2) {
		return (isBlank(s1) && isBlank(s2)) || ((!isBlank(s1)) && s1.equals(s2));
	}
	
}
