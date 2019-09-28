package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Gains;
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

    private double mReference;

    private Double mSlamTime;

    private SparkMaxOutput mOutput = new SparkMaxOutput();

    public enum PusherState {
        IN, MIDDLE, OUT, COMPRESSION, SLAM
    }

    private PusherState mState = PusherState.IN;

    protected Pusher() {
        super("pusher");
    }

    @Override
    public void start() {
        mState = PusherState.IN;
    }

    @Override
    public void stop() {
        mState = PusherState.IN;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        commands.hasPusherCargo = robotState.hasPusherCargo;

        mState = commands.wantedPusherInOutState;
        switch (mState) {
            case SLAM:
                mReference = -0.33;
                if (mSlamTime == -1) {
                    mSlamTime = (double) System.currentTimeMillis();
                }
                mOutput.setPercentOutput((System.currentTimeMillis() - mSlamTime > 400) ? mReference / 5.3 : mReference);
                HardwareAdapter.getInstance().getPusher().resetSensors();
                break;
            case IN:
                mReference = mConfig.vidarDistanceIn;
                mSlamTime = null;
                mOutput.setTargetPosition(mReference, Gains.pusherPosition);
                break;
            case OUT:
                mReference = mConfig.vidarDistanceOut;
                mSlamTime = null;
                mOutput.setTargetPosition(mReference, Gains.pusherPosition);
                break;
        }

        CSVWriter.addData("pusherPos", robotState.pusherPosition);
        CSVWriter.addData("pusherReference", mReference);
        CSVWriter.addData("pusherEncVelocity", robotState.pusherEncVelocity);
        CSVWriter.addData("pusherPotPosition", robotState.pusherPosition);
        CSVWriter.addData("pusherPotPositionInches", robotState.pusherPosition / PusherConfig.kTicksPerInch);
        CSVWriter.addData("pusherPotVelocity", robotState.pusherVelocity * PusherConfig.kPusherPotSpeedUnitConversion);
    }

    public boolean onTarget() {
        return Math.abs((Robot.getRobotState().pusherPosition - mReference)) < mConfig.acceptablePositionError
                && Math.abs(Robot.getRobotState().pusherVelocity) < .5;
    }

    public PusherState getPusherState() {
        return mState;
    }

    public SparkMaxOutput getPusherOutput() {
        return mOutput;
    }

    @Override
    public String getStatus() {
        return "Pusher State: " + mState + "\nPusher output" + mOutput.getReference();
    }
}
