package com.palyrobotics.frc2020.behavior.routines;

import java.util.HashSet;
import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.Timer;

/**
 * Routine that waits the specified amount of time. Does not require any subsystems. The preferred
 * way to extend time based routines is to use {@link TimeoutRoutineBase} instead. Passing
 * {@link Double#POSITIVE_INFINITY} to the timeout can be used to achieve a persistent command.
 */
public class TimedRoutine extends RoutineBase {

	protected final Timer mTimer = new Timer();
	protected double mTimeout;

	/**
	 * @see TimedRoutine
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
