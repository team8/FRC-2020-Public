package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.subsystem.FingerConfig;
import com.palyrobotics.frc2019.util.config.Configs;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Fingers extends Subsystem {

    private static Fingers sInstance = new Fingers();

    public static Fingers getInstance() {
        return sInstance;
    }

    private DoubleSolenoid.Value
            mOpenCloseValue = DoubleSolenoid.Value.kForward,
            mExpelValue = DoubleSolenoid.Value.kReverse;

    public enum FingersState {
        OPEN, CLOSE
    }

    public enum PushingState {
        EXPELLING, CLOSED
    }

    private FingersState mOpenCloseState = FingersState.CLOSE;
    private PushingState mExpelState = PushingState.CLOSED;

    protected Fingers() {
        super("fingers");
    }

    @Override
    public void reset() {
        mOpenCloseState = FingersState.CLOSE;
        mExpelState = PushingState.CLOSED;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mOpenCloseState = commands.wantedFingersOpenCloseState;
        mExpelState = commands.wantedFingersExpelState;
//        System.out.println(robotState.drivePose.heading);
        if (Math.abs(Math.abs(robotState.drivePose.heading % 360) - 180) < Configs.get(FingerConfig.class).angleLoadingStationTolerance) {
            // We are at the loading station
            if (mExpelState == PushingState.EXPELLING) {
                mExpelState = PushingState.CLOSED;
            }
        }

        switch (mOpenCloseState) {
            case OPEN:
                mOpenCloseValue = DoubleSolenoid.Value.kForward;
                break;
            case CLOSE:
                mOpenCloseValue = DoubleSolenoid.Value.kReverse;
                break;
        }

        switch (mExpelState) {
            case EXPELLING:
                mExpelValue = DoubleSolenoid.Value.kForward;
                break;
            case CLOSED:
                mExpelValue = DoubleSolenoid.Value.kReverse;
                break;
        }
    }

    public DoubleSolenoid.Value getOpenCloseOutput() {
        return mOpenCloseValue;
    }

    public DoubleSolenoid.Value getExpelOutput() {
        return mExpelValue;
    }

    @Override
    public String getStatus() {
        return String.format("Fingers State: %s%nExpel State: %s", mOpenCloseState, mExpelState);
    }
}
