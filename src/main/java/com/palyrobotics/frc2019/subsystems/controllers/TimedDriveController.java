package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Drive.DriveController;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;
import edu.wpi.first.wpilibj.Timer;

public class TimedDriveController implements DriveController {

    private double mVoltage, mTime, mStartTime;
    private SparkDriveSignal mSignal = new SparkDriveSignal();

    public TimedDriveController(double voltage, double time) {
        mVoltage = voltage;
        mTime = time;
        mStartTime = Timer.getFPGATimestamp();
    }

    @Override
    public boolean onTarget() {
        return Timer.getFPGATimestamp() > mStartTime + mTime;
    }

    @Override
    public SparkDriveSignal update(RobotState state) {
        mSignal.leftOutput.setPercentOutput(mVoltage);
        mSignal.rightOutput.setPercentOutput(mVoltage);
        return mSignal;
    }

    @Override
    public Pose getSetPoint() {
        return new Pose();
    }
}
