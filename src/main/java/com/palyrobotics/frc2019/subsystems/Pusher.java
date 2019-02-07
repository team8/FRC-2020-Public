package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.PusherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.util.SynchronousPID;

public class Pusher extends Subsystem {

    private static Pusher instance = new Pusher();

    public static Pusher getInstance() { return instance; }

    public static void resetInstance() { instance = new Pusher();}

    private double target;
    private final double kTolerance;
    private SynchronousPID pusherPID;

    private double mVictorOutput = pusherPID.calculate(PusherConstants.kVidarDistanceIn);

    public enum PusherState {
        IN, MIDDLE, OUT
    }

    private PusherState mState = PusherState.IN;

    protected Pusher(){
        super("Pusher");
        kTolerance = PusherConstants.kAcceptablePositionError;
        pusherPID = new SynchronousPID(PusherConstants.kVidarPositionkP, PusherConstants.kVidarPositionkI, PusherConstants.kVidarPositionkD);
        pusherPID.setOutputRange(-1, 1);
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
    public void update(Commands commands, RobotState robotState){
        mState = commands.wantedPusherInOutState;
        pusherPID.setSetpoint(robotState.pusherPosition);
        commands.hasPusherCargo = robotState.hasPusherCargo;
        switch(mState) {
            case IN:
                target = PusherConstants.kVidarDistanceIn;
                mVictorOutput = pusherPID.calculate(PusherConstants.kVidarDistanceIn);
                break;
            case MIDDLE:
                target = PusherConstants.kVidarDistanceMiddle;
                mVictorOutput = pusherPID.calculate(PusherConstants.kVidarDistanceMiddle);
                break;
            case OUT:
                target = PusherConstants.kVidarDistanceOut;
                mVictorOutput = pusherPID.calculate(PusherConstants.kVidarDistanceOut);
                break;
        }
    }

    public boolean onTarget() {
        return Math.abs((Robot.getRobotState().pusherPosition - target)) < kTolerance
                && Math.abs(Robot.getRobotState().pusherVelocity) < 0.05;
    }

    public PusherState getPusherState() {
        return mState;
    }

    public double getPusherOutput() {
        return mVictorOutput;
    }
    @Override
    public String getStatus() {
        return "Pusher State: " + mState + "\nPusher output" + mVictorOutput;
    }
}
