package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveTimeRoutine;
import com.palyrobotics.frc2019.util.SparkDriveSignal;
import com.palyrobotics.frc2019.util.SparkMaxOutput;

import java.util.ArrayList;

public class ReZeroSubAutoMode extends AutoModeBase {

    @Override
    public String toString() {
        return sAlliance + this.getClass().toString();
    }

    @Override
    public void preStart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new DriveSensorResetRoutine(0.1), ReZero(false));
    }

    Routine ReZero(boolean inverted) {

        //invert the cords if the robot starts backwards
        int invertCord = 1;
        if (inverted) {
            invertCord = -1;
        }

        ArrayList<Routine> routines = new ArrayList<>();

        // Drive off the level 2 platform
        SparkMaxOutput backOffOutput = new SparkMaxOutput();
        backOffOutput.setPercentOutput(invertCord * 0.25);

        //drive off platform
        SparkDriveSignal driveOff = new SparkDriveSignal(backOffOutput, backOffOutput);
        routines.add(new DriveTimeRoutine(0.8, driveOff));

        // Back up against the platform
        SparkMaxOutput eachOutput = new SparkMaxOutput();
        eachOutput.setPercentOutput(invertCord * -0.3);

        SparkDriveSignal backUp = new SparkDriveSignal(eachOutput, eachOutput);
        routines.add(new DriveTimeRoutine(1.5, backUp));

        // Zero robot state
        routines.add(new DriveSensorResetRoutine(0.3));

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return sAlliance.toString();
    }
}


