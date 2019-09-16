package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveTimeRoutine;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.SparkSignal;

import java.util.ArrayList;

public class RezeroSubAutoMode extends AutoModeBase {

    @Override
    public String toString() {
        return mAlliance + this.getClass().toString();
    }

    @Override
    public void prestart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new DriveSensorResetRoutine(0.1), Rezero(false));
    }

    public Routine Rezero(boolean inverted) {

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
        SparkSignal driveOff = new SparkSignal(backOffOutput, backOffOutput);
        routines.add(new DriveTimeRoutine(0.8, driveOff));

        // Back up against the platform
        SparkMaxOutput eachOutput = new SparkMaxOutput();
        eachOutput.setPercentOutput(invertCord * -0.3);

        SparkSignal backUp = new SparkSignal(eachOutput, eachOutput);
        routines.add(new DriveTimeRoutine(1.5, backUp));

        // Zero robot state
        routines.add(new DriveSensorResetRoutine(0.3));

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}


