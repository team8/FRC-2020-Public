package com.palyrobotics.frc2020.config.constants;

import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.util.config.Configs;

import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.util.Units;

public class DrivetrainConstants {

	public static final double kMaxVoltage = 12.0;
	/**
	 * Path following constants
	 */
	public static final double kDriveWheelDiameterInches = 6.0,
			kDriveWheelDiameterMeters = Units.inchesToMeters(kDriveWheelDiameterInches), kTrackWidthInches = 24.5,
			kTrackWidthMeters = Units.inchesToMeters(kTrackWidthInches);

	public static final DifferentialDriveKinematics kKinematics = new DifferentialDriveKinematics(
			DrivetrainConstants.kTrackWidthMeters);
	public static final double kS = 0.145, kV = 2.59, kA = 0.484;
	public static final Pose2d kPathFinishTolerance = new Pose2d(new Translation2d(0.2, 0.2),
			Rotation2d.fromDegrees(5.0));

	/**
	 * Unit Conversions
	 */
	public static final double kDriveMetersPerRotation = 0.04677268475,
			kDriveMetersPerSecondPerRpm = kDriveMetersPerRotation;
	/**
	 * Cheesy Drive Constants
	 */
	public static final double kDeadBand = 0.02;

	private static DriveConfig kDriveConfig = Configs.get(DriveConfig.class);

	private DrivetrainConstants() {
	}

	/**
	 * @return Copy of the standard trajectory configuration. Can be modified
	 *         safely.
	 */
	public static TrajectoryConfig getStandardTrajectoryConfig() {
		return new TrajectoryConfig(kDriveConfig.maxPathVelocityMetersPerSecond,
				kDriveConfig.maxPathAccelerationMetersPerSecondSquared).setKinematics(kKinematics)
						.addConstraint(new DifferentialDriveVoltageConstraint(new SimpleMotorFeedforward(kS, kV, kA),
								kKinematics, kMaxVoltage));
	}
}
