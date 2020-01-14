package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

public class DriveTurnController extends Drive.DriveController {

    private double mTargetHeading;
    private ProfiledPIDController mController = new ProfiledPIDController(
            mDriveConfig.turnGains.p, mDriveConfig.turnGains.i, mDriveConfig.turnGains.d,
            new TrapezoidProfile.Constraints(mDriveConfig.turnGains.velocity, mDriveConfig.turnGains.acceleration)
    );

    public DriveTurnController(double targetHeading) {
        mTargetHeading = targetHeading;
    }

    @Override
    public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
        double targetVelocity = mController.calculate(state.driveHeading, mTargetHeading);
        mDriveSignal.leftOutput.setTargetVelocityProfiled(targetVelocity, mDriveConfig.smartVelocityGains);
        mDriveSignal.rightOutput.setTargetVelocityProfiled(-targetVelocity, mDriveConfig.smartVelocityGains);
    }
}
