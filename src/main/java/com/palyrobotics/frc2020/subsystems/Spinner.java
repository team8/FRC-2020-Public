package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.constants.SpinnerConstants;

public class Spinner extends Subsystem {

    private static Spinner sInstance = new Spinner();

    public static Spinner getInstance() {
        return sInstance;
    }
    
    private SpinnerState mSpinnerState;
    private double mOutput = 0;
    private String mCurrentColor;
    private String mPreviousColor;

    private int mColorPassedCount;

    public enum SpinnerState {
        TO_COLOR, SPIN, IDLE
    }

    public Spinner() {
        super("spinner");
    }

    @Override
    public void start() {
        mSpinnerState = Spinner.SpinnerState.IDLE;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mSpinnerState = commands.wantedSpinnerState;
        mCurrentColor = robotState.closestColorString;
        switch (mSpinnerState) {
            case IDLE:
                mOutput = SpinnerConstants.idleOutput;
                break;
            case SPIN:
                mOutput = SpinnerConstants.rotationOutput;
                if(mColorPassedCount > 30) {
                    mSpinnerState = SpinnerState.IDLE;
                    mColorPassedCount = 0;
                }
                else {
                    if (!mPreviousColor.equals(mCurrentColor)) {
                        mColorPassedCount++;
                    }
                }
                break;
            case TO_COLOR:
                mOutput = SpinnerConstants.positionOutput;
                if (mCurrentColor.equals(robotState.gameData)) {
                    mSpinnerState = SpinnerState.IDLE;
                }
                break;
        }
        mPreviousColor = mCurrentColor;
    }

    public double getOutput() {
        return mOutput;
    }
}
