package com.palyrobotics.frc2019.auto.testautos;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DriveSensorResetRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.SparkMaxRoutine;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.util.SparkSignal;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;

/**
 * Created by Nihar on 1/11/17. An AutoMode for running test autonomous
 */
public class TestAutoMode extends AutoModeBase {

	@Override
	public Routine getRoutine() {

		return getDrive();
//		return testF();
//		return getDrive();
	}

	@Override
	public String getKey() {
		return "Test Auto Mode";
	}

	@Override
	public String toString() {
		return "Test";
	}

	@Override
	public void prestart() {
//		Logger.getInstance().logRobotThread(Level.FINE, "Starting TestAutoMode");
	}

	private Routine testF() {
		double power = 0.1;
		SparkSignal signal = SparkSignal.getNeutralSignal();
		signal.leftMotor.setTargetVelocity(20, Gains.vidarVelocity);
		signal.rightMotor.setTargetVelocity(20, Gains.vidarVelocity);

//		signal.leftMotor.setPercentOutput(0.4);
//		signal.rightMotor.setPercentOutput(0.4);
		return new SparkMaxRoutine(signal, false);
	}

	private SequentialRoutine getDrive() {

		ArrayList<Routine> sequence = new ArrayList<>();
		ArrayList<Path.Waypoint> path = new ArrayList<>();
        path.add(new Path.Waypoint(new Translation2d(0, 0), 40));
		path.add(new Path.Waypoint(new Translation2d(60, 30), 40));
		path.add(new Path.Waypoint(new Translation2d(120, 30), 40));

//		sequence.add(new IntakeDownRoutine());
		sequence.add(new DriveSensorResetRoutine(.25));
		sequence.add(new DrivePathRoutine(new Path(path), false));
//		sequence.add(new SparkMaxRoutine(driveBackup, false));

		return new SequentialRoutine(sequence);
	}
}
