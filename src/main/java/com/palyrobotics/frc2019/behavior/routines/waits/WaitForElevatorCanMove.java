package com.palyrobotics.frc2019.behavior.routines.waits;

import com.palyrobotics.frc2019.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.config.Configs;

public class WaitForElevatorCanMove extends WaitRoutine {

    @Override
    public boolean isCompleted() {
        return mRobotState.intakeAngle <= (Configs.get(IntakeConfig.class).holdAngle + Configs.get(IntakeConfig.class).holdTolerance);
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mElevator};
    }

    @Override
    public String getName() {
        return "WaitForElevatorCanMove";
    }
}
