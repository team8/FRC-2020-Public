package com.palyrobotics.frc2020.behavior.routines.intake;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutine;

public class IntakeBallRoutine extends TimeoutRoutine { // TODO implement class

    public IntakeBallRoutine() {
        super(3.0);
    }

    public IntakeBallRoutine(double waitTime) {
        super(waitTime);
    }

    @Override
    public boolean checkIfFinishedEarly() {
        return false;
    }
}
