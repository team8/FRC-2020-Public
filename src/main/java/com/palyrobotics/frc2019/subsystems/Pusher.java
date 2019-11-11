package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.dashboard.LiveGraph;
import com.palyrobotics.frc2019.config.subsystem.PusherConfig;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.config.Configs;
import edu.wpi.first.wpilibj.Timer;

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
        IN, OUT, START
    }

    private PusherState mState;

    @Override
    public void reset() {
        mOutput = new SparkMaxOutput();
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
                mOutput.setTargetPosition(mConfig.distanceIn, mConfig.positionGains);
                break;
            case IN:
                if (mConfig.useSlam) {
                    double currentTimeMs = Timer.getFPGATimestamp();
                    if (mSlamStartTimeMs == null) {
                        mSlamStartTimeMs = currentTimeMs;
                    }
                    boolean afterSlamTime = currentTimeMs - mSlamStartTimeMs > mConfig.slamTime;
                    if (afterSlamTime) {
                        if (mIsFirstTickForSlamResetEncoder) {
                            if (HardwareAdapter.getInstance().getPusher().resetSensors()) // Zero encoder since we assume to slam to in position
                                mIsFirstTickForSlamResetEncoder = false;
                        } else {
                            mOutput.setPercentOutput(-0.05);
                        }
                    } else if (robotState.pusherPosition > mConfig.distanceOut - 0.4) {
                        mOutput.setPercentOutput(-0.55); // Sticky at fully extended
                    } else {
                        mOutput.setPercentOutput(-0.28);
                    }
                } else {
                    mOutput.setTargetPosition(mConfig.distanceIn, mConfig.positionGains);
                }
                break;
            case OUT:
                double arbitraryDemand;
                if (robotState.pusherPosition < mConfig.distanceOut / 2.0) {
                    arbitraryDemand = 0.3;
                } else {
                    arbitraryDemand = 0.0;
                }
                mOutput.setTargetPosition(mConfig.distanceOut, arbitraryDemand, mConfig.positionGains);
                mIsFirstTickForSlamResetEncoder = true;
                mSlamStartTimeMs = null;
                break;
        }

//        LiveGraph.getInstance().add("pusher", robotState.pusherPosition);

//        CSVWriter.addData("pusherAppliedOut", robotState.pusherAppliedOutput);
//        CSVWriter.addData("pusherPos", robotState.pusherPosition);
//        CSVWriter.addData("pusherSetPoint", mOutput.getReference());
//        CSVWriter.addData("pusherVelocity", robotState.pusherVelocity);
//        CSVWriter.addData("pusherPosition", robotState.pusherPosition);
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
