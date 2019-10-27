package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.VisionClosedDriveRoutine;

import java.util.ArrayList;

@SuppressWarnings("Duplicates")

public class TestAuto extends AutoModeBase {

    @Override
    public String toString() {
        return sAlliance + this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        ArrayList<Routine> routines = new ArrayList<>();

        routines.add(new VisionClosedDriveRoutine());

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return sAlliance.toString();
    }
}