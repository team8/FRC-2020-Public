package com.palyrobotics.frc2018.behavior.routines.intake;

import com.palyrobotics.frc2018.behavior.Routine;
import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.subsystems.Intake;
import com.palyrobotics.frc2018.subsystems.Subsystem;

/**
 * @author Jason
 */
public class IntakeWheelRoutine extends Routine {

    private Intake.WheelState wantedWheelState;
    
    //How long the wheels spin for (seconds)
  	private double mTimeout;
  	
  	private long mStartTime;

    /**
     *
     * @param wantedWheelState the desired state
     * @param timeout how long (seconds) to run for
     */
    public IntakeWheelRoutine(Intake.WheelState wantedWheelState, double timeout) {
        this.wantedWheelState = wantedWheelState;
        mTimeout = timeout;
    }

    @Override
    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    @Override
    public Commands update(Commands commands) {

    	commands.wantedIntakingState = wantedWheelState;

        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
    	commands.wantedIntakingState = Intake.WheelState.IDLE;
        return commands;
    }

    @Override
    public boolean finished() {
        return System.currentTimeMillis() - mStartTime > mTimeout * 1000;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] { intake };
    }

    @Override
    public String getName() {
        return "IntakeWheelRoutine";
    }
}
