package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2020.util.config.Configs;

public class Intake extends Subsystem {
    private static Intake sInstance = new Intake();
    private IntakeConfig mConfig = Configs.get(IntakeConfig.class);
    private double mOutput;
    private IntakeState mState;


    public Intake() {
        super ("intake");
    }

    public static Intake getInstance() {
        return sInstance;
    }

    public void reset() {
        mState = IntakeState.IDLE;
        mOutput = 0.0;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        switch (mState) {
            case IDLE:
                mOutput = 0.0;
            case INTAKING:
                mOutput = mConfig.intakingVelocity;
        }
    }

    public double getOutput() {
        return mOutput;
    }

    public enum IntakeState {
        IDLE, INTAKING
    }
}
