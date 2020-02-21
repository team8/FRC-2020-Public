package com.palyrobotics.frc2020.behavior.routines;

import java.util.HashSet;
import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.Timer;

public class TimedRoutine extends RoutineBase {

	protected final Timer mTimer = new Timer();
	protected double mTimeout;

	/**
	 * Routine that waits the specified amount of time. Does not require any subsystems.
	 */
	public TimedRoutine(double durationSeconds) {
		mTimeout = durationSeconds;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		mTimer.start();
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mTimer.hasElapsed(mTimeout);
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return new HashSet<>();
	}
}
