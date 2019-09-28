package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.ShovelConfig;
import com.palyrobotics.frc2019.util.configv2.Configs;

public class Shovel extends Subsystem {

    private static Shovel sInstance = new Shovel();

    public static Shovel getInstance() {
        return sInstance;
    }

    private double mVictorOutput;

    private double mRumbleLength;

    private ShovelConfig mConfig = Configs.get(ShovelConfig.class);

    private boolean cachedHatchState;

    public enum WheelState {
        INTAKING, EXPELLING, IDLE, SMALL_EXPEL
    }

    public enum UpDownState {
        UP, DOWN
    }

    private WheelState mWheelState = WheelState.IDLE;
    private UpDownState mUpDownOutput = UpDownState.UP;

    protected Shovel() {
        super("shovel");
    }

    @Override
    public void start() {
        mWheelState = WheelState.IDLE;
        mUpDownOutput = UpDownState.UP;
    }

    @Override
    public void stop() {
        mWheelState = WheelState.IDLE;
        mUpDownOutput = UpDownState.UP;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mWheelState = commands.wantedShovelWheelState;
        mUpDownOutput = commands.wantedShovelUpDownState;
        commands.intakeHFX = robotState.hatchIntakeUp;
        commands.intakeHasHatch = robotState.hasHatch;

        switch (mWheelState) {
            case INTAKING:
                if (commands.customShovelSpeed) {
                    mVictorOutput = robotState.operatorXboxControllerInput.leftTrigger;
                } else {
                    mVictorOutput = mConfig.motorVelocity;
                }
                break;
            case EXPELLING:
                if (commands.customShovelSpeed) {
                    mVictorOutput = -robotState.operatorXboxControllerInput.leftTrigger;
                } else {
                    mVictorOutput = mConfig.expellingMotorVelocity;
                }
                break;
            case SMALL_EXPEL:
                mVictorOutput = mConfig.smallExpelMotorVelocity;
                break;
            case IDLE:
                mVictorOutput = 0;
                break;
        }

        switch (mUpDownOutput) {
            case UP:
                mUpDownOutput = UpDownState.UP;
                mVictorOutput = 0.0;
                break;
            case DOWN:
                mUpDownOutput = UpDownState.DOWN;
                break;
        }

        if (!cachedHatchState && cachedHatchState) { // TODO huh, this always false
            mRumbleLength = 0.25;
        } else if (mRumbleLength <= 0) {
            mRumbleLength = -1;
        }

        cachedHatchState = robotState.hasHatch;
    }

    public double getRumbleLength() {
        return mRumbleLength;
    }

    public void decreaseRumbleLength() {
        mRumbleLength -= OtherConstants.deltaTime;
    }

    public WheelState getWheelState() {
        return mWheelState;
    }

    public boolean getUpDownOutput() {
        return mUpDownOutput == UpDownState.UP;
    }

    public double getPercentOutput() {
        return mVictorOutput;
    }

    public String getStatus() {
        return String.format("Shovel Intake State: %s%nVictor Output: %s%nUp Down Output: %s%n", mWheelState, mVictorOutput, mUpDownOutput);
    }
}
