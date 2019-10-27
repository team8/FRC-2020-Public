package com.palyrobotics.frc2019.behavior.routines.drive;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.trajectory.RigidTransform2d;
import edu.wpi.first.wpilibj.Timer;

public class DriveSensorResetRoutine extends Routine {

    private double mStartTime, mTimeout;

    public DriveSensorResetRoutine(double timeout) {
        mTimeout = timeout;
    }

    @Override
    public void start() {
        mStartTime = Timer.getFPGATimestamp();
        HardwareAdapter.getInstance().getDrivetrain().resetSensors();
        mRobotState.reset(0.0, new RigidTransform2d());
        mRobotState.drivePose.heading = 0.0;
        mRobotState.drivePose.leftEncoderPosition = 0.0;
        mRobotState.drivePose.rightEncoderPosition = 0.0;
        mRobotState.drivePose.lastHeading = 0.0;
        mRobotState.drivePose.lastLeftEncoderPosition = 0.0;
        mRobotState.drivePose.lastRightEncoderPosition = 0.0;
    }

    @Override
    public Commands update(Commands commands) {
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        return commands;
    }

    @Override
    public boolean isFinished() {
        if (Timer.getFPGATimestamp() - mStartTime > mTimeout) {
//			Logger.getInstance().logRobotThread(Level.WARNING, "Drive sensor reset routine timed out!");
            return true;
        } else return Math.abs(mDrive.getPose().leftEncoderPosition) <= DrivetrainConstants.kAcceptableEncoderZeroError
				&& Math.abs(mDrive.getPose().rightEncoderPosition) <= DrivetrainConstants.kAcceptableEncoderZeroError
				&& Math.abs(mDrive.getPose().heading) <= DrivetrainConstants.kAcceptableGyroZeroError;
	}

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mDrive};
    }

    @Override
    public String getName() {
        return "DriveSensorResetRoutine";
    }
}
