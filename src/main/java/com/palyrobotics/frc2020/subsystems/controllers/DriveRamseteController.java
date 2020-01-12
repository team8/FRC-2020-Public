package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class DriveRamseteController extends Drive.DriveController {

    public static final double B = 2.0, ZETA = 0.7;

    private final RamseteController mController;
    private final Trajectory mTrajectory;
    private final Timer mTimer = new Timer();

    public DriveRamseteController(Trajectory trajectory) {
        mTrajectory = trajectory;
        mController = new RamseteController(B, ZETA);
        mTimer.reset();
        mTimer.start();
    }

    @Override
    public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
        Trajectory.State targetPose = mTrajectory.sample(mTimer.get());
        ChassisSpeeds speeds = mController.calculate(state.drivePose, targetPose);
        DifferentialDriveWheelSpeeds wheelSpeeds = DrivetrainConstants.kKinematics.toWheelSpeeds(speeds);
        mDriveSignal.leftOutput.setTargetSmartVelocity(wheelSpeeds.leftMetersPerSecond * 60.0, mDriveConfig.smartVelocityGains);
        mDriveSignal.rightOutput.setTargetSmartVelocity(wheelSpeeds.rightMetersPerSecond * 60.0, mDriveConfig.smartVelocityGains);
        CSVWriter.addData("targetLeftVelocity", wheelSpeeds.leftMetersPerSecond);
        CSVWriter.addData("targetRightVelocity", wheelSpeeds.rightMetersPerSecond);
        CSVWriter.addData("currentPoseX", state.drivePose.getTranslation().getX());
        CSVWriter.addData("currentPoseY", state.drivePose.getTranslation().getY());
        CSVWriter.addData("leftVelocity", state.driveLeftVelocity);
        CSVWriter.addData("rightVelocity", state.driveRightVelocity);
        CSVWriter.addData("targetPoseX", targetPose.poseMeters.getTranslation().getX());
        CSVWriter.addData("targetPoseY", targetPose.poseMeters.getTranslation().getY());
    }

    @Override
    public boolean onTarget() {
        return mTimer.get() > mTrajectory.getTotalTimeSeconds();
    }
}
