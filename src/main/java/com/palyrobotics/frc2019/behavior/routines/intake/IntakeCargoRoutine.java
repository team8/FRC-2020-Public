package com.palyrobotics.frc2019.behavior.routines.intake;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.subsystems.Intake;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.logging.Level;
public class IntakeCargoRoutine extends Routine{


    private double mTimeout;
    private long mStartTime;

    public IntakeCargoRoutine(double timeout){
        mTimeout = timeout;
    }

    @Override
    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedIntakingState = Intake.WheelState.SLOW_EXPELLING;

        if(robotState.hasCargo){
            commands.wantedIntakingState = Intake.WheelState.IDLE;
        } else if (commands.wantedIntakingState == Intake.WheelState.SLOW_EXPELLING && robotState.cargoDistance < Constants.kIntakeCloseRoutineCloseNow) {
            commands.wantedIntakingState = Intake.WheelState.FAST_EXPELLING;
        }

        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        return commands;
    }

    @Override
    public boolean finished() {
        if(robotState.hasCargo) {
            Logger.getInstance().logRobotThread(Level.INFO, "IntakeCargoRoutine finishing intake with cargo");
            return true;
        } else if(System.currentTimeMillis() - mStartTime > mTimeout * 1000) {
            Logger.getInstance().logRobotThread(Level.INFO, "IntakeCargoRoutine timeout", System.currentTimeMillis() - mStartTime);
            return true;
        }
        return false;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { intake };
    }

    @Override
    public String getName() {
        return "IntakeCargoRoutine";
    }

}