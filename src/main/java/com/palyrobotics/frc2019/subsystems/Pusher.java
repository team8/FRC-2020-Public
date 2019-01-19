package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.robot.Robot;
import com.palyrobotics.frc2019.util.SynchronousPID;
import com.palyrobotics.frc2019.util.logger.LeveledString;

public class Pusher extends Subsystem {

    private static Pusher instance = new Pusher();

    public static Pusher getInstance() { return instance; }

    public static void resetInstance() { instance = new Pusher();}

    private double target;
    private final double kTolerance;
    private SynchronousPID pusherPID;

    private double mVictorOutput = pusherPID.calculate(Constants.kVidarPusherDistanceIn);

    public enum PusherState {
        IN, MIDDLE, OUT
    }

    private PusherState mState = PusherState.IN;

    protected Pusher(){
        super("Pusher");
        kTolerance = Constants.kAcceptablePusherPositionError;
        pusherPID = new SynchronousPID(Constants.kVidarPusherPositionkP, Constants.kVidarPusherPositionkI, Constants.kVidarPusherPositionkD);
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
        switch(mState) {
            case IN:
                target = Constants.kVidarPusherDistanceIn;
                mVictorOutput = pusherPID.calculate(Constants.kVidarPusherDistanceIn);
                break;
            case MIDDLE:
                target = Constants.kVidarPusherDistanceMiddle;
                mVictorOutput = pusherPID.calculate(Constants.kVidarPusherDistanceMiddle);
                break;
            case OUT:
                target = Constants.kVidarPusherDistanceOut;
                mVictorOutput = pusherPID.calculate(Constants.kVidarPusherDistanceOut);
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
