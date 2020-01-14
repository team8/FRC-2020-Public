package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.palyrobotics.frc2020.config.subsystem.SpinnerConfig;
import com.palyrobotics.frc2020.util.config.Configs;

public class Spinner extends Subsystem {

    private static Spinner sInstance = new Spinner();
    private static final SpinnerConfig mSpinnerConfig = Configs.get(SpinnerConfig.class);


    public static Spinner getInstance() {
        return sInstance;
    }
    
    private SpinnerState mSpinnerState;
    private double mOutput;
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
        mOutput = 0;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mSpinnerState = commands.wantedSpinnerState;
        mCurrentColor = robotState.closestColorString;
        switch (mSpinnerState) {
            case IDLE:
                mOutput = mSpinnerConfig.idleOutput;
                break;
            case SPIN:
                mOutput = mSpinnerConfig.rotationOutput;
                if(mColorPassedCount > mSpinnerConfig.rotationControlColorPassedCount) {
                    commands.wantedSpinnerState = SpinnerState.IDLE;
                    mColorPassedCount = 0;
                }
                else {
                    if (!mPreviousColor.equals(mCurrentColor)) {
                        mColorPassedCount++;
                    }
                }
                break;
            case TO_COLOR:
                mOutput = mSpinnerConfig.positionOutput;
                if (mCurrentColor.equals(robotState.gameData)) {
                    commands.wantedSpinnerState = SpinnerState.IDLE;
                }
                break;
        }
        mPreviousColor = mCurrentColor;
    }

    public double getOutput() {
        return mOutput;
    }
}
