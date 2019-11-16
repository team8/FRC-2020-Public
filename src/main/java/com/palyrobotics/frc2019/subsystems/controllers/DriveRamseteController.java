package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.subsystem.DriveConfig;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.config.Configs;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class DriveRamseteController implements Drive.DriveController {

    private final RamseteController mController;
    private final Trajectory mTrajectory;
    private final double mStartTime;
    private final SparkDriveSignal mOutput;
    private final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);

    public DriveRamseteController(Trajectory trajectory) {
        mTrajectory = trajectory;
        mController = new RamseteController(2.0, 0.7);
        mStartTime  = Timer.getFPGATimestamp();
        mOutput     = new SparkDriveSignal();
    }

    @Override
    public SparkDriveSignal update(RobotState state) {
        double timeSinceStart = Timer.getFPGATimestamp() - mStartTime;
        Pose2d pose = new Pose2d(
                state.drivePose.leftEncoderPosition, state.drivePose.rightEncoderPosition,
                new Rotation2d(state.drivePose.heading)
        );
        ChassisSpeeds speeds = mController.calculate(pose, mTrajectory.sample(timeSinceStart));
        mOutput.leftOutput.setTargetVelocity(speeds.vxMetersPerSecond, mDriveConfig.velocityGains);
        mOutput.rightOutput.setTargetVelocity(speeds.vyMetersPerSecond, mDriveConfig.velocityGains);
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
