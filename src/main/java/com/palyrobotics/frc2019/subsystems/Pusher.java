package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.PusherConfig;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.configv2.Configs;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;

public class Pusher extends Subsystem {

    private static Pusher sInstance = new Pusher();

    public static Pusher getsInstance() {
        return sInstance;
    }

    private PusherConfig mConfig = Configs.get(PusherConfig.class);

    private Double mSlamStartTimeMs;

    private SparkMaxOutput mOutput;
    private boolean mIsFirstTickForSlamResetEncoder = true;

    public enum PusherState {
        IN, MIDDLE, OUT, START
    }

    private PusherState mState;

    @Override
    public void reset() {
        mOutput = SparkMaxOutput.getIdle();
        mSlamStartTimeMs = null;
        mState = PusherState.START;
        mIsFirstTickForSlamResetEncoder = true;
    }

    protected Pusher() {
        super("pusher");
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        commands.hasPusherCargo = robotState.hasPusherCargo;

        mState = commands.wantedPusherInOutState;
        switch (mState) {
            case START:
                mOutput.setTargetPositionSmartMotion(mConfig.vidarDistanceIn);
                break;
            case IN:
                if (mConfig.useSlam) {
                    long currentTimeMs = System.currentTimeMillis();
                    if (mSlamStartTimeMs == null) {
                        mSlamStartTimeMs = (double) currentTimeMs;
                    }
                    double percentOutput = mConfig.slamPercentOutput;
                    boolean afterSlamTime = currentTimeMs - mSlamStartTimeMs > mConfig.slamTimeMs;
                    if (afterSlamTime) {
                        if (mIsFirstTickForSlamResetEncoder) {
                            if (HardwareAdapter.getInstance().getPusher().resetSensors()) // Zero encoder since we assume to slam to in position
                                mIsFirstTickForSlamResetEncoder = false;
                        } else {
                            mOutput.setPercentOutput(-0.05);
                        }
                    } else if (robotState.pusherPosition > mConfig.vidarDistanceOut - 0.4) {
                        mOutput.setPercentOutput(-0.55);
                    } else {
                        mOutput.setPercentOutput(-0.28);
                    }
                } else {
                    mOutput.setTargetPositionSmartMotion(mConfig.vidarDistanceIn);
                }
                break;
            case OUT:
                if (robotState.hasPusherCargo) {
                    double arbitraryDemand;
                    if (robotState.pusherPosition < mConfig.vidarDistanceOut / 4.0) {
                        arbitraryDemand =  robotState.pusherPosition * 0.08;
                    } else if (robotState.pusherPosition < mConfig.vidarDistanceOut / 2.0) {
                        arbitraryDemand = (robotState.pusherPosition - mConfig.vidarDistanceOut / 2.0) * -0.08;
                    } else {
                        arbitraryDemand = 0.0;
                    }
                    arbitraryDemand = Math.max(Math.min(arbitraryDemand, 0.3), 0.0); // Clamp addition percent output between [0.0, 0.3]
                    CSVWriter.addData("pusherArbFF", arbitraryDemand);
                    mOutput.setTargetPositionSmartMotion(mConfig.vidarDistanceOut, arbitraryDemand);
                } else {
                    mOutput.setTargetPositionSmartMotion(mConfig.vidarDistanceOut);
                }
                mIsFirstTickForSlamResetEncoder = true;
                mSlamStartTimeMs = null;
                break;
        }

        CSVWriter.addData("pusherOutput", HardwareAdapter.getInstance().getPusher().pusherSpark.getAppliedOutput());
        CSVWriter.addData("pusherPos", robotState.pusherPosition);
        CSVWriter.addData("pusherSetPoint", mOutput.getReference());
        CSVWriter.addData("pusherVelocity", robotState.pusherVelocity);
        CSVWriter.addData("pusherPosition", robotState.pusherPosition);
    }

    public boolean onTarget() {
        return Math.abs((Robot.getRobotState().pusherPosition - mOutput.getReference())) < mConfig.acceptablePositionError
                && Math.abs(Robot.getRobotState().pusherVelocity) < mConfig.acceptablePositionError;
    }

    public PusherState getPusherState() {
        return mState;
    }

    public SparkMaxOutput getPusherOutput() {
        return mOutput;
    }

    @Override
    public String getStatus() {
        return String.format("Pusher State: %s%nPusher output%s", mState, mOutput.getReference());
    }
}
