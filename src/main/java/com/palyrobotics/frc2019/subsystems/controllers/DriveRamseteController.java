package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.subsystem.DriveConfig;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;
import com.palyrobotics.frc2019.util.config.Configs;
import com.palyrobotics.frc2019.util.trajectory.RigidTransform2d;
import com.palyrobotics.frc2019.util.trajectory.Rotation2d;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class DriveRamseteController implements Drive.DriveController {

    private final RamseteController mController;
    private final Trajectory mTrajectory;
    private final double mStartTime;
    private final SparkDriveSignal mOutput;
    private final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);
    private final DifferentialDriveKinematics mKinematics = new DifferentialDriveKinematics(DrivetrainConstants.kTrackWidthMeters);

    public DriveRamseteController(Trajectory trajectory) {
        mTrajectory = trajectory;
        mController = new RamseteController(2.0, 0.7);
        mStartTime  = Timer.getFPGATimestamp();
        mOutput     = new SparkDriveSignal();
    }

    @Override
    public SparkDriveSignal update(RobotState state) {
        double timeSinceStart = Timer.getFPGATimestamp() - mStartTime;
        RigidTransform2d transform = state.getLatestFieldToVehicle().getValue();
        Translation2d translation = transform.getTranslation();
        Rotation2d rotation = transform.getRotation();
        Pose2d pose = new Pose2d( // TODO make our codebase use the WPILib classes now instead of rolling out custom ones, since we have to do this conversion manually at the moment
                new edu.wpi.first.wpilibj.geometry.Translation2d(translation.getX(), translation.getY()),
                new edu.wpi.first.wpilibj.geometry.Rotation2d(rotation.getRadians())
        );
        ChassisSpeeds speeds = mController.calculate(pose, mTrajectory.sample(timeSinceStart));
        DifferentialDriveWheelSpeeds wheelSpeeds = mKinematics.toWheelSpeeds(speeds);
        mOutput.leftOutput.setTargetVelocity(wheelSpeeds.leftMetersPerSecond, mDriveConfig.velocityGains);
        mOutput.rightOutput.setTargetVelocity(wheelSpeeds.rightMetersPerSecond, mDriveConfig.velocityGains);
        return mOutput;
    }

    @Override
    public Pose getSetPoint() {
        return null;
    }

    @Override
    public boolean onTarget() {
        return false;
    }
}
