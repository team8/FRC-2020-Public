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
	public static final double kWheelDiameterInches = 6.0,
			kTrackWidthMeters = 0.67160389; // Tuned 2/12/20

	public static final DifferentialDriveKinematics kKinematics = new DifferentialDriveKinematics(DriveConstants.kTrackWidthMeters);
	public static final double kS = 0.298, kV = 2.78, kA = 0.404; // Tuned 2/12/20
	public static final SimpleMotorFeedforward kFeedForward = new SimpleMotorFeedforward(kS, kV, kA);
	public static final DifferentialDriveVoltageConstraint kVoltageConstraints = new DifferentialDriveVoltageConstraint(kFeedForward, kKinematics, kMaxVoltage);

	/**
	 * Unit Conversions
	 */
	public static final double kDriveMetersPerTick = (1.0 / 2048.0) * (1.0 / 12.34567901) * Units.inchesToMeters(kWheelDiameterInches) * Math.PI,
			kDriveMetersPerSecondPerTickPer100Ms = kDriveMetersPerTick * 10;
	/**
	 * Cheesy Drive Constants
	 */
	public static final double kDeadBand = 0.05;

	private static DriveConfig kConfig = Configs.get(DriveConfig.class);

	private DriveConstants() {
	}

	/**
	 * @return Copy of the standard trajectory configuration. Can be modified safely.
	 */
	public static TrajectoryConfig getTrajectoryConfig(double maxPathVelocityMetersPerSecond, double maxPathAccelerationMetersPerSecondSquared) {
		return new TrajectoryConfig(maxPathVelocityMetersPerSecond, maxPathAccelerationMetersPerSecondSquared)
				.setKinematics(kKinematics)
				.addConstraint(kVoltageConstraints);
	}

	public static double calculateTimeToFinishTurn(double currentYawDegrees, double targetYawDegrees) {
		return new TrapezoidProfile(
				new TrapezoidProfile.Constraints(kConfig.turnGains.velocity, kConfig.turnGains.acceleration),
				new TrapezoidProfile.State(targetYawDegrees, 0.0), new TrapezoidProfile.State(currentYawDegrees, 0.0)).totalTime();
	}
}
