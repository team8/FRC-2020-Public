package com.palyrobotics.frc2020.config.constants;

import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.util.config.Configs;

import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.util.Units;

public class DriveConstants {

	public static final double kMaxVoltage = 12.0;
	/**
	 * Path following constants
	 */
	public static final double kWheelDiameterInches = 6.0, kTrackWidthInches = 24.5,
			kTrackWidthMeters = Units.inchesToMeters(kTrackWidthInches);

	public static final DifferentialDriveKinematics kKinematics = new DifferentialDriveKinematics(
			DriveConstants.kTrackWidthMeters);
	public static final double kS = 0.145, kV = 2.59, kA = 0.484;
	public static final SimpleMotorFeedforward kFeedForward = new SimpleMotorFeedforward(kS, kV, kA);
	public static final DifferentialDriveVoltageConstraint kVoltageConstraints = new DifferentialDriveVoltageConstraint(
			kFeedForward, kKinematics, kMaxVoltage);

	/**
	 * Unit Conversions
	 */
	public static final double kDriveMetersPerRotation = 0.04677268475,
			kDriveMetersPerSecondPerRpm = kDriveMetersPerRotation;
	/**
	 * Cheesy Drive Constants
	 */
	public static final double kDeadBand = 0.04;

	private static DriveConfig kConfig = Configs.get(DriveConfig.class);

	private DriveConstants() {
	}

	/**
	 * @return Copy of the standard trajectory configuration. Can be modified
	 *         safely.
	 */
	public static TrajectoryConfig getStandardTrajectoryConfig() {
		return new TrajectoryConfig(kConfig.maxPathVelocityMetersPerSecond,
				kConfig.maxPathAccelerationMetersPerSecondSquared).setKinematics(kKinematics)
						.addConstraint(kVoltageConstraints);
	}

	public static double calculateTimeToFinishTurn(double currentHeadingDegrees, double targetHeadingDegrees) {
		return new TrapezoidProfile(
				new TrapezoidProfile.Constraints(kConfig.maxPathVelocityMetersPerSecond,
						kConfig.maxPathAccelerationMetersPerSecondSquared),
				new TrapezoidProfile.State(targetHeadingDegrees, 0.0),
				new TrapezoidProfile.State(currentHeadingDegrees, 0.0)).totalTime();
	}
}
