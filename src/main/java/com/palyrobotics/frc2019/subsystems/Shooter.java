package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.util.TalonSRXOutput;

public class Shooter extends Subsystem{
    public static Shooter instance = new Shooter();

    public static Shooter getInstance() { return instance; }

    public static void resetInstance() { instance = new Shooter(); }

    private TalonSRXOutput mTalonOutput = new TalonSRXOutput();

    public enum ShooterState {
        EXPELLING, IDLE
    }

    private ShooterState mState = ShooterState.IDLE;

    protected Shooter() { super("Shooter"); }

    @Override
    public void start() {
        mState = ShooterState.IDLE;
    }

    @Override
    public void stop() {
        mState = ShooterState.IDLE;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mState = commands.wantedShooterState;

        switch(mState) {
            case IDLE:
                mTalonOutput.setPercentOutput(0);
                break;
            case EXPELLING:
                if (commands.customShooterSpeed) {
                    mTalonOutput.setPercentOutput(robotState.operatorXboxControllerInput.leftTrigger); //TODO: change control?
                } else {
                    mTalonOutput.setPercentOutput(Constants.kExpellingMotorVelocity);
                }
        }
    }

    public TalonSRXOutput getTalonOutput() { return mTalonOutput; }

    @Override
    public String getStatus() {
        return "Shooter State: " + mState + "\nTalon Output: " + mTalonOutput.getSetpoint();
    }
}
