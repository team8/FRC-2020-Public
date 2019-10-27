package com.palyrobotics.frc2019.behavior;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.subsystems.Subsystem;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Nihar on 12/27/16.
 */
public class SequentialRoutine extends Routine {
    private final ArrayList<Routine> mRoutines;
    private int mRunningRoutineIndex;
    private boolean mIsDone;

    public SequentialRoutine(ArrayList<Routine> routines) {
        mRoutines = routines;
    }

    public SequentialRoutine(Routine... routines) {
        mRoutines = new ArrayList<>(Arrays.asList(routines));
    }

    @Override
    public void start() {
        mRoutines.get(mRunningRoutineIndex).start();
    }

    @Override
    public Commands update(Commands commands) {
        if (mIsDone) {
            return commands;
        }
        // Update the current routine
        commands = mRoutines.get(mRunningRoutineIndex).update(commands);
        // Keep moving to next routine if the current routine is finished
        while (mRoutines.get(mRunningRoutineIndex).isFinished()) {
            commands = mRoutines.get(mRunningRoutineIndex).cancel(commands);
            if (mRunningRoutineIndex <= mRoutines.size() - 1) {
                mRunningRoutineIndex++;
            }
            // If final routine is finished, don't update anything
            if (mRunningRoutineIndex > mRoutines.size() - 1) {
                mIsDone = true;
                break;
            }
            // Start the next routine
            mRoutines.get(mRunningRoutineIndex).start();
        }
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        // If not all routines finished, cancel the current routine. Otherwise everything is already finished.
        if (mRunningRoutineIndex < mRoutines.size()) {
            mRoutines.get(mRunningRoutineIndex).cancel(commands);
        }
        return commands;
    }

    @Override
    public boolean isFinished() {
        return mIsDone;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return RoutineManager.sharedSubsystems(mRoutines);
    }

    @Override
    public String getName() {
        StringBuilder name = new StringBuilder("SequentialRoutine of (");
        for (Routine routine : mRoutines) {
            name.append(routine.getName()).append(" ");
        }
        name.append(")");
        return name.toString();
    }
}
