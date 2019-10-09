package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.Constants.ShooterConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.ElevatorConfig;
import com.palyrobotics.frc2019.util.configv2.Configs;

public class Shooter extends Subsystem {

    private static Shooter sInstance = new Shooter();

    public static Shooter getInstance() {
        return sInstance;
    }

    private double mOutput;

    private double mRumbleLength;

    private boolean mCachedHasCargo;

    private double mExpellingCycles;

    public enum ShooterState {
        SPIN_UP,
        IDLE
    }

    private ShooterState mState = ShooterState.IDLE;

    protected Shooter() {
        super("shooter");
    }

    @Override
    public void reset() {
        mOutput = 0.0;
        mRumbleLength = -1;
        mCachedHasCargo = false;
        mExpellingCycles = 0;
        mState = ShooterState.IDLE;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mState = commands.wantedShooterState;

        switch (mState) {
            case IDLE:
                commands.shooterSpinning = false;
                mOutput = 0;
                mExpellingCycles = 0;
                break;
            case SPIN_UP:
                commands.shooterSpinning = true;
                if (commands.customShooterSpeed) {
                    mOutput = robotState.operatorXboxControllerInput.leftTrigger; //TODO: change control?
                } else {
                    if (robotState.elevatorPosition > Configs.get(ElevatorConfig.class).elevatorHeight3 - 8.0) {
                        mOutput = ShooterConstants.kLevel3MotorVelocity;
                    } else {
                        mOutput = ShooterConstants.kExpellingMotorVelocity;
                    }
                }
                mExpellingCycles++;
                break;
        }

        // Once enough time passes, ready to expel
        boolean readyToExpel = 1 / OtherConstants.deltaTime <= mExpellingCycles;

        if (readyToExpel && robotState.hasCargo) { // Rumble until expelled
            mRumbleLength = 0.5;
        }

        if (mCachedHasCargo && !robotState.hasCargo) { // Stop rumbling once you go from cargo -> no cargo
            mRumbleLength = -1;
            mExpellingCycles = 0;
        }

        mCachedHasCargo = robotState.hasCargo;
    }

    public double getRumbleLength() {
        return mRumbleLength;
    }

    public void decreaseRumbleLength() {
        mRumbleLength -= OtherConstants.deltaTime;
    }


    public double getOutput() {
        return mOutput;
    }

    @Override
    public String getStatus() {
        return String.format("Shooter State: %s%nVictor Output: %s", mState, mOutput);
    }
}
