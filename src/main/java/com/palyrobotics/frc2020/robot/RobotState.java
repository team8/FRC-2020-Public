package com.palyrobotics.frc2020.robot;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.util.StringUtil;
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
public class RobotState {

	public enum GamePeriod {
		AUTO, TELEOP, TESTING, DISABLED
	}

	public static final String LOGGER_TAB = StringUtil.classToJsonName(RobotState.class);
	private static RobotState sInstance = new RobotState();
	private final DifferentialDriveOdometry driveOdometry = new DifferentialDriveOdometry(new Rotation2d());
	public GamePeriod gamePeriod = GamePeriod.DISABLED;
	public double driveHeading;
	public boolean driveIsQuickTurning;
	public double driveLeftVelocity, driveRightVelocity, driveLeftPosition, driveRightPosition;
	public Pose2d drivePose = new Pose2d();
	public String gameData;
	public boolean atVisionTargetThreshold;
	public String closestColorString;
	public double closestColorConfidence;
	public Color detectedRGBVals;
	public ColorMatchResult closestColorRGB;

	public RobotState() {
	}

	public void resetOdometry() {
		driveOdometry.resetPosition(new Pose2d(), new Rotation2d());
		Log.info(LOGGER_TAB, "Odometry reset!");
	}

	public void updateOdometry(double headingDegrees, double leftMeters, double rightMeters) {
		drivePose = driveOdometry.update(Rotation2d.fromDegrees(Math.IEEEremainder(headingDegrees, 360.0)), leftMeters,
				rightMeters);
	}
}
