package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;

public class Intake extends Subsystem {

    public enum IntakeState {
        IDLE, INTAKE
    }

    private static Intake sInstance = new Intake();

    public static Intake getInstance() {
        return sInstance;
    }

    private IntakeConfig mConfig = Configs.get(IntakeConfig.class);

    private double mOutput;

    @Override
    public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
        IntakeState mState = commands.intakeWantedState;
        switch (mState) {
            case IDLE:
                mOutput = 0.0;
            case INTAKE:
                mOutput = mConfig.intakingVelocity;
        }
    }

    public double getOutput() {
        return mOutput;
    }
}
