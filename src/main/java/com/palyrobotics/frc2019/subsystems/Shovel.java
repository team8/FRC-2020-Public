package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.RobotState;

public class Shovel extends Subsystem {
    public static Shovel instance = new Shovel();

    public static Shovel getInstance() {
        return instance;
    }

    public static void resetInstance() { instance = new Shovel(); }

    private double mVictorOutput;

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

        switch(mWheelState) {
            case INTAKING:
                if(commands.customShovelSpeed) {
                    mVictorOutput = robotState.operatorXboxControllerInput.leftTrigger;
                } else {
                    mVictorOutput = Constants.kShovelMotorVelocity;
                }
                break;
            case EXPELLING:
                if(commands.customShovelSpeed) {
                    mVictorOutput = -robotState.operatorXboxControllerInput.leftTrigger;
                } else {
                    mVictorOutput = Constants.kShovelExpellingMotorVelocity;
                }
                break;
            case SMALL_EXPEL:
                mVictorOutput = Constants.kShovelSmallExpelMotorVelocity;
                break;
            case IDLE:
                mVictorOutput = 0;
                break;
        }

        switch(mUpDownOutput) {
            case UP:
                mUpDownOutput = UpDownState.UP;
                break;
            case DOWN:
                mUpDownOutput = UpDownState.DOWN;
                break;
        }
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

    public double getVictorOutput() {
        return mVictorOutput;
    }

    public String getStatus() {
        return "Shovel Intake State: " + mWheelState + "\nVictor Output: " + mVictorOutput +
                "\nUp Down Output: " + mUpDownOutput + "\n";
    }


}
