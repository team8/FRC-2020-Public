package com.palyrobotics.frc2020.util.csvlogger;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.util.StringUtil;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.UnaryOperator;

/**
 * @author Jason Liu, Quintin Dwight
 */
public class CSVWriter {

	private static final String COMMA_DELIMITER = ",", NEW_LINE_SEPARATOR = "\n", FILE_NAME = "canlog.csv";
	private static final int ALLOCATE_SIZE = 100000;
	private static final String LOGGER_TAG = StringUtil.classToJsonName(CSVWriter.class);
	private static final File sCsvFile = RobotBase.isReal() ? Paths.get("/home/lvuser", FILE_NAME).toFile() : Paths.get(
			Filesystem.getOperatingDirectory().toString(), FILE_NAME).toFile();
	private static final StringBuilder sBuilder = new StringBuilder(ALLOCATE_SIZE);
	private static double sStartTime;

	static {
		resetTimer();
	}

	private CSVWriter() {
	}

	public static void cleanFile() {
		if (!sCsvFile.delete()) {
			Log.error(LOGGER_TAG, "Failed to delete existing CSV file!");
		}
	}

	public static void resetTimer() {
		sStartTime = Timer.getFPGATimestamp();
	}

	public static void addData(String key, Object customSecond, Object value) {
		addData(key, customSecond, builder -> builder.append(value));
	}

	private static void addData(String key, Object secondValue, UnaryOperator<StringBuilder> valueCellWriter) {
		sBuilder.append(key).append(COMMA_DELIMITER).append(secondValue).append(COMMA_DELIMITER);
		valueCellWriter.apply(sBuilder).append(NEW_LINE_SEPARATOR);
		if (sBuilder.length() > ALLOCATE_SIZE)
			write();
	}

	public static void write() {
		Log.info(LOGGER_TAG, "Writing CSV...");
		try (var fileWriter = new FileWriter(sCsvFile, true)) {
			fileWriter.append(sBuilder.toString());
		} catch (IOException writeException) {
			Log.error(LOGGER_TAG, "Failed to write CSV", writeException);
		} finally {
			sBuilder.setLength(0);
		}
		System.gc();
	}

	public static void addData(String key, Object value) {
		addData(key, getTimeSeconds(), builder -> builder.append(value));
	}

	private static double getTimeSeconds() {
		return Timer.getFPGATimestamp() - sStartTime;
	}

	public static void addData(String key, double value) {
		addData(key, getTimeSeconds(), builder -> builder.append(value));
	}

	public static int getSize() {
		return sBuilder.length();
	}
}
