package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.util.SparkMaxOutput;

public class Climber extends Subsystem{
    private static Climber sInstance = new Climber();

    private static Climber getInstance() {
        return sInstance;
    }

    private ClimberState mClimberState;
    private SparkMaxOutput mOutput;
    private ClimberConfig mConfig;
    private Double mWantedVelocity;
    private Double mWantedPosition;

    public enum ClimberState {
        CUSTOM_VELOCITY, IDLE
    }

    public Climber() {
        super("climber");
    }

    @Override
    public void start() {
        mClimberState = Climber.ClimberState.IDLE;
    }

    @Override
    public void reset() {
        mClimberState = ClimberState.IDLE;
        mWantedVelocity = null;
        mWantedPosition = null;
        mOutput = new SparkMaxOutput();
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mClimberState = commands.wantedClimberState;
        switch(mClimberState) {
            case CUSTOM_VELOCITY:

                break;
            case IDLE:
                mOutput.setIdle();
                break;
        }
    }


}
