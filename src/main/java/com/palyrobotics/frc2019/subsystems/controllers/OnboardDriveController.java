package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.Gains.TrajectoryGains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.revrobotics.ControlType;

/**
 * Used to run onboard position or velocity control of the drivetrain
 * This controller is intended to be used in a cascading manner, with a parent controller that generates realtime setpoints
 */
public class OnboardDriveController implements Drive.DriveController {
    private SparkDriveSignal mSignal;
    private RobotState mCachedState;
    private OnboardControlType mControlType;
    private TrajectoryGains mGains;

    private TrajectorySegment mLeftSetPoint;
    private TrajectorySegment mRightSetPoint;

    private double left_last_error = 0;
    private double right_last_error = 0;

    private boolean reset = true;

    public enum OnboardControlType {
        kPosition,
        kVelocity,
        kVelArbFF
    }

    OnboardDriveController(OnboardControlType controlType, TrajectoryGains gains) {
        //Use copy constructors and prevent the signal passed in from being modified externally
        mSignal = new SparkDriveSignal();
        mLeftSetPoint = new TrajectorySegment();
        mRightSetPoint = new TrajectorySegment();
        mGains = gains;
        mControlType = controlType;
    }

    @Override
    public SparkDriveSignal update(RobotState state) {
        mCachedState = state;
        switch (mControlType) {
            case kPosition:
                mSignal = getPositionOutput(state.drivePose);
                break;
            case kVelocity:
                mSignal = getVelocityOutput(state.drivePose);
                break;
            case kVelArbFF:
                mSignal = getArbFFOutput(state.drivePose);
                break;
            default:
                mSignal = new SparkDriveSignal();
                break;
        }

        return mSignal;
    }

    /**
     * Should only be called by a parent controller
     */
    void updateSetpoint(TrajectorySegment leftSetpoint, TrajectorySegment rightSetpoint, Object handle) throws IllegalAccessException {
        if (!(handle instanceof Drive.DriveController)) {
            throw new IllegalAccessException();
        }
        mLeftSetPoint = leftSetpoint;
        mRightSetPoint = rightSetpoint;
    }

    private SparkDriveSignal getPositionOutput(Pose drivePose) {
        double left_sp = mLeftSetPoint.pos;
        double left_pv = drivePose.leftEnc;
        double right_sp = mRightSetPoint.pos;
        double right_pv = drivePose.rightEnc;

        return updatePID(left_sp, left_pv, right_sp, right_pv);
    }

    private SparkDriveSignal getVelocityOutput(Pose drivePose) {
        double left_sp = mLeftSetPoint.vel;
        double left_pv = drivePose.leftEncVelocity;
        double right_sp = mRightSetPoint.vel;
        double right_pv = drivePose.rightEncVelocity;

        return updatePID(left_sp, left_pv, right_sp, right_pv);
    }

    private SparkDriveSignal getArbFFOutput(Pose drivePose) {
        SparkDriveSignal signal = updatePID(0, 0, 0, 0);
        signal.leftOutput.setTargetVelocity(mLeftSetPoint.vel, signal.leftOutput.getReference() * 12.0, new Gains(mGains.p, 0, mGains.d, 0, 0, 0));
        signal.rightOutput.setTargetVelocity(mRightSetPoint.vel, signal.rightOutput.getReference() * 12.0, new Gains(mGains.p, 0, mGains.d, 0, 0, 0));

        return signal;
    }

    /**
     * sp = setpoint, goal value
     * pv = process variable, actual value
     */
    private SparkDriveSignal updatePID(double left_sp, double left_pv, double right_sp, double right_pv) {

        //calculate error
        double left_error = left_sp - left_pv;
        double right_error = right_sp - right_pv;

        //calculate d_error
        double d_left_error = (left_error - left_last_error) / mLeftSetPoint.dt;
        double d_right_error = (right_error - right_last_error) / mRightSetPoint.dt;

        //ignore d_error on the first cycle
        if (reset) {
            d_left_error = 0;
            d_right_error = 0;
            reset = false;
        }

        //calculate output
        //output = kP * error + kD * d_error + kV * velocity + kA * accel + sign(velocity)*kS
        double left_output = mGains.p * left_error + mGains.d * d_left_error + mGains.v * mLeftSetPoint.vel + mGains.a * mLeftSetPoint.acc + Math.signum(mLeftSetPoint.vel) * mGains.s;
        double right_output = mGains.p * right_error + mGains.d * d_right_error + mGains.v * mRightSetPoint.vel + mGains.a * mRightSetPoint.acc + Math.signum(mRightSetPoint.vel) * mGains.s;

        //clamp output within [-1,1] because we don't have infinite power
        left_output = Math.min(Math.max(left_output, -1), 1);
        right_output = Math.min(Math.max(right_output, -1), 1);

        //deadband output within (-kS-0.02, kS+0.02) to avoid jitter with kS
        left_output = Math.abs(left_output) - mGains.s < 0.02 ? 0 : left_output;
        right_output = Math.abs(right_output) - mGains.s < 0.02 ? 0 : right_output;

        left_last_error = left_error;
        right_last_error = right_error;

        return new SparkDriveSignal(new SparkMaxOutput(null, ControlType.kDutyCycle, left_output), new SparkMaxOutput(null, ControlType.kDutyCycle, right_output));
    }

    @Override
    public Pose getSetPoint() {
        return new Pose(0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public boolean onTarget() {
		return mCachedState != null;
        //unimplemented, not used with cascading control
	}

    /**
     * Helper class to represent a setpoint
     */
    public static class TrajectorySegment {

        double pos, vel, acc, dt;

        TrajectorySegment() {
        }

        TrajectorySegment(double vel, double acc, double dt) {
            this(0, vel, acc, dt);
        }

        TrajectorySegment(double pos, double vel, double acc, double dt) {
            this.pos = pos;
            this.vel = vel;
            this.acc = acc;
            this.dt = dt;
        }

        public TrajectorySegment(TrajectorySegment to_copy) {
            pos = to_copy.pos;
            vel = to_copy.vel;
            acc = to_copy.acc;
            dt = to_copy.dt;
        }

        public String toString() {
            return "pos: " + pos + "; vel: " + vel + "; acc: " + acc + "; dt: " + dt;
        }
    }


}