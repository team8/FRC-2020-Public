package com.palyrobotics.frc2020.util;

public class StringUtil {

	private StringUtil() {
	}

	public static String classToJsonName(Class<?> clazz) {
		String className = clazz.getSimpleName();
		// Make first character lowercase to match JSON conventions
		return Character.toLowerCase(className.charAt(0)) + className.substring(1);
	}
}
