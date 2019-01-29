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
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

public class Rezero extends AutoModeBase {

//TODO adjust speed and other constants to optimize auto

    @Override
    public String toString() {
        return mAlliance + this.getClass().toString();
    }

    @Override
    public void prestart() {

    }

    @Override
    public Routine getRoutine() {
        return new SequentialRoutine(new DriveSensorResetRoutine(2), Rezero());
    }

    public Routine Rezero() {

        ArrayList<Routine> routines = new ArrayList<>();

        // Drive off the level 2 platform
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        waypoints.add(new Waypoint(new Translation2d(-30, 0), 35));
        waypoints.add(new Waypoint(new Translation2d(-40, 0), 0));
        routines.add(new DrivePathRoutine(new Path(waypoints), true));

        // Back up against the platform
        TalonSRXOutput left = new TalonSRXOutput();
        TalonSRXOutput right = new TalonSRXOutput();
        left.setPercentOutput(0.2);
        right.setPercentOutput(0.2);
        DriveSignal backUp = new DriveSignal(left, right);
        routines.add(new DriveTimeRoutine(0.7, backUp));

        // Zero robot state
        routines.add(new DriveSensorResetRoutine(0.2));

        return new SequentialRoutine(routines);
    }

    @Override
    public String getKey() {
        return mAlliance.toString();
    }
}


