package com.palyrobotics.frc2020.robot;

import java.util.List;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.util.SolenoidState;
import com.palyrobotics.frc2020.util.Util;
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

	public static final String kLoggerTag = Util.classToJsonName(RobotState.class);
	public static final int kUltrasonicBufferSize = 10;
	/* Game and Field */
	public GamePeriod gamePeriod = GamePeriod.DISABLED;
	public String gameData, closestColorString;
	public double closestColorConfidence;
	public Color detectedRGBValues;
	public ColorMatchResult closestColorRGB;
	/* Drive */
	private final DifferentialDriveOdometry driveOdometry = new DifferentialDriveOdometry(new Rotation2d());
	public double driveYawDegrees;
	public boolean driveIsQuickTurning;
	public double driveLeftVelocity, driveRightVelocity, driveLeftPosition, driveRightPosition;
	public Pose2d drivePose = new Pose2d();
	/* Shooter */
	public double shooterVelocity;
	public SolenoidState shooterHoodSolenoidState = new SolenoidState(),
			shooterBlockingSolenoidState = new SolenoidState();
	// TODO: Reorder this and add comments to separate by subsystem and function
	public CircularBuffer backIndexerUltrasonicReadings = new CircularBuffer(kUltrasonicBufferSize),
			frontIndexerUltrasonicReadings = new CircularBuffer(kUltrasonicBufferSize),
			topIndexerUltrasonicReadings = new CircularBuffer(kUltrasonicBufferSize);
	public boolean hasBackUltrasonicBall, hasFrontUltrasonicBall, hasTopUltrasonicBall;
	public double visionDistanceToTarget;

	// Climber
	public double climberPosition;
	public double climberVelocity;

	public void resetUltrasonics() {
		for (CircularBuffer buffer : List.of(backIndexerUltrasonicReadings, frontIndexerUltrasonicReadings)) {
			for (int i = 0; i < kUltrasonicBufferSize; i++) {
				buffer.addFirst(Double.MAX_VALUE);
			}
		}
	}

	public void resetOdometry(Pose2d pose) {
		driveOdometry.resetPosition(pose, pose.getRotation());
		drivePose = driveOdometry.getPoseMeters();
		Log.info(kLoggerTag, String.format("Odometry reset to: %s", pose));
	}

	public void updateOdometry(double yawDegrees, double leftMeters, double rightMeters) {
		drivePose = driveOdometry.update(Rotation2d.fromDegrees(yawDegrees), leftMeters, rightMeters);
	}
}
