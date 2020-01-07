package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.SparkDriveSignal;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class DriveRamseteController implements Drive.DriveController {

    public static final double B = 2.0, ZETA = 0.7;

    private final RamseteController mController;
    private final Trajectory mTrajectory;
    private final SparkDriveSignal mOutput;
    private final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);
    private final Timer mTimer = new Timer();

    public DriveRamseteController(Trajectory trajectory) {
        mTrajectory = trajectory;
        mController = new RamseteController(B, ZETA);
        mController.setTolerance(DrivetrainConstants.kPathFinishTolerance);
        mTimer.reset();
        mTimer.start();
        mOutput = new SparkDriveSignal();
    }

    @Override
    public SparkDriveSignal update(RobotState state) {
        Trajectory.State targetPose = mTrajectory.sample(mTimer.get());
        ChassisSpeeds speeds = mController.calculate(state.drivePose, targetPose);
        DifferentialDriveWheelSpeeds wheelSpeeds = DrivetrainConstants.kKinematics.toWheelSpeeds(speeds);
        mOutput.leftOutput.setTargetSmartVelocity(wheelSpeeds.leftMetersPerSecond * 60.0, mDriveConfig.smartVelocityGains);
        mOutput.rightOutput.setTargetSmartVelocity(wheelSpeeds.rightMetersPerSecond * 60.0, mDriveConfig.smartVelocityGains);
        CSVWriter.addData("targetLeftVelocity", wheelSpeeds.leftMetersPerSecond);
        CSVWriter.addData("targetRightVelocity", wheelSpeeds.rightMetersPerSecond);
        CSVWriter.addData("currentPoseX", state.drivePose.getTranslation().getX());
        CSVWriter.addData("currentPoseY", state.drivePose.getTranslation().getY());
        CSVWriter.addData("leftVelocity", state.leftDriveVelocity);
        CSVWriter.addData("rightVelocity", state.rightDriveVelocity);
        CSVWriter.addData("targetPoseX", targetPose.poseMeters.getTranslation().getX());
        CSVWriter.addData("targetPoseY", targetPose.poseMeters.getTranslation().getY());
        return mOutput;
    }

    @Override
    public boolean onTarget() {
        return mController.atReference();
    }
}
