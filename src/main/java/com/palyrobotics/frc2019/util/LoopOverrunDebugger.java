package com.palyrobotics.frc2019.util;

import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Quintin Dwight
 */
public class LoopOverrunDebugger {

    private static class Measurement {
        Measurement(String name, double durationSeconds) {
            this.name = name;
            this.durationSeconds = durationSeconds;
        }

        String name;
        double durationSeconds;
    }

    private String mName;
    private double mStartTimeSeconds, mLastSeconds;
    private Double mPrintDuration;
    private ArrayList<Measurement> mMeasurements = new ArrayList<>(8);

    public LoopOverrunDebugger(String name) {
        mName = name;
        mStartTimeSeconds = mLastSeconds = Timer.getFPGATimestamp();
    }

    public LoopOverrunDebugger(String name, double printDurationSeconds) {
        this(name);
        mPrintDuration = printDurationSeconds;
    }

    public void addPoint(String name) {
        double now = Timer.getFPGATimestamp();
        double deltaSeconds = now - mLastSeconds;
        mLastSeconds = now;
        mMeasurements.add(new Measurement(name, deltaSeconds));
    }

    public void finish() {
        Optional.ofNullable(mPrintDuration)
                .filter(printDurationSeconds -> Timer.getFPGATimestamp() - mStartTimeSeconds > printDurationSeconds)
                .ifPresent(duration -> printSummary());
    }

    public void finishAndPrint() {
        printSummary();
    }

    private void printSummary() {
        System.out.printf("[Time Summary] [%s]%n", mName);
        for (Measurement measurement : mMeasurements) {
            System.out.printf("    <%s> %f seconds%n", measurement.name, measurement.durationSeconds);
        }
    }
}
