package com.palyrobotics.frc2020.util.csvlogger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.UnaryOperator;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.util.Util;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;

/**
 * @author Jason Liu, Quintin
 */
public class CSVWriter {

	private static final String kCommaDeliminator = ",", kNewLine = "\n", kFileName = "canlog.csv";
	private static final int kMaxSizeBeforeWrite = 100000;
	private static final String kLoggerTag = Util.classToJsonName(CSVWriter.class);
	private static final Path sCsvFile = RobotBase.isReal() ? Paths.get("/home/lvuser", kFileName) :
			Paths.get(Filesystem.getOperatingDirectory().toString(), kFileName);
	private static final StringBuilder sBuilder = new StringBuilder(kMaxSizeBeforeWrite);
	private static double sStartTime;

	static {
		resetTimer();
	}

	private CSVWriter() {
	}

	public static void cleanFile() {
		try {
			Files.deleteIfExists(sCsvFile);
		} catch (IOException deleteException) {
			Log.error(kLoggerTag, "Failed to delete existing file!", deleteException);
		}
	}

	public static void resetTimer() {
		sStartTime = Timer.getFPGATimestamp();
	}

	public static void addData(String key, Object customSecond, Object value) {
		addData(key, customSecond, builder -> builder.append(value));
	}

	private static void addData(String key, Object secondValue, UnaryOperator<StringBuilder> valueCellWriter) {
		sBuilder.append(key).append(kCommaDeliminator).append(secondValue).append(kCommaDeliminator);
		valueCellWriter.apply(sBuilder).append(kNewLine);
		if (sBuilder.length() > kMaxSizeBeforeWrite) write();
	}

	public static void write() {
		Log.info(kLoggerTag, "Writing...");
		try (var fileWriter = new FileWriter(sCsvFile.toFile(), true)) {
			fileWriter.append(sBuilder.toString());
		} catch (IOException writeException) {
			Log.error(kLoggerTag, "Failed to write", writeException);
		} finally {
			sBuilder.setLength(0);
		}
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
