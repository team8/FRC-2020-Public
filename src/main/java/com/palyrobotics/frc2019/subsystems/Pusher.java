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

    private SparkMaxOutput mOutput = new SparkMaxOutput();

    public enum PusherState {
        IN, MIDDLE, OUT
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
            case IN:
                mOutput.setTargetPositionSmartMotion(mConfig.vidarDistanceIn);
                break;
            case OUT:
                mOutput.setTargetPositionSmartMotion(mConfig.vidarDistanceOut);
                break;
        }

        CSVWriter.addData("pusherOutput", HardwareAdapter.getInstance().getPusher().pusherSpark.getAppliedOutput());
        CSVWriter.addData("pusherPos", robotState.pusherPosition);
        CSVWriter.addData("pusherSetPoint", mOutput.getReference());
        CSVWriter.addData("pusherEncVelocity", robotState.pusherEncVelocity);
        CSVWriter.addData("pusherPotPosition", robotState.pusherPosition);
        CSVWriter.addData("pusherPotPositionInches", robotState.pusherPosition / PusherConfig.kTicksPerInch);
        CSVWriter.addData("pusherPotVelocity", robotState.pusherVelocity * PusherConfig.kPusherPotSpeedUnitConversion);
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
