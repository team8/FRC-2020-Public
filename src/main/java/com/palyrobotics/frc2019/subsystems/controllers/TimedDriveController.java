package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Drive.DriveController;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;

public class TimedDriveController implements DriveController {

    private double mVoltage, mTime, mStartTime;
    private SparkDriveSignal mSignal = new SparkDriveSignal();

    public TimedDriveController(double voltage, double time) {
        mVoltage = voltage;
        mTime = time;
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public boolean onTarget() {
        return System.currentTimeMillis() > mStartTime + mTime * 1000;
    }

    @Override
    public SparkDriveSignal update(RobotState state) {
        mSignal.leftOutput.setPercentOutput(mVoltage);
        mSignal.rightOutput.setPercentOutput(mVoltage);
        return mSignal;
    }

    @Override
    public Pose getSetPoint() {
        return new Pose(0, 0, 0, 0, 0, 0, 0, 0);
    }
}
