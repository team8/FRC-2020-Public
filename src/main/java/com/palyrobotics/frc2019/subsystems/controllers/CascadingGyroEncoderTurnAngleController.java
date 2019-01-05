package com.palyrobotics.frc2018.subsystems.controllers;

import java.util.logging.Level;

import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.config.Gains;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.config.dashboard.DashboardManager;
import com.palyrobotics.frc2018.subsystems.Drive.DriveController;
import com.palyrobotics.frc2018.util.DriveSignal;
import com.palyrobotics.frc2018.util.Pose;
import com.palyrobotics.frc2018.util.TalonSRXOutput;
import com.palyrobotics.frc2018.util.logger.Logger;

public class CascadingGyroEncoderTurnAngleController implements DriveController {

    private double mTargetHeading;
    private Pose mCachedPose;
    
    private double mTarget;
    private double mLastTarget;

    private TalonSRXOutput mLeftOutput;
    private TalonSRXOutput mRightOutput;
    
    //Error measurements for angle-to-velocity PID
    private double mErrorIntegral;
    private double mErrorDerivative;
    private double mLastError;

    public CascadingGyroEncoderTurnAngleController(Pose priorSetpoint, double angle) {
        mTargetHeading = priorSetpoint.heading + angle;
        mCachedPose = priorSetpoint;

        mLastTarget = 0;

        mLeftOutput = new TalonSRXOutput();
        mRightOutput = new TalonSRXOutput();
        
        mErrorIntegral = 0;

        mLastError = angle;
    }

    @Override
    public DriveSignal update(RobotState state) {

        mCachedPose = state.drivePose;
        
        if (mCachedPose == null) {
        	Logger.getInstance().logSubsystemThread(Level.WARNING, "CascadingGyroEncoderTurnAngle", "Cached pose is null!");
        	return DriveSignal.getNeutralSignal();
        } else {
            double currentHeading = mCachedPose.heading;
            double error = mTargetHeading - currentHeading;

            if(Math.abs(error) < Gains.kForsetiCascadingTurnIzone) {
                mErrorIntegral += error;
            } else {
                mErrorIntegral = 0.0;
            }

            mErrorDerivative = (error - mLastError) / Constants.kNormalLoopsDt;

//            Manually calculate PID output for velocity loop
            mTarget = (Gains.kForsetiCascadingTurnkP * error + Gains.kForsetiCascadingTurnkI * mErrorIntegral + Gains.kForsetiCascadingTurnkD * mErrorDerivative);

            if((Math.abs(mTarget) - Math.abs(mLastTarget))/Constants.kNormalLoopsDt > (Constants.kPathFollowingMaxAccel+25) * Constants.kDriveSpeedUnitConversion) {
                System.out.println((Math.abs(mTarget) - Math.abs(mLastTarget))/Constants.kNormalLoopsDt);
                System.out.println(Constants.kPathFollowingMaxAccel * Constants.kDriveSpeedUnitConversion);
                mTarget = mLastTarget + Math.signum(mTarget) * ((Constants.kPathFollowingMaxAccel+25) * Constants.kDriveSpeedUnitConversion * Constants.kNormalLoopsDt);
            }

            mLastTarget = mTarget;

//            System.out.println("error: " + error);
//            System.out.println("P: " + (Gains.kForsetiCascadingTurnkP * error));
//            System.out.println("I: " + (Gains.kForsetiCascadingTurnkI * mErrorIntegral));
//            System.out.println("D: " + (Gains.kForsetiCascadingTurnkD * mErrorDerivative));

            DashboardManager.getInstance().updateCANTable("angle", Double.toString(mTarget));

            mLeftOutput.setVelocity(-mTarget, Gains.forsetiVelocity);
            mRightOutput.setVelocity(mTarget, Gains.forsetiVelocity);
             
            mLastError = error;

            return new DriveSignal(mLeftOutput, mRightOutput);
        }
    }

    @Override
    public Pose getSetpoint() {
        return null;
    }

    @Override
    public boolean onTarget() {
        if (mCachedPose == null) {
        	Logger.getInstance().logSubsystemThread(Level.WARNING, "CascadingGyroEncoderTurnAngle", "Cached pose is null!");
        	return false;
        } else {
        	return Math.abs(mLastError) < Constants.kAcceptableTurnAngleError &&
        			Math.abs(mCachedPose.leftEncVelocity) < Constants.kAcceptableDriveVelocityError &&
        			Math.abs(mCachedPose.rightEncVelocity) < Constants.kAcceptableDriveVelocityError;
        }
        
    }
}
