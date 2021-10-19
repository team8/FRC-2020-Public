package com.palyrobotics.frc2020.behavior.routines.superstructure;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.config.Configs;

public class IntakeBallRoutine extends TimeoutRoutineBase {

	private double mPercentOutput = 0;

	public IntakeBallRoutine(double durationSeconds) {
		super(durationSeconds);
		mPercentOutput = Configs.get(IntakeConfig.class).rollerPo;
	}

	public IntakeBallRoutine(double durationSeconds, double percentOutput) {
		super(durationSeconds);
		mPercentOutput = percentOutput;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return false;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.setIntakeRunning(mPercentOutput);
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.setIntakeRunning(0);
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mIntake);
	}
}
