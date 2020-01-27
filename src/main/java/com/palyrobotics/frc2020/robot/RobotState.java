package com.palyrobotics.frc2020.robot;

import java.util.List;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.util.StringUtil;
import com.revrobotics.ColorMatchResult;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpiutil.CircularBuffer;

/**
 * Holds the current physical state of the robot from our sensors.
 *
 * @author Nihar
 */
public class RobotState {

	public enum GamePeriod {
		AUTO, TELEOP, TESTING, DISABLED
	}

	// TODO: Reorder this and add comments to separate by subsystem and function
	// (logger, misc, etc.)
	public static final String LOGGER_TAB = StringUtil.classToJsonName(RobotState.class);
	public static final int kUltrasonicBufferSize = 10;
	private final DifferentialDriveOdometry driveOdometry = new DifferentialDriveOdometry(new Rotation2d());
	public GamePeriod gamePeriod = GamePeriod.DISABLED;
	public double driveHeadingDegrees;
	public boolean driveIsQuickTurning;
	public double driveLeftVelocity, driveRightVelocity, driveLeftPosition, driveRightPosition;
	public Pose2d drivePose = new Pose2d();
	public String gameData;
	public boolean atVisionTargetThreshold;
	public String closestColorString;
	public double closestColorConfidence;
	public Color detectedRGBVals;
	public ColorMatchResult closestColorRGB;
	public double shooterVelocity;
	public CircularBuffer backIndexerUltrasonicReadings = new CircularBuffer(kUltrasonicBufferSize),
			frontIndexerUltrasonicReadings = new CircularBuffer(kUltrasonicBufferSize),
			topIndexerUltrasonicReadings = new CircularBuffer(kUltrasonicBufferSize);
	public boolean hasBackUltrasonicBall, hasFrontUltrasonicBall, hasTopUltrasonicBall;

	public void resetUltrasonics() {
		for (CircularBuffer buffer : List.of(backIndexerUltrasonicReadings, frontIndexerUltrasonicReadings)) {
			for (int i = 0; i < kUltrasonicBufferSize; i++) {
				buffer.addFirst(Double.MAX_VALUE);
			}
		}
	}

	public void resetOdometry(Pose2d pose) {
		driveOdometry.resetPosition(pose, new Rotation2d());
		Log.info(LOGGER_TAB, "Odometry reset!");
	}

	public void updateOdometry(double headingDegrees, double leftMeters, double rightMeters) {
		drivePose = driveOdometry.update(Rotation2d.fromDegrees(headingDegrees), leftMeters, rightMeters);
	}
}
