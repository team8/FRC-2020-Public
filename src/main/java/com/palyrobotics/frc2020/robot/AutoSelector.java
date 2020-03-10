package com.palyrobotics.frc2020.robot;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.auto.AutoBase;
import com.palyrobotics.frc2020.auto.TrenchStealTwoShootFive;
import com.palyrobotics.frc2020.util.Util;

import org.reflections.Reflections;

public class AutoSelector {

	// ============================================================= //
	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= //

	private static AutoBase sChosenAuto = new TrenchStealTwoShootFive();

	// =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= //
	// ============================================================= //

	private static final String kLoggerTag = Util.classToJsonName(AutoSelector.class);
	private static final Set<AutoBase> sAutos = new Reflections(AutoBase.class).getSubTypesOf(AutoBase.class)
			.stream().flatMap(autoClass -> {
				try {
					return Stream.of(autoClass.getConstructor().newInstance());
				} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException exception) {
					Log.error(kLoggerTag, String.format("Failed to register auto %s", autoClass.getName()));
					return Stream.empty();
				}
			}).collect(Collectors.toUnmodifiableSet());
	private static final Map<String, AutoBase> sNameToAuto = sAutos.stream().collect(
			Collectors.toUnmodifiableMap(AutoBase::getName, Function.identity()));

	private AutoSelector() {
	}

	public static boolean setAuto(String autoName) {
		if (sNameToAuto.containsKey(autoName)) {
			sChosenAuto = sNameToAuto.get(autoName);
			Log.info(kLoggerTag, String.format("Selected auto: %s", autoName));
			return true;
		} else {
			Log.warn(kLoggerTag, String.format("Cannot select unknown auto %s", autoName));
			return false;
		}
	}

	public static AutoBase getAuto() {
		return sChosenAuto;
	}

	public static Set<String> getAutoNames() {
		return sNameToAuto.keySet();
	}
}
