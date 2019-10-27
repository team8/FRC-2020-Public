package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;

/**
 * Turns drivetrain using the gyroscope and bang-bang control loop
 *
 * @author Robbie, Nihar
 */
public class BangBangTurnAngleController implements Drive.DriveController {

    private double mPower, mTargetHeading;
    private Pose mCachedPose;

    /**
     * @param currentPose Pass in the latest robot state
     * @param heading     Degrees relative to current state to turn
     */
    public BangBangTurnAngleController(Pose currentPose, double heading) {
        mPower = -DrivetrainConstants.kTurnInPlacePower;
        mCachedPose = currentPose;
        mTargetHeading = mCachedPose.heading + heading;
//		Logger.getInstance().logSubsystemThread(Level.INFO, "Starting Heading", this.mCachedPose.heading);
//		Logger.getInstance().logSubsystemThread(Level.INFO, "Target Heading", this.mTargetHeading);

    }

    @Override
    public SparkDriveSignal update(RobotState state) {
        if (onTarget()) {
            return new SparkDriveSignal();
        }
        mCachedPose = state.drivePose;
        SparkDriveSignal output = new SparkDriveSignal();
        if (Math.abs(mCachedPose.heading - mTargetHeading) < 35) {
            mPower *= .20;
        }
        if (mCachedPose.heading < mTargetHeading) {
            output.leftOutput.setPercentOutput(mPower);
            output.rightOutput.setPercentOutput(-mPower);
        } else {
            output.leftOutput.setPercentOutput(-mPower);
            output.rightOutput.setPercentOutput(mPower);
        }
        return output;
    }

    @Override
    public Pose getSetPoint() {
        mCachedPose.heading = mTargetHeading;
//        Pose setPoint = new Pose(0, 0, 0, 0, 0, 0, 0, 0);
        return mCachedPose;
    }

    @Override
    public boolean onTarget() {
        double tolerance = DrivetrainConstants.kAcceptableTurnAngleError;
        System.out.println(mCachedPose.heading - mTargetHeading);
        return Math.abs(mCachedPose.heading - mTargetHeading) < tolerance;
    }
}
