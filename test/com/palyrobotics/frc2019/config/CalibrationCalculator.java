package com.palyrobotics.frc2020.config;

import org.junit.Test;

public class CalibrationCalculator {
	public boolean blue = false;

	@Test
	public void calculateBaseline() {

	}

	@Test
	public void generalCalibration() {
		System.out.println("Inches to ticks: " + Constants.kDriveTicksPerInch);
		System.out.println("Inches per degree: " + Constants.kDriveInchesPerDegree);
		System.out.println("60 degrees: " + Constants.kDriveInchesPerDegree * 60);
		System.out.println("61.8 degrees: " + (21.5 / 90) * 60);
		System.out.println("93 degrees: " + (21.5 / 90) * 90);
		System.out.println("");
	}
}
