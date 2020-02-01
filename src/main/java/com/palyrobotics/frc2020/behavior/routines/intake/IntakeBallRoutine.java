package com.palyrobotics.frc2020.behavior.routines.intake;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class IntakeBallRoutine extends TimedRoutine {

	public IntakeBallRoutine() {
		super(3.0);
	}

	public IntakeBallRoutine(double duration) {
		super(duration);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.intakeWantedState = Intake.State.INTAKE;
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIntake);
	}
}
