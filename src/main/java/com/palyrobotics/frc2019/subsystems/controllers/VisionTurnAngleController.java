package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;
import com.palyrobotics.frc2019.util.SynchronousPID;
import com.palyrobotics.frc2019.vision.Limelight;

/**
 * Turns drivetrain using the gyroscope and bang-bang control loop
 *
 * @author Robbie, Nihar
 *
 */
public class VisionTurnAngleController implements Drive.DriveController {

    private double mPower;
    private double mTargetHeading;
    private Pose mCachedPose;
    private SynchronousPID pidController;

    /**
     * @param currentPose
     *            Pass in the latest robot state
     * @param
     */
    public VisionTurnAngleController(Pose currentPose) {
        this.mCachedPose = currentPose;
        Gains turnGains = new Gains(.017, 0, 0, 0, 200, 0);
        pidController = new SynchronousPID(turnGains.p, turnGains.i, turnGains.d, turnGains.iZone);
        pidController.setOutputRange(-1,1);
        pidController.setSetPoint(0);
        pidController.calculate(Limelight.getInstance().getYawToTarget());
    }

    @Override
    public SparkDriveSignal update(RobotState state) {
        if(this.onTarget()) {
            return new SparkDriveSignal();
        }

        mCachedPose = state.drivePose;
        double error = Limelight.getInstance().getYawToTarget();
        double turn = pidController.calculate(error);
        SparkDriveSignal output = new SparkDriveSignal();
        output.leftOutput.setPercentOutput(turn);
        output.rightOutput.setPercentOutput(-turn);

        return output;
    }

    @Override
    public Pose getSetPoint() {
        mCachedPose.heading = 0;
        Pose setpoint = new Pose(0, 0, 0, 0, 0, 0, 0, 0);
        return mCachedPose;
    }

    @Override
    public boolean onTarget() {
        return Math.abs(Limelight.getInstance().getYawToTarget()) < OtherConstants.kVisionAlignDistanceTolerance
                && Math.abs(mCachedPose.headingVelocity) < OtherConstants.kVisionAlignSpeedyTolerance;
    }

}