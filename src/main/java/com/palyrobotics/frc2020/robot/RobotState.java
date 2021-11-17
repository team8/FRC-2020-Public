package com.palyrobotics.frc2020.robot;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.MeasurementConfig;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.estimator.DifferentialDrivePoseEstimator;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpiutil.math.MatBuilder;
import edu.wpi.first.wpiutil.math.Matrix;
import edu.wpi.first.wpiutil.math.Nat;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;

/**
 * Holds the current physical state of the robot from our sensors.
 */
@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class RobotState {

	public enum GamePeriod {
		AUTO, TELEOP, TESTING, DISABLED
	}

	public static final String kLoggerTag = Util.classToJsonName(RobotState.class);
	private final MeasurementConfig mMeasurementConfig = Configs.get(MeasurementConfig.class);
	public Matrix stateStdDevs = fillNx1Matrix(mMeasurementConfig.stateStdDevs, new Matrix(Nat.N5(), Nat.N1()));
	public Matrix localMeasurementStdDevs = fillNx1Matrix(mMeasurementConfig.localMeasurementStdDevs, new Matrix(Nat.N3(), Nat.N1()));
	public Matrix visionMeasurementStdDevs = fillNx1Matrix(mMeasurementConfig.visionMeasurementStdDevs, new Matrix(Nat.N3(), Nat.N1()));

	/* Drive */
	//TODO: finish up with the uncertainty matrices
	private final DifferentialDrivePoseEstimator driveOdometry = new DifferentialDrivePoseEstimator(new Rotation2d(), new Pose2d(0,0, new Rotation2d(0)), stateStdDevs, localMeasurementStdDevs,visionMeasurementStdDevs);
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
		drivePoseMeters = driveOdometry.getEstimatedPosition();
		driveVelocityMetersPerSecond = 0.0;
		Log.info(kLoggerTag, String.format("Odometry reset to: %s", pose));
	}

	public void updateOdometry(double yawDegrees, double leftVelMeters, double rightVelMeters, double leftMeters, double rightMeters) {
		drivePoseMeters = driveOdometry.update(Rotation2d.fromDegrees(yawDegrees), new DifferentialDriveWheelSpeeds(leftVelMeters, rightVelMeters), leftMeters, rightMeters);
		ChassisSpeeds speeds = DriveConstants.kKinematics.toChassisSpeeds(new DifferentialDriveWheelSpeeds(driveLeftVelocity, driveRightVelocity));
		driveVelocityMetersPerSecond = Math.sqrt(Math.pow(speeds.vxMetersPerSecond, 2) + Math.pow(speeds.vyMetersPerSecond, 2));
	}
	private Matrix fillNx1Matrix(ArrayList<Double> data, Matrix matrix){
		//I know this is ugly, but it's the only way I could get it to work
		for (int i = 0; i < data.size(); i++){
			matrix.set(i, 0, data.get(i));
		}
		return matrix;
	}
}
