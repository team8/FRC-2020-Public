package com.palyrobotics.frc2020.robot;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.util.Util;
import com.revrobotics.ColorMatchResult;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.util.Color;

/**
 * Holds the current physical state of the robot from our sensors.
 *
 * @author Nihar
 */
@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class RobotState {

	public enum GamePeriod {
		AUTO, TELEOP, TESTING, DISABLED
	}

	/* Climber */
	public double climberPosition, climberVelocity;

	/* Drive */
	private final DifferentialDriveOdometry driveOdometry = new DifferentialDriveOdometry(new Rotation2d());
	public double driveYawDegrees;
	public boolean driveIsQuickTurning;
	public double driveLeftVelocity, driveRightVelocity, driveLeftPosition, driveRightPosition;
	public Pose2d drivePose = new Pose2d();

	/* Indexer */
	public boolean indexerIsHopperExtended;
	public boolean indexerHasBackBall, indexerHasFrontBall, indexerHasTopBall;

	/* Intake */
	public boolean intakeIsExtended;

	/* Shooter */
	public double shooterFlywheelVelocity;
	public boolean shooterIsReadyToShoot;
	public boolean shooterIsHoodExtended, shooterIsBlockingExtended;
	public boolean shooterHoodIsInTransition;

	/* Spinner */
	public String closestColorString;
	public double closestColorConfidence;
	public Color detectedRGBValues;
	public ColorMatchResult closestColorRGB;

	/* Turret */
	public double turretYawDegrees;
	public boolean turretAtCenter;
	public boolean turretIsCalibrated;

	/* Game and Field */
	public GamePeriod gamePeriod = GamePeriod.DISABLED;
	public String gameData;

	/* Miscellaneous */
	public static final String kLoggerTag = Util.classToJsonName(RobotState.class);

	public void resetOdometry(Pose2d pose) {
		driveOdometry.resetPosition(pose, pose.getRotation());
		drivePose = driveOdometry.getPoseMeters();
		Log.info(kLoggerTag, String.format("Odometry reset to: %s", pose));
	}

	public void updateOdometry(double yawDegrees, double leftMeters, double rightMeters) {
		drivePose = driveOdometry.update(Rotation2d.fromDegrees(yawDegrees), leftMeters, rightMeters);
	}
}
