package com.palyrobotics.frc2020.util;

import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;

/**
 * @author Quintin Dwight
 */
public class LoopOverrunDebugger {

    private String mName;
    private Timer mTimer = new Timer();
    private Double mPrintDuration;
    private ArrayList<Measurement> mMeasurements = new ArrayList<>(8);
    public LoopOverrunDebugger(String name) {
        mName = name;
        mTimer.start();
    }

    public LoopOverrunDebugger(String name, double printDurationSeconds) {
        this(name);
        mPrintDuration = printDurationSeconds;
    }

    public void addPoint(String name) {
        mMeasurements.add(new Measurement(name, mTimer.get()));
    }

    public void finish() {
        if (mPrintDuration == null || mTimer.get() > mPrintDuration)
            printSummary();
    }

    public void reset() {
        mTimer.stop();
        mTimer.reset();
        mMeasurements.clear();
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

    private static class Measurement {
        String name;
        double durationSeconds;

        Measurement(String name, double durationSeconds) {
            this.name = name;
            this.durationSeconds = durationSeconds;
        }
    }
}
