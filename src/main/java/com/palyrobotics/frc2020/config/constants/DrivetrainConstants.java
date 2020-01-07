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

    private static DriveConfig kDriveConfig = Configs.get(DriveConfig.class);

    public static double kMaxVoltage = 12.0;

    static TrajectoryConfig getStandardTrajectoryConfig() {
        return new TrajectoryConfig(kDriveConfig.maxPathVelocityMetersPerSecond, kDriveConfig.maxPathAccelerationMetersPerSecondSquared)
                .setKinematics(kKinematics)
                .addConstraint(
                        new DifferentialDriveVoltageConstraint(
                                new SimpleMotorFeedforward(kS, kV, kA),
                                kKinematics,
                                kMaxVoltage));
    }

    /**
     * Path following constants
     */
    public static final double
            kDriveWheelDiameterInches = 6.0,
            kDriveWheelDiameterMeters = Units.inchesToMeters(kDriveWheelDiameterInches),
            kTrackWidthInches = 24.5,
            kTrackWidthMeters = Units.inchesToMeters(kTrackWidthInches);
    public static final DifferentialDriveKinematics kKinematics = new DifferentialDriveKinematics(DrivetrainConstants.kTrackWidthMeters);
    public static final double kS = 0.0, kV = 0.0, kA = 0.0; // TODO: fill in with characterization tool
    public static TrajectoryConfig
            kTrajectoryConfig = getStandardTrajectoryConfig(),
            kReverseTrajectoryConfig = getStandardTrajectoryConfig().setReversed(true);
    public static final Pose2d kPathFinishTolerance = new Pose2d(new Translation2d(0.2, 0.2), Rotation2d.fromDegrees(5.0));

    /*
     * Control loop constants for both robots
     */
    public static final double
            kTurnInPlacePower = .45, // For bang bang
            kVisionLookingForTargetCreepPower = 0.18,
            kDriveMaxClosedLoopOutput = 0.8,
            kVisionTargetThreshold = 5; // Threshold before target cannot be seen // TODO: change this threshold

    /**
     * Tolerances
     */
    public static final double
            kAcceptableDrivePositionError = 15,
            kAcceptableDriveVelocityError = 5,
            kAcceptableShortDrivePositionError = 1,
            kAcceptableShortDriveVelocityError = 3,
            kAcceptableTurnAngleError = 4,
            kAcceptableGyroZeroError = 3,
            kAcceptableEncoderZeroError = 50;

    /**
     * Unit Conversions
     */
    public static final double
            kDriveMetersPerRotation = 0.04677268475,
            kDriveMetersPerSecondPerRpm = kDriveMetersPerRotation;

    /**
     * Cheesy Drive Constants
     */
    public static double
            kDeadBand = 0.02,
            kCyclesUntilStop = 50;
}
