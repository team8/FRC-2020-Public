package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.FingerConfig;
import com.palyrobotics.frc2019.util.configv2.Configs;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Fingers extends Subsystem {

    public static Fingers instance = new Fingers();

    public static Fingers getInstance() {
        return instance;
    }

    public static void resetInstance() {
        instance = new Fingers();
    }

    private DoubleSolenoid.Value mOpenCloseValue = DoubleSolenoid.Value.kForward;
    private DoubleSolenoid.Value mExpelValue = DoubleSolenoid.Value.kReverse;


    /**
     * State of Fingers. OPEN = fingers open, etc
     */
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
        return "Fingers State: " + mOpenCloseState + "\nExpel State: " + mExpelState;
    }
}
