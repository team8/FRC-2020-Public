package com.palyrobotics.frc2020.behavior;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.subsystems.Subsystem;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Runs routines one at a time.
 * Finishes when the last one is finished.
 */
public class SequentialRoutine extends MultipleRoutine {

    private final Iterator<Routine> mIterator = mRoutines.iterator();
    private Routine mRunningRoutine = mIterator.next();

    public SequentialRoutine(Routine... routines) {
        super(routines);
    }

    public SequentialRoutine(List<Routine> routines) {
        super(routines);
    }

    @Override
    public void start() {
        mRunningRoutine.start();
    }

    @Override
    public void update(Commands commands) {
        while (mRunningRoutine.execute(commands)) {
            if (!mIterator.hasNext()) {
                mRunningRoutine = null;
                break;
            }
            mRunningRoutine = mIterator.next();
        }
    }

    @Override
    public boolean checkFinished() {
        return mRunningRoutine == null;
    }

    @Override
    public Set<Subsystem> getRequiredSubsystems() {
        return RoutineManager.sharedSubsystems(mRoutines);
    }
}
