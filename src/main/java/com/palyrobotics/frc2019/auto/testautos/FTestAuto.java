package com.palyrobotics.frc2019.auto.testautos;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.ParallelRoutine;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.routines.drive.SparkMaxRoutine;

import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.SparkSignal;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by Nihar on 1/11/17. An AutoMode for running test autonomous
 */
public class FTestAuto extends AutoModeBase {

    @Override
    public Routine getRoutine() {

//        List<Path.Waypoint> path = new ArrayList<>();
//        path.add(new Path.Waypoint(new Translation2d(0, 0), 72.0));
//        path.add(new Path.Waypoint(new Translation2d(20.0, 0.0), 72.0));
//        path.add(new Path.Waypoint(new Translation2d(mDistances.kLeftSwitchX - Constants.kRobotLengthInches,
//                mDistances.kLeftToCenterY + Constants.kRobotWidthInches/2.0 - mDistances.kLeftSwitchY/2.0), 72.0));
//        path.add(new Path.Waypoint(new Translation2d(mDistances.kLeftSwitchX + Constants.kRobotLengthInches,
//                mDistances.kLeftToCenterY + Constants.kRobotWidthInches/2.0 - mDistances.kLeftSwitchY/2.0), 0.0));
//        return new DrivePathRoutine(new Path(path), false);


//        DriveSignal test = DriveSignal.getNeutralSignal();
//        test.leftMotor.setPercentOutput(0.2);
//        test.rightMotor.setPercentOutput(0.2);
//        return new SparkMaxRoutine(test, true);
//        return new TimedDriveRoutine(0.2, 6.5);
        return getDrive();
    }

    @Override
    public String getKey() {
        return "FTestAuto";
    }

    @Override
    public String toString() {
        return "Test";
    }

    @Override
    public void prestart() {
        Logger.getInstance().logRobotThread(Level.FINE, "Starting TestAutoMode");
    }

    private ParallelRoutine getDrive() {
//        Gains mShortGains = Gains.vidarShortDriveMotionMagicGains;
//
        SparkSignal driveBackup = SparkSignal.getNeutralSignal();
        driveBackup.leftMotor.setPercentOutput(.6);
        driveBackup.rightMotor.setPercentOutput(.6);
        ArrayList<Routine> sequence = new ArrayList<>();
//
        sequence.add(new SparkMaxRoutine(driveBackup, false));
        System.out.println("getDrive() called in FTestAuto, SparkMaxRoutine created.");
//        sequence.add(new TimeoutRoutine(10));


        return new ParallelRoutine(sequence);
    }
}
