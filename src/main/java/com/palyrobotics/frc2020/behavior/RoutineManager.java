package com.palyrobotics.frc2020.behavior;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;

import java.util.*;

/**
 * Handles the updating of commands by passing them to each running routine. <br />
 *
 * @author Nihar, Ailyn
 */
public class RoutineManager {

    private static RoutineManager sInstance = new RoutineManager();

    public static RoutineManager getInstance() {
        return sInstance;
    }

    private List<Routine> mRunningRoutines = new LinkedList<>();

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

    public void clearRunningRoutines() {
        mRunningRoutines.clear();
    }

    /**
     * Updates the commands that are passed in based on the running routines.
     *
     * @param commands Current commands
     * @return Modified commands
     */
    public void update(Commands commands) {
        mRunningRoutines.removeIf(routine -> {
            boolean isFinished = routine.execute(commands);
            if (isFinished) {
                System.out.printf("Dropping finished routine: %s%n", routine);
            }
            return isFinished;
        });
        if (commands.shouldClearCurrentRoutines) {
            clearRunningRoutines();
        }
        for (Routine newRoutine : commands.routinesWanted) {
            //  Remove any running routines that conflict with new routine
            List<Routine> conflicts = conflictingRoutines(newRoutine);
            for (Routine routine : conflicts) {
                System.out.printf("Dropping conflicting routine: %s%n", routine);
                mRunningRoutines.remove(routine);
            }
            if (!newRoutine.execute(commands)) { // If it finishes immediately never add it to running routines
                mRunningRoutines.add(newRoutine);
            }
        }
        // Clears the wanted routines every update cycle
        commands.routinesWanted.clear();
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