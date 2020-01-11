package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.util.SparkMaxOutput;
import com.palyrobotics.frc2020.util.config.Configs;

public class Climber extends Subsystem{
    private static Climber sInstance = new Climber();

    public static Climber getInstance() {
        return sInstance;
    }

    private ClimberState mClimberState;
    private SparkMaxOutput mOutput;
    private ClimberConfig mConfig = Configs.get(ClimberConfig.class);
    private Double mWantedVelocity;
    private Double mWantedPosition;
    private RobotState mRobotState;

    public enum ClimberState {
        CUSTOM_VELOCITY, CUSTOM_POSITION, IDLE
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
        mOutput = new SparkMaxOutput();
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mClimberState = commands.wantedClimberState;
        switch(mClimberState) {
            case CUSTOM_VELOCITY:
                mWantedVelocity = commands.climberCustomVelocity;
                mOutput.setTargetSmartVelocity(mWantedVelocity, mConfig.gravityFeedForward, mConfig.gains);
                break;
            case CUSTOM_POSITION: //TODO: check
                mWantedPosition = commands.robotSetPoints.climberPositionSetPoint;
                mOutput.setTargetPositionSmartMotion(mWantedPosition, mConfig.gravityFeedForward, mConfig.gains);
            case IDLE:
                mOutput.setIdle();
                break;
        }
    }

    public boolean climberOnTarget() {
        return mClimberState == ClimberState.CUSTOM_POSITION &&
                Math.abs(mWantedPosition - mRobotState.climberPosition) < mConfig.acceptablePositionError &&
                Math.abs(mRobotState.climberVelocity) < mConfig.acceptableVelocityError;
    }

    public SparkMaxOutput getOutput() {
        return mOutput;
    }
}
