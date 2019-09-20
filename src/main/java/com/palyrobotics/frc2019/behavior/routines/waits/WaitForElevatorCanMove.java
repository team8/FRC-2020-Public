package com.palyrobotics.frc2019.behavior.routines.waits;

import com.palyrobotics.frc2019.config.configv2.IntakeConfig;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.configv2.Configs;

public class WaitForElevatorCanMove extends WaitRoutine {

    @Override
    public boolean isCompleted() {
        return robotState.intakeAngle <= (Configs.get(IntakeConfig.class).holdAngle + Configs.get(IntakeConfig.class).holdTolerance);
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{elevator};
    }

    @Override
    public String getName() {
        return "WaitForElevatorCanMove";
    }
}
