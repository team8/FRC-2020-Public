package com.palyrobotics.frc2020.config.constants;

import java.util.List;

import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.util.Color;

public class SpinnerConstants {

	// May need to be re-tuned based on lighting conditions
	public static final Color kCyanCPTarget = ColorMatch.makeColor(0.2, 0.5, 0.4),
			kGreenCPTarget = ColorMatch.makeColor(0.213, 0.537, 0.249), kRedCPTarget = ColorMatch.makeColor(0.396, 0.415, 0.19),
			kYellowCPTarget = ColorMatch.makeColor(0.312, 0.537, 0.151);
	// scale ratio to allow for control panel 1/eighth rotation
	public static final double kEighthCPMovementGearRatio = 1.5;
	public static final List<String> kControlPanelColorOrder = List.of("Y", "R", "G", "C");

	private SpinnerConstants() {
	}
}
