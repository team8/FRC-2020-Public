package com.palyrobotics.frc2020.behavior;

import java.util.*;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.StringUtil;

/**
 * Handles the updating of commands by passing them to each running routine.
 *
 * @author Nihar, Ailyn
 */
public class RoutineManager {

	public static final String LOGGER_TAG = StringUtil.classToJsonName(RoutineManager.class);
	private static RoutineManager sInstance = new RoutineManager();
	private List<RoutineBase> mRunningRoutines = new LinkedList<>();

	private RoutineManager() {
	}

	public static RoutineManager getInstance() {
		return sInstance;
	}

	static Set<SubsystemBase> sharedSubsystems(List<RoutineBase> routines) {
		Set<SubsystemBase> sharedSubsystems = new HashSet<>(); // TODO: No allocation on update
		for (RoutineBase routine : routines) {
			sharedSubsystems.addAll(routine.getRequiredSubsystems());
		}
		return sharedSubsystems;
	}

	public List<RoutineBase> getCurrentRoutines() {
		return mRunningRoutines;
	}

	/**
	 * Updates the commands that are passed in based on the running routines.
	 *
	 * @param commands Current commands
	 * @param state
	 */
	public void update(Commands commands, @ReadOnly RobotState state) {
		mRunningRoutines.removeIf(routine -> {
			boolean isFinished = routine.execute(commands, state);
			if (isFinished) {
				Log.debug(LOGGER_TAG, String.format("Dropping finished routine: %s%n", routine));
			}
			return isFinished;
		});
		if (commands.shouldClearCurrentRoutines) {
			clearRunningRoutines();
		}
		for (RoutineBase newRoutine : commands.routinesWanted) {
			// Remove any running routines that conflict with new routine
			for (RoutineBase runningRoutine : mRunningRoutines) {
				// Non-disjoint means both subsystem sets have elements in common, which creates
				// conflicts
				if (!Collections.disjoint(newRoutine.getRequiredSubsystems(), runningRoutine.getRequiredSubsystems())) {
					Log.warn(LOGGER_TAG, String.format("Dropping conflicting routine: %s%n", runningRoutine));
					mRunningRoutines.remove(runningRoutine);
				}
			}
			// If it finishes immediately never add it to running routines
			if (newRoutine.execute(commands, state)) {
				Log.debug(LOGGER_TAG, String.format("Immediately dropping new routine: %s%n", newRoutine));
			} else {
				Log.debug(LOGGER_TAG, String.format("Adding routine: %s%n", newRoutine));
				mRunningRoutines.add(newRoutine);
			}
		}
		// Clears the wanted routines every update cycle
		commands.routinesWanted.clear();
	}

	public void clearRunningRoutines() {
		mRunningRoutines.clear();
	}
}
