package com.palyrobotics.frc2019.behavior;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.*;

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

    //Routines that are being run
    private ArrayList<Routine>
            mRunningRoutines = new ArrayList<>(),
            mRoutinesToRemove = new ArrayList<>(),
            mRoutinesToAdd = new ArrayList<>();

    /**
     * Stores the new routine to be added in next update cycle <br />
     * Will automatically cancel any existing routines with the same subsystems
     */
    public void addNewRoutine(Routine newRoutine) {
        mRoutinesToAdd.add(Objects.requireNonNull(newRoutine));
    }

    public ArrayList<Routine> getCurrentRoutines() {
        return mRunningRoutines;
    }

    /**
     * Wipes all current routines <br />
     * Pass in the commands so that routines can clean up
     */
    public Commands reset(Commands commands) {
        // Cancel all running routines
        if (mRunningRoutines.size() > 0) {
            for (Routine routine : mRunningRoutines) {
                commands = routine.cancel(commands);
            }
        }
        // Empty the routine buffers
        mRunningRoutines.clear();
        mRoutinesToAdd.clear();
        mRoutinesToRemove.clear();
        return commands;
    }

    /**
     * Updates the commands that are passed in based on the running and canceled routines
     *
     * @param commands Current commands
     * @return Modified commands
     */
    public Commands update(Commands commands) {
        mRoutinesToRemove.clear();
        // Update all running routines
        for (Routine routine : mRunningRoutines) {
            if (routine.isFinished()) {
                commands = routine.cancel(commands);
                mRoutinesToRemove.add(routine);
            } else {
                commands = routine.update(commands);
            }
        }

        // Remove routines that finished
        for (Routine routine : mRoutinesToRemove) {
            mRunningRoutines.remove(routine);
        }

        // Add newest routines after current routines may have finished, start them, and update them
        for (Routine newRoutine : mRoutinesToAdd) {
            // Combine running routines with new routine to check for shared subsystems
            ArrayList<Routine> conflicts = conflictingRoutines(mRunningRoutines, newRoutine);
            for (Routine routine : conflicts) {
                commands = routine.cancel(commands);
                mRunningRoutines.remove(routine);
            }
            newRoutine.start();
            commands = newRoutine.update(commands);
            mRunningRoutines.add(newRoutine);
        }

        mRoutinesToAdd.clear();

        if (commands.cancelCurrentRoutines) {
            commands = reset(commands);
        }

        // Add new routines this cycle.
        // Intentionally runs even if cancelCurrentRoutines is true, as these are new routines requested on the same cycle.
        for (Routine routine : commands.wantedRoutines) {
            addNewRoutine(routine);
        }
        // Clears the wanted routines every update cycle
        commands.wantedRoutines.clear();
        return commands;
    }

    /**
     * Finds all conflicting routines required by all of the routines
     *
     * @param routinesList Existing routines
     * @param newRoutine   The new routine
     * @return Array of routines that require subsystems the newRoutine needs
     */
    private ArrayList<Routine> conflictingRoutines(ArrayList<Routine> routinesList, Routine newRoutine) {
        // Get hash sets of required subsystems for existing routines
        ArrayList<HashSet<Subsystem>> routineSubsystemSets = new ArrayList<>();
        HashSet<Subsystem> subsystemsRequired = new HashSet<>(Arrays.asList(newRoutine.getRequiredSubsystems()));

        for (Routine routine : routinesList) {
            routineSubsystemSets.add(new HashSet<>(Arrays.asList(routine.getRequiredSubsystems())));
        }

        ArrayList<Routine> conflicts = new ArrayList<>();
        // Any existing routines that require the same subsystem are added to routine
        for (int j = 0; j < routinesList.size(); j++) {
            // Find intersection
            routineSubsystemSets.get(j).retainAll(subsystemsRequired);
            if (routineSubsystemSets.get(j).size() != 0) {
                conflicts.add(routinesList.get(j));
                // Move to next routine in the list
            }
        }
        return conflicts;
    }

    public static Subsystem[] subsystemSuperset(ArrayList<Routine> routines) {
        HashSet<Subsystem> superset = new HashSet<>();
        for (Routine routine : routines) {
            superset.addAll(Arrays.asList(routine.getRequiredSubsystems()));
        }
        return superset.toArray(new Subsystem[0]);
    }

    /**
     * Finds overlapping subsystems. Not optimized
     */
    static Subsystem[] sharedSubsystems(ArrayList<Routine> routines) {
        HashMap<Subsystem, Integer> counter = new HashMap<>();
        counter.put(null, 0); //for SampleRoutine
        counter.put(Drive.getInstance(), 0);
        counter.put(Elevator.getInstance(), 0);
        counter.put(Shooter.getInstance(), 0);
        counter.put(Fingers.getInstance(), 0);
        counter.put(Intake.getInstance(), 0);
        counter.put(Pusher.getsInstance(), 0);
        // Count the number of times each subsystem appears
        for (Routine routine : routines) {
            for (Subsystem subsystem : routine.getRequiredSubsystems()) {
                counter.put(subsystem, counter.get(subsystem) + 1);
            }
        }
        // Add all subsystems that appear multiple times to return list
        HashSet<Subsystem> conflicts = new HashSet<>();
        for (Subsystem subsystem : counter.keySet()) {
            if (counter.get(subsystem) > 1 && subsystem != null) {
                conflicts.add(subsystem);
            }
        }
        return conflicts.toArray(new Subsystem[0]);
    }
}