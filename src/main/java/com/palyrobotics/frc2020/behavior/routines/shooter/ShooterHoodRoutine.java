package com.palyrobotics.frc2020.behavior.routines.shooter;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.subsystems.Shooter;
import com.palyrobotics.frc2020.subsystems.Subsystem;

public class ShooterHoodRoutine extends Routine {
    //todo: implement, add counter thing to time
    public ShooterHoodRoutine(Shooter.HoodState mHoodState) {
        super();
    }

    @Override
    public void start() {

    }

    @Override
    public Commands update(Commands commands) {
        return null;
    }

    @Override
    public Commands cancel(Commands commands) {
        return null;
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
