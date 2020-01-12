package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.SparkDriveSignal;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;

public class DriveTurnController extends Drive.DriveController {

    private double mTargetHeading;
    private ProfiledPIDController mController = new ProfiledPIDController();

    public DriveTurnController(double targetHeading) {
        mTargetHeading = targetHeading;
    }

    @Override
    public SparkDriveSignal update(Commands commands, RobotState state) {
        double targetVelocity = mController.calculate(state.driveHeading, mTargetHeading);
        mSignal.leftOutput.setTargetSmartVelocity(targetVelocity, mDriveConfig.smartVelocityGains);
        mSignal.rightOutput.setTargetSmartVelocity(-targetVelocity, mDriveConfig.smartVelocityGains);
        return mSignal;
    }
}
