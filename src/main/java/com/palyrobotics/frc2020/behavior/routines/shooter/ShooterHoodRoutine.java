package com.palyrobotics.frc2020.behavior.routines.shooter;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.subsystems.Shooter;
import com.palyrobotics.frc2020.subsystems.Subsystem;

public class ShooterHoodRoutine extends Routine {
    //todo: add start and finish
    Shooter.HoodState mHoodState;
    public ShooterHoodRoutine(Shooter.HoodState mHoodState) {
        super();
        this.mHoodState = mHoodState;
    }

    Routine horizontal;
    Routine vertical;

    @Override
    public void start() {

    }

    @Override
    public Commands update(Commands commands) {
        switch(mHoodState){
            case LOW:
                vertical = new VerticalShooterHoodRoutine(false);
                horizontal = new HorizontalShooterHoodRoutine(false);
                commands.addWantedRoutine(new SequentialRoutine(vertical, horizontal));
                break;
            case MEDIUM:
                //moves vertical one up first, waits, sends out the horizontal one, waits, pulls down the vertical one
                vertical = new VerticalShooterHoodRoutine(true);
                horizontal = new ParallelRoutine(new HorizontalShooterHoodRoutine(true), new TimeoutRoutine(0.3));
                Routine vertical2 = new ParallelRoutine(new TimeoutRoutine(0.3), new VerticalShooterHoodRoutine(false));
                commands.addWantedRoutine(new SequentialRoutine(vertical, horizontal, vertical2));
                break;
            case HIGH:
                vertical = new VerticalShooterHoodRoutine(true);
                horizontal = new HorizontalShooterHoodRoutine(false);
                commands.addWantedRoutine(new SequentialRoutine(vertical, horizontal));
                break;

        }
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        //retracts the horizontal one first, waits, then retracts the vertical one.
        horizontal = new HorizontalShooterHoodRoutine(true);
        Routine vertical2 = new ParallelRoutine(new TimeoutRoutine(0.3), new VerticalShooterHoodRoutine(false));
        commands.addWantedRoutine(new SequentialRoutine(horizontal, vertical2));
        return commands;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] {mShooter};
    }
}
