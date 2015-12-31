package br.fapema.morholt.web.shared.util;

public class StringUtils {
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}

	public static boolean isBlank(String string) {
		return string == null || string.trim().isEmpty();
	}
	
	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}
	
	public static String defaultString(String string, String defaultString) {
		if (string == null) return defaultString;
		return string;
	}
}
