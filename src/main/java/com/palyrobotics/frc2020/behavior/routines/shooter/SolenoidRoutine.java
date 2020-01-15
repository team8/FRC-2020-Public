package com.palyrobotics.frc2020.behavior.routines.shooter;

import com.palyrobotics.frc2020.behavior.OneTimeRoutine;
import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Shooter;
import com.palyrobotics.frc2020.subsystems.Subsystem;

public abstract class SolenoidRoutine extends OneTimeRoutine {

    boolean solenoidOutput;

    public SolenoidRoutine(boolean solenoidOutput){
        this.solenoidOutput = solenoidOutput;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[] {mShooter};
    }

}
