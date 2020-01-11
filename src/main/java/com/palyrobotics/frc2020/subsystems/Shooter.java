package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.util.SparkMaxOutput;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.SmartGains;
import com.revrobotics.SparkMax;
import edu.wpi.first.wpilibj.Spark;

public class Shooter extends Subsystem{
    private static Shooter sInstance = new Shooter();

    public static Shooter getInstance() {
        return sInstance;
    }

    private ShooterConfig mConfig = Configs.get(ShooterConfig.class);
    //yoinks json file values.

    private double mOutput;

    public enum ShooterState {
        IDLE, SHOOTING
    }

    private ShooterState mState = ShooterState.IDLE;

    protected Shooter() {
        super("shooter");
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        //given a wanted shooter state, set the wantedShooterState to something based on that state using a switch
        mState = commands.wantedShooterState;
        switch(mState){
            case IDLE:
                mOutput = 0;
                break;
            case SHOOTING:
                SparkMaxOutput shootOutput = new SparkMaxOutput();
                //sets up motion profile for the shooter in order to reach a speed.
                shootOutput.setTargetSmartVelocity(mConfig.maxPowerSpeed, mConfig.shooterGains);
                break;
        }
    }

    @Override
    public void reset() {
        mOutput = 0.0;
        mState = ShooterState.IDLE;
    }

    public double getOutput() {
        return mOutput;
    }
}
