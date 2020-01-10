package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.CheesyDriveController;
import com.palyrobotics.frc2020.util.SparkDriveSignal;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.SynchronousPID;
import com.palyrobotics.frc2020.vision.Limelight;

/**
 * Turns drivetrain using the gyroscope and bang-bang control loop
 *
 * @author Robbie, Nihar
 */
public class VisionClosedController extends CheesyDriveController {

    private static final double
            MAX_ANGULAR_POWER = 0.4, // 0.6
            DISTANCE_POW_CONST = 2 * Configs.get(DriveConfig.class).trajectoryGains.v;

    private final Limelight mLimelight = Limelight.getInstance();

    private int mUpdateCyclesForward;

    private SynchronousPID mPidController = new SynchronousPID(Configs.get(VisionConfig.class).gains);

    @Override
    public SparkDriveSignal update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
        double angularPower;
        if (mLimelight.isTargetFound()) {
            angularPower = mPidController.calculate(mLimelight.getYawToTarget());
            // |angularPower| should be at most 0.6
            if (angularPower > MAX_ANGULAR_POWER) angularPower = MAX_ANGULAR_POWER;
            if (angularPower < -MAX_ANGULAR_POWER) angularPower = -MAX_ANGULAR_POWER;

            if (Limelight.getInstance().getCorrectedEstimatedDistanceZ() < DrivetrainConstants.kVisionTargetThreshold) {
                RobotState.getInstance().atVisionTargetThreshold = true;
            }
        } else {
            if (!robotState.atVisionTargetThreshold) {
                return super.update(Commands.getInstance(), RobotState.getInstance());
            } else {
                SparkDriveSignal mSignal = new SparkDriveSignal();
                mSignal.leftOutput.setPercentOutput(DrivetrainConstants.kVisionLookingForTargetCreepPower);
                mSignal.rightOutput.setPercentOutput(DrivetrainConstants.kVisionLookingForTargetCreepPower);
                return mSignal;
            }
        }

        double
                leftOutput = getAdjustedDistancePower(),
                rightOutput = getAdjustedDistancePower();

        angularPower *= -1;
        //angularPower *= mOldThrottle;
        leftOutput *= (1 + angularPower);
        rightOutput *= (1 - angularPower);

        if (leftOutput > 1.0) {
            leftOutput = 1.0;
        } else if (rightOutput > 1.0) {
            rightOutput = 1.0;
        } else if (leftOutput < -1.0) {
            leftOutput = -1.0;
        } else if (rightOutput < -1.0) {
            rightOutput = -1.0;
        }

        mSignal.leftOutput.setPercentOutput(leftOutput);
        mSignal.rightOutput.setPercentOutput(rightOutput);
        return mSignal;
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
                ? Math.min(mLimelight.getCorrectedEstimatedDistanceZ() * DISTANCE_POW_CONST, 0.4)
                : 0.0;
    }

}