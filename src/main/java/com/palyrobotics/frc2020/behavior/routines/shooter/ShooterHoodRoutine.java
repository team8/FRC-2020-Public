package com.palyrobotics.frc2020.behavior.routines.shooter;

import com.palyrobotics.frc2020.behavior.OneTimeRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.subsystems.Shooter;
import com.palyrobotics.frc2020.subsystems.Subsystem;

public class ShooterHoodRoutine extends OneTimeRoutine {
    //todo: implement, add counter thing to time
    public ShooterHoodRoutine(Shooter.HoodState mHoodState) {
        super();
    }

    @Override
    protected Commands doOnce(Commands commands) {
        //todo: implement
        return null;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] {mShooter};
    }
}
