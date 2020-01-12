package com.palyrobotics.frc2020.behavior;

import java.util.Arrays;
import java.util.List;

public abstract class MultipleRoutine extends Routine {

    protected final List<Routine> mRoutines;

    public MultipleRoutine(Routine... routines) {
        this(Arrays.asList(routines));
    }

    public MultipleRoutine(List<Routine> routines) {
        if (routines.size() < 2) {
            throw new IllegalArgumentException("Multiple routines should have at least two routines.");
        }
        mRoutines = routines;
    }

    @Override
    public String getName() {
        StringBuilder name = new StringBuilder(super.getName()).append(":");
        for (Routine routine : mRoutines) {
            name.append("\n").append("    ")
                    .append(routine).append(" ")
                    .append("[").append(routine.isFinished() ? "Finished" : "Running").append("]");
        }
        return name.toString();
    }
}
