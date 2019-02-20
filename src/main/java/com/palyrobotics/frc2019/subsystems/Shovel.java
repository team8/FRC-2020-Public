package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.ShovelConstants;
import com.palyrobotics.frc2019.config.RobotState;

public class Shovel extends Subsystem {
    public static Shovel instance = new Shovel();

    public static Shovel getInstance() {
        return instance;
    }

    public static void resetInstance() { instance = new Shovel(); }

    private double mVictorOutput;

    private double mRumbleLength;

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
        super("Hatch Intake");
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
//        System.out.println("Stored robot state:" + robotState.hasHatch);

        switch(mWheelState) {
            case INTAKING:
                if(commands.customShovelSpeed) {
                    mVictorOutput = robotState.operatorXboxControllerInput.leftTrigger;
                } else {
                    mVictorOutput = ShovelConstants.kMotorVelocity;
                }
                break;
            case EXPELLING:
                if(commands.customShovelSpeed) {
                    mVictorOutput = -robotState.operatorXboxControllerInput.leftTrigger;
                } else {
                    mVictorOutput = ShovelConstants.kExpellingMotorVelocity;
                }
                break;
            case SMALL_EXPEL:
                mVictorOutput = ShovelConstants.kSmallExpelMotorVelocity;
                break;
            case IDLE:
                mVictorOutput = 0;
                break;
        }

        switch(mUpDownOutput) {
            case UP:
                mUpDownOutput = UpDownState.UP;
                mVictorOutput = 0.0;
                break;
            case DOWN:
                mUpDownOutput = UpDownState.DOWN;
                break;
        }

        if(!cachedHatchState && cachedHatchState) {
            mRumbleLength = 0.25;
        } else if(mRumbleLength <= 0) {
            mRumbleLength = -1;
        }

        cachedHatchState = robotState.hasHatch;
    }

    public double getRumbleLength() {
        return mRumbleLength;
    }

    public WheelState getWheelState() {
        return mWheelState;
    }

    public boolean getUpDownOutput() {
        if(mUpDownOutput == UpDownState.UP) {
            return true;
        } else {
            return false;
        }
    }

    public double getPercentOutput() {
        return mVictorOutput;
    }

    public String getStatus() {
        return "Shovel Intake State: " + mWheelState + "\nVictor Output: " + mVictorOutput +
                "\nUp Down Output: " + mUpDownOutput + "\n";
    }


}
