package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.PusherConstants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.SynchronousPID;

public class Pusher extends Subsystem {

    private static Pusher instance = new Pusher();

    public static Pusher getInstance() {
        return instance;
    }

    public static void resetInstance() {
        instance = new Pusher();
    }

    private double target;

    private SparkMaxOutput mOutput = new SparkMaxOutput();

    public enum PusherState {
        IN, MIDDLE, OUT, COMPRESSION, SLAM
    }

    private PusherState mState = PusherState.IN;

    protected Pusher(){
        super("Pusher");
    }

    @Override
    public void start(){
        mState = PusherState.IN;
    }

    @Override
    public void stop(){
        mState = PusherState.IN;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        commands.hasPusherCargo = robotState.hasPusherCargo;

        mState = commands.wantedPusherInOutState;
        switch (mState) {
            case SLAM:
                target = -.18;
                mOutput.setPercentOutput(target);
                HardwareAdapter.getInstance().getPusher().resetSensors();
                break;
            case IN:
                target = PusherConstants.kVidarDistanceIn;
                mOutput.setTargetPosition(target, Gains.pusherPosition);
                break;
            case OUT:
                target = PusherConstants.kVidarDistanceOut;
                mOutput.setTargetPosition(target, Gains.pusherPosition);
                break;
        }
        
        mWriter.addData("pusherPos", robotState.pusherPosition);
        mWriter.addData("pusherSetpoint", this.target);
        mWriter.addData("pusherEncVelocity", robotState.pusherEncVelocity);
        mWriter.addData("pusherPotPosition", robotState.pusherPosition);
        mWriter.addData("pusherPotPositionInches", robotState.pusherPosition / PusherConstants.kTicksPerInch);
        mWriter.addData("pusherPotVelocity", robotState.pusherVelocity * PusherConstants.kPusherPotSpeedUnitConversion);
    }

    public boolean onTarget() {
        return Math.abs((Robot.getRobotState().pusherPosition - target)) < PusherConstants.kAcceptablePositionError
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
        return "Pusher State: " + mState + "\nPusher output" + mOutput.getSetpoint();
    }
}
