package com.palyrobotics.frc2020.behavior;

import java.util.*;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import com.palyrobotics.frc2020.util.StringUtil;

/**
* Handles the updating of commands by passing them to each running routine.
* <br>
*
* @author Nihar, Ailyn
*/
public class RoutineManager {

	public static final String LOGGER_TAG = StringUtil.classToJsonName(RoutineManager.class);
	private static RoutineManager sInstance = new RoutineManager();
	private List<Routine> mRunningRoutines = new LinkedList<>();

	private RoutineManager() {
	}

	public static RoutineManager getInstance() {
		return sInstance;
	}

	static Set<Subsystem> sharedSubsystems(List<Routine> routines) {
		Set<Subsystem> sharedSubsystems = new HashSet<>(); // TODO: No allocation on update
		for (Routine routine : routines) {
			sharedSubsystems.addAll(routine.getRequiredSubsystems());
		}
		return sharedSubsystems;
	}

	public List<Routine> getCurrentRoutines() {
		return mRunningRoutines;
	}

	/**
	* Updates the commands that are passed in based on the running routines.
	*
	* @param commands Current commands
	*/
	public void update(Commands commands) {
		mRunningRoutines.removeIf(routine -> {
			boolean isFinished = routine.execute(commands);
			if (isFinished) {
				Log.debug(LOGGER_TAG, String.format("Dropping finished routine: %s%n", routine));
			}
			return isFinished;
		});
		if (commands.shouldClearCurrentRoutines) {
			clearRunningRoutines();
		}
		for (Routine newRoutine : commands.routinesWanted) {
			// Remove any running routines that conflict with new routine
			List<Routine> conflicts = conflictingRoutines(newRoutine);
			for (Routine routine : conflicts) {
				Log.warn(LOGGER_TAG, String.format("Dropping conflicting routine: %s%n", routine));
				mRunningRoutines.remove(routine);
			}
			if (!newRoutine.execute(commands)) { // If it finishes immediately never add it to running routines
				mRunningRoutines.add(newRoutine);
			}
		}
		// Clears the wanted routines every update cycle
		commands.routinesWanted.clear();
	}

	public void clearRunningRoutines() {
		mRunningRoutines.clear();
	}

	/**
	* Finds all conflicting routines required by all of the routines.
	*/
	private List<Routine> conflictingRoutines(Routine newRoutine) {
		List<Routine> conflicts = new ArrayList<>(); // TODO: No allocation on update
		for (Routine runningRoutine : mRunningRoutines) {
			if (!Collections.disjoint(newRoutine.getRequiredSubsystems(), runningRoutine.getRequiredSubsystems())) {
				conflicts.add(runningRoutine);
			}
		}
		return conflicts;
	}
}
