package com.palyrobotics.frc2019.auto.testautos;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.*;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.util.DriveSignal;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.SparkSignal;
import com.palyrobotics.frc2019.util.logger.Logger;

import java.util.ArrayList;
import java.util.logging.Level;

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
		Logger.getInstance().logRobotThread(Level.FINE, "Starting TestAutoMode");
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
//		sequence.add(new IntakeDownRoutine());
		sequence.add(new DriveSensorResetRoutine(.1));
		sequence.add(new CascadingGyroEncoderTurnAngleRoutine(180));
//		sequence.add(new SparkMaxRoutine(driveBackup, false));

		return new SequentialRoutine(sequence);
	}
}
