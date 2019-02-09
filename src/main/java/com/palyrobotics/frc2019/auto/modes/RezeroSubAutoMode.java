package com.palyrobotics.frc2019.auto.modes;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.TimeoutRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.CascadingGyroEncoderTurnAngleRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveTimeRoutine;
import com.palyrobotics.frc2019.config.Constants.*;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.SparkSignal;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

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
        return new SequentialRoutine(new DriveSensorResetRoutine(0.1), Rezero(true));
    }

    public Routine Rezero(boolean inverted) {

        //invert the cords if the robot starts backwards
        int invertCord = 1;
        if (inverted) {
            invertCord = -1;
        }

        ArrayList<Routine> routines = new ArrayList<>();

        // Drive off the level 2 platform
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        waypoints.add(new Waypoint(new Translation2d(30 * invertCord, 0), 35));
        waypoints.add(new Waypoint(new Translation2d(40 * invertCord, 0), 0));
        routines.add(new DrivePathRoutine(new Path(waypoints), true));

        // Back up against the platform
        SparkMaxOutput eachOutput = new SparkMaxOutput();
        eachOutput.setPercentOutput(0.2);

        SparkSignal backUp = new SparkSignal(eachOutput, eachOutput);
        routines.add(new DriveTimeRoutine(0.7, backUp));

        // Zero robot state
        routines.add(new DriveSensorResetRoutine(0.3));

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}


