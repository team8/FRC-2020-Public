package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IntakeLowerRoutine extends TimeoutRoutineBase {

	public IntakeLowerRoutine() {
		super(2.0);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.intakeWantedState = Intake.State.LOWER;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIntake);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return state.intakeIsExtended;
	}
}
