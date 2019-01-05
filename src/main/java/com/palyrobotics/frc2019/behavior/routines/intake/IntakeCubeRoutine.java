package com.palyrobotics.frc2019.behavior.routines.intake;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.subsystems.Intake;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.logging.Level;
public class IntakeCubeRoutine extends Routine{


    private double mTimeout;
    private long mStartTime;

    public IntakeCubeRoutine(double timeout){
        mTimeout = timeout;
    }

    @Override
    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedIntakeOpenCloseState = Intake.OpenCloseState.OPEN;
        commands.wantedIntakingState = Intake.WheelState.AUTO1;

        if(robotState.hasCube){
            commands.wantedIntakingState = Intake.WheelState.IDLE;
        } else if (commands.wantedIntakingState == Intake.WheelState.AUTO1 && robotState.cubeDistance < Constants.kIntakeCloseRoutineCloseNow) {
            commands.wantedIntakingState = Intake.WheelState.AUTO2;
            commands.wantedIntakeOpenCloseState = Intake.OpenCloseState.CLOSED;
        }

        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        return commands;
    }

    @Override
    public boolean finished() {
        if(robotState.hasCube) {
            Logger.getInstance().logRobotThread(Level.INFO, "IntakeCubeRoutine finishing intake with cube");
            return true;
        } else if(System.currentTimeMillis() - mStartTime > mTimeout * 1000) {
            Logger.getInstance().logRobotThread(Level.INFO, "IntakeCubeRoutine timeout", System.currentTimeMillis() - mStartTime);
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
        return "IntakeCubeRoutine";
    }

}
