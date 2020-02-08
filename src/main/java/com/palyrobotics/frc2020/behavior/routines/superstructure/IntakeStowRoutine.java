package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IntakeStowRoutine extends TimeoutRoutineBase {

	public IntakeStowRoutine() {
		super(2.0);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.intakeWantedState = Intake.State.STOW;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return !state.intakeIsExtended;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIntake);
	}
}
