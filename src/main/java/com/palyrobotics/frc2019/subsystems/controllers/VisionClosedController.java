package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;
import com.palyrobotics.frc2019.vision.Limelight;

/**
 * Turns drivetrain using the gyroscope and bang-bang control loop
 *
 * @author Robbie, Nihar
 */
public class VisionClosedController implements Drive.DriveController {

    private static final double
            MAX_ANGULAR_POWER = 0.6,
            DISTANCE_POW_CONST = 2.5 * Gains.kVidarTrajectorykV;

    private final Limelight mLimelight = Limelight.getInstance();

    private double mLastYawError;
    private long mLastTimeMs;

    private int mUpdateCyclesForward;

    private SparkDriveSignal mSignal = SparkDriveSignal.getNeutralSignal();

    @Override
    public SparkDriveSignal update(RobotState robotState) {
        double angularPower;
        if (mLimelight.isTargetFound()) {
            double kP, kD;

            if (robotState.gamePeriod == RobotState.GamePeriod.AUTO) {
                kD = 0.0;
                kP = 0.013;
            } else {
                kP = 0.03;
                kD = 0.009;
            }

            long
                    currentTime = System.currentTimeMillis(),
                    deltaSeconds = (currentTime - mLastTimeMs) / 1000L;
            double
                    yawError = mLimelight.getYawToTarget(),
                    yawErrorDerivative = (yawError - mLastYawError) / deltaSeconds;
            angularPower = -yawError * kP - yawErrorDerivative * kD;
            mLastYawError = yawError;
            mLastTimeMs = currentTime;
            // |angularPower| should be at most 0.6
            if (angularPower > MAX_ANGULAR_POWER) angularPower = MAX_ANGULAR_POWER;
            if (angularPower < -MAX_ANGULAR_POWER) angularPower = -MAX_ANGULAR_POWER;
        } else {
            SparkDriveSignal mSignal = SparkDriveSignal.getNeutralSignal();
            mSignal.leftOutput.setPercentOutput(DrivetrainConstants.kVisionLookingForTargetCreepPower);
            mSignal.rightOutput.setPercentOutput(DrivetrainConstants.kVisionLookingForTargetCreepPower);
            return mSignal;
        }

        double
                leftPower = getAdjustedDistancePower(),
                rightPower = getAdjustedDistancePower();

        angularPower *= -1;
        //angularPower *= mOldThrottle;
        leftPower *= (1 + angularPower);
        rightPower *= (1 - angularPower);

        if (leftPower > 1.0) {
            leftPower = 1.0;
        } else if (rightPower > 1.0) {
            rightPower = 1.0;
        } else if (leftPower < -1.0) {
            leftPower = -1.0;
        } else if (rightPower < -1.0) {
            rightPower = -1.0;
        }

        mSignal.leftOutput.setPercentOutput(leftPower);
        mSignal.rightOutput.setPercentOutput(rightPower);
        return mSignal;
    }

    @Override
    public Pose getSetpoint() {
        // TODO use default constructor?
        return new Pose(0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public boolean onTarget() {
        // Once the target is out of sight, we are on target (after 3 update cycles of just creeping forward
        if (!mLimelight.isTargetFound()) {
            mUpdateCyclesForward += 1;
        }
        return !mLimelight.isTargetFound() && (mUpdateCyclesForward > 3);
    }

    private double getAdjustedDistancePower() {
        return mLimelight.isTargetFound()
                ? mLimelight.getCorrectedEstimatedDistanceZ() * DISTANCE_POW_CONST
                : 0.0;
    }

}