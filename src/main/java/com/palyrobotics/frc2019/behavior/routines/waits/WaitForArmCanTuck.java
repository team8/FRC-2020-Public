package com.palyrobotics.frc2019.behavior.routines.waits;

import com.palyrobotics.frc2019.config.Constants.ElevatorConstants;
import com.palyrobotics.frc2019.subsystems.Subsystem;

public class WaitForArmCanTuck extends WaitRoutine {
    @Override
    public boolean isCompleted() {
        return robotState.elevatorPosition < ElevatorConstants.secondStageCanStartMovingArm;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{intake};
    }

    @Override
    public String getName() {
        return "Can Tuck Routine";
    }
}
