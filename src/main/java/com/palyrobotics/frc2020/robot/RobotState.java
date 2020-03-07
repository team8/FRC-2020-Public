package com.palyrobotics.frc2020.robot;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.util.Util;
import com.revrobotics.ColorMatchResult;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.util.Color;

/**
 * Holds the current physical state of the robot from our sensors.
 */
@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class RobotState {

	public enum GamePeriod {
		AUTO, TELEOP, TESTING, DISABLED
	}

	public static final String kLoggerTag = Util.classToJsonName(RobotState.class);

	/* Drive */
	private final DifferentialDriveOdometry driveOdometry = new DifferentialDriveOdometry(new Rotation2d());
	public double driveYawDegrees, driveYawAngularVelocityDegrees;
	public boolean driveIsQuickTurning, driveIsSlowTurning;
	public double driveLeftVelocity, driveRightVelocity, driveLeftPosition, driveRightPosition;
	public Pose2d drivePoseMeters = new Pose2d();
	public double driveVelocityMetersPerSecond;
	public boolean driveIsGyroReady;

	/* Indexer */
	public boolean indexerIsHopperExtended;
	public boolean indexerHasBackBall, indexerHasFrontBall, indexerHasTopBall;
	public double indexerMasterVelocity;

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

	/* Game and Field */
	public GamePeriod gamePeriod = GamePeriod.DISABLED;
	public String gameData;

	public void resetOdometry(Pose2d pose) {
		driveOdometry.resetPosition(pose, pose.getRotation());
		drivePoseMeters = driveOdometry.getPoseMeters();
		driveVelocityMetersPerSecond = 0.0;
		Log.info(kLoggerTag, String.format("Odometry reset to: %s", pose));
	}

	public void updateOdometry(double yawDegrees, double leftMeters, double rightMeters) {
		drivePoseMeters = driveOdometry.update(Rotation2d.fromDegrees(yawDegrees), leftMeters, rightMeters);
		ChassisSpeeds speeds = DriveConstants.kKinematics.toChassisSpeeds(new DifferentialDriveWheelSpeeds(driveLeftVelocity, driveRightVelocity));
		driveVelocityMetersPerSecond = Math.sqrt(Math.pow(speeds.vxMetersPerSecond, 2) + Math.pow(speeds.vyMetersPerSecond, 2));
	}
}
