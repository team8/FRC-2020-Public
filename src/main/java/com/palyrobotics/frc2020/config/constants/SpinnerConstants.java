package com.palyrobotics.frc2020.config.constants;

import java.util.List;

import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.util.Color;

public class SpinnerConstants {

	// May need to be re-tuned based on lighting conditions
	public static final Color kCyanCPTarget = ColorMatch.makeColor(0.2, 0.5, 0.4),
			kGreenCPTarget = ColorMatch.makeColor(0.2, 0.546, 0.229), kRedCPTarget = ColorMatch.makeColor(0.3, 0.5, 0.2),
			kYellowCPTarget = ColorMatch.makeColor(0.3, 0.55, 0.147);
	// scale ratio to allow for control panel 1/eighth rotation
	public static final double kEighthCPMovementGearRatio = 1.5;
	public static final List<String> kControlPanelColorOrder = List.of("Y", "B", "G", "R");

	private SpinnerConstants() {
	}
}
