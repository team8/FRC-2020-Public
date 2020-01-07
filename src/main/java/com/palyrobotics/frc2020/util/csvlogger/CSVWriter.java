package com.palyrobotics.frc2020.util.csvlogger;

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

    private static double sStartTime;
    private static final File sCsvFile = RobotBase.isReal()
            ? Paths.get("/home/lvuser", FILE_NAME).toFile()
            : Paths.get(Filesystem.getOperatingDirectory().toString(), FILE_NAME).toFile();
    private static final StringBuilder sBuilder = new StringBuilder(ALLOCATE_SIZE);

    public static void cleanFile() {
        if (!sCsvFile.delete()) {
            System.err.println("Failed to delete existing CSV file!");
        }
    }

    static {
        resetTimer();
    }

    public static void resetTimer() {
        sStartTime = Timer.getFPGATimestamp();
    }

    private static double getTimeSeconds() {
        return Timer.getFPGATimestamp() - sStartTime;
    }

    private static void addData(String key, Object secondValue, UnaryOperator<StringBuilder> valueCellWriter) {
        sBuilder.append(key).append(COMMA_DELIMITER).append(secondValue).append(COMMA_DELIMITER);
        valueCellWriter.apply(sBuilder).append(NEW_LINE_SEPARATOR);
        if (sBuilder.length() > ALLOCATE_SIZE) write();
    }

    public static void addData(String key, Object customSecond, Object value) {
        addData(key, customSecond, builder -> builder.append(value));
    }

    public static void addData(String key, Object value) {
        addData(key, getTimeSeconds(), builder -> builder.append(value));
    }

    public static void addData(String key, double value) {
        addData(key, getTimeSeconds(), builder -> builder.append(value));
    }

    public static int getSize() {
        return sBuilder.length();
    }

    public static void write() {
        System.out.println("Writing CSV...");
        try (FileWriter fileWriter = new FileWriter(sCsvFile, true)) {
            fileWriter.append(sBuilder.toString());
        } catch (IOException writeException) {
            System.err.println("Failed to write CSV:");
            writeException.printStackTrace();
        } finally {
            sBuilder.setLength(0);
        }
        System.gc();
    }
}