package com.palyrobotics.frc2020.behavior.routines.shooter;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutine;

public class ShootAllBallsRoutine extends TimeoutRoutine {

    public ShootAllBallsRoutine() {
        super(2.0); // TODO implement class
    }

    public ShootAllBallsRoutine(double timeout) {
        super(timeout);
    }

    @Override
    public boolean checkIfFinishedEarly() {
        return false;
    }
}
