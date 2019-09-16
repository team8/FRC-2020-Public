package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.dashboard.DashboardManager;
import com.palyrobotics.frc2019.subsystems.Drive.DriveController;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.SparkSignal;

public class CascadingGyroEncoderTurnAngleController implements DriveController {

    private double mTargetHeading;
    private Pose mCachedPose;
    
    private double mTarget;
    private double mLastTarget;

    private SparkMaxOutput mLeftOutput;
    private SparkMaxOutput mRightOutput;
    
    //Error measurements for angle-to-velocity PID
    private double mErrorIntegral;
    private double mErrorDerivative;
    private double mLastError;

    public CascadingGyroEncoderTurnAngleController(Pose priorSetpoint, double angle) {
        mTargetHeading = priorSetpoint.heading + angle;
        mCachedPose = priorSetpoint;

        mLastTarget = 0;

        mLeftOutput = new SparkMaxOutput();
        mRightOutput = new SparkMaxOutput();
        
        mErrorIntegral = 0;

        mLastError = angle;
    }

    @Override
    public SparkSignal update(RobotState state) {

        mCachedPose = state.drivePose;
        
        if (mCachedPose == null) {
//        	Logger.getInstance().logSubsystemThread(Level.WARNING, "CascadingGyroEncoderTurnAngle", "Cached pose is null!");
        	return SparkSignal.getNeutralSignal();
        } else {
            double currentHeading = mCachedPose.heading;
            double error = mTargetHeading - currentHeading;

            if(Math.abs(error) < Gains.kVidarCascadingTurnIzone) {
                mErrorIntegral += error;
            } else {
                mErrorIntegral = 0.0;
            }

            mErrorDerivative = (error - mLastError) / DrivetrainConstants.kNormalLoopsDt;

//            Manually calculate PID output for velocity loop
            mTarget = (Gains.kVidarCascadingTurnkP * error + Gains.kVidarCascadingTurnkI * mErrorIntegral + Gains.kVidarCascadingTurnkD * mErrorDerivative);

            if((Math.abs(mTarget) - Math.abs(mLastTarget))/DrivetrainConstants.kNormalLoopsDt > (DrivetrainConstants.kPathFollowingMaxAccel+25)) {
                mTarget = mLastTarget + Math.signum(mTarget) * ((DrivetrainConstants.kPathFollowingMaxAccel+25) * DrivetrainConstants.kNormalLoopsDt);
            }

            mLastTarget = mTarget;


            DashboardManager.getInstance().updateCANTable("angle", Double.toString(mTarget));

            mLeftOutput.setTargetVelocity(-mTarget, Gains.vidarVelocity);
            mRightOutput.setTargetVelocity(mTarget, Gains.vidarVelocity);
             
            mLastError = error;

            return new SparkSignal(mLeftOutput, mRightOutput);
        }
    }

    @Override
    public Pose getSetpoint() {
        return null;
    }

    @Override
    public boolean onTarget() {
        if (mCachedPose == null) {
//        	Logger.getInstance().logSubsystemThread(Level.WARNING, "CascadingGyroEncoderTurnAngle", "Cached pose is null!");
        	return false;
        } else {
        	return Math.abs(mLastError) < DrivetrainConstants.kAcceptableTurnAngleError &&
        			Math.abs(mCachedPose.leftEncVelocity) < DrivetrainConstants.kAcceptableDriveVelocityError &&
        			Math.abs(mCachedPose.rightEncVelocity) < DrivetrainConstants.kAcceptableDriveVelocityError;
        }
        
    }
}
