package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.SpinnerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.util.Color;

public class Spinner extends Subsystem {

	public enum SpinnerState {
		TO_COLOR, SPIN, IDLE
	}

	private static final SpinnerConfig mConfig = Configs.get(SpinnerConfig.class);
	public static final Color kCyanCPTarget = ColorMatch.makeColor(mConfig.colorSensorCyanRGB.get(0),
			mConfig.colorSensorCyanRGB.get(1), mConfig.colorSensorCyanRGB.get(2)),
			kGreenCPTarget = ColorMatch.makeColor(mConfig.colorSensorGreenRGB.get(0),
					mConfig.colorSensorGreenRGB.get(1), mConfig.colorSensorGreenRGB.get(2)),
			kRedCPTarget = ColorMatch.makeColor(mConfig.colorSensorRedRGB.get(0), mConfig.colorSensorRedRGB.get(1),
					mConfig.colorSensorRedRGB.get(2)),
			kYellowCPTarget = ColorMatch.makeColor(mConfig.colorSensorYellowRGB.get(0),
					mConfig.colorSensorYellowRGB.get(1), mConfig.colorSensorYellowRGB.get(2));
	private static Spinner sInstance = new Spinner();
	private double mOutput;
	private String mPreviousColor;
	private int mColorPassedCount;

	private Spinner() {
	}

	public static Spinner getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		SpinnerState spinnerState = commands.spinnerWantedState;
		String currentColor = robotState.closestColorString;
		switch (spinnerState) {
			case IDLE:
				mOutput = mConfig.idleOutput;
				break;
			case SPIN:
				mOutput = mConfig.rotationOutput;
				if (mColorPassedCount > 30) {
					mColorPassedCount = 0;
				} else {
					if (!mPreviousColor.equals(currentColor)) {
						mColorPassedCount++;
					}
				}
				break;
			case TO_COLOR:
				mOutput = mConfig.positionOutput;
				break;
		}
		mPreviousColor = currentColor;
	}

	public double getOutput() {
		return mOutput;
	}
}
