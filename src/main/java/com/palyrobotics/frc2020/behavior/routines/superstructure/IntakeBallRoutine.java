package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IntakeBallRoutine extends TimedRoutine {

	public IntakeBallRoutine() {
		super(0.5);
	}

	public IntakeBallRoutine(double durationSeconds) {
		super(durationSeconds);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.intakeWantedState = Intake.State.INTAKE;
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.intakeWantedState = Intake.State.LOWER;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIntake);
	}
}
