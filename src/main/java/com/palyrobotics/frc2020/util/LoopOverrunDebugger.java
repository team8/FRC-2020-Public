package com.palyrobotics.frc2020.util;

import java.util.ArrayList;

import com.esotericsoftware.minlog.Log;

import edu.wpi.first.wpilibj.Timer;

/**
 * @author Quintin
 */
public class LoopOverrunDebugger {

	private static class Measurement {

		String name;
		double durationSeconds;

		Measurement(String name, double durationSeconds) {
			this.name = name;
			this.durationSeconds = durationSeconds;
		}
	}

	private String mName;
	private Timer mTimer = new Timer();
	private Double mPrintDuration;
	private ArrayList<Measurement> mMeasurements = new ArrayList<>(8);

	public LoopOverrunDebugger(String name, double printDurationSeconds) {
		this(name);
		mPrintDuration = printDurationSeconds;
	}

	public LoopOverrunDebugger(String name) {
		mName = name;
		mTimer.start();
	}

	public void addPoint(String name) {
		mMeasurements.add(new Measurement(name, mTimer.get()));
	}

	public void finish() {
		if (mPrintDuration == null || mTimer.hasElapsed(mPrintDuration)) printSummary();
	}

	private void printSummary() {
		var builder = new StringBuilder();
		builder.append(String.format("[Time Summary] [%s]%n", mName));
		for (Measurement measurement : mMeasurements) {
			builder.append(String.format("    <%s> %f seconds%n", measurement.name, measurement.durationSeconds));
		}
		Log.info(Util.classToJsonName(getClass()), builder.toString());
	}

	public void reset() {
		mTimer.reset();
		mMeasurements.clear();
	}

	public void finishAndPrint() {
		printSummary();
	}
}
