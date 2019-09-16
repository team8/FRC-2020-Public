package com.palyrobotics.frc2019.behavior.routines.waits;

import com.palyrobotics.frc2019.config.Constants.IntakeConstants;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class WaitForElevatorCanMove extends WaitRoutine {

    @Override
    public boolean isCompleted() {

        return robotState.intakeAngle <= (IntakeConstants.kHoldingPosition + IntakeConstants.kHoldTolerance);
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
