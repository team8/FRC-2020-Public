package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.constants.OtherConstants;
import com.palyrobotics.frc2019.config.subsystem.ElevatorConfig;
import com.palyrobotics.frc2019.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2019.util.config.Configs;

public class Shooter extends Subsystem {

    private static Shooter sInstance = new Shooter();

    public static Shooter getInstance() {
        return sInstance;
    }

    private ShooterConfig mConfig = Configs.get(ShooterConfig.class);

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
                mOutput = 0;
                mExpellingCycles = 0;
                break;
            case SPIN_UP:
                if (robotState.elevatorPosition > Configs.get(ElevatorConfig.class).elevatorHeight3 - 8.0) {
                    mOutput = mConfig.level3MotorVelocity;
                } else {
                    mOutput = mConfig.expellingMotorVelocity;
                }
                mExpellingCycles++;
                break;
        }

        // Once enough time passes, ready to expel
        boolean readyToExpel = 1 / OtherConstants.deltaTime <= mExpellingCycles;

        if (readyToExpel && robotState.hasIntakeCargo) { // Rumble until expelled
            mRumbleLength = 0.5;
        }

        if (mCachedHasCargo && !robotState.hasIntakeCargo) { // Stop rumbling once you go from cargo -> no cargo
            mRumbleLength = -1;
            mExpellingCycles = 0;
        }

        mCachedHasCargo = robotState.hasIntakeCargo;
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
