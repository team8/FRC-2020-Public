package com.palyrobotics.frc2018.subsystems;

import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.robot.MockRobot;
import com.palyrobotics.frc2018.robot.Robot;
import com.palyrobotics.frc2018.util.DriveSignal;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * Created by Nihar on 1/22/17.
 * Tests {@link LegacyDrive}
 */
public class DriveTest {
	@Test
	public void testOffboard() {
		Commands commands = MockRobot.getCommands();
		RobotState state = Robot.getRobotState();
		Drive drive = Drive.getInstance();
		drive.resetController();
		drive.update(commands, state);
		commands.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
		drive.update(commands, state);	// should print error message that controller is missing

		DriveSignal signal = DriveSignal.getNeutralSignal();
		signal.leftMotor.setPercentOutput(0.5);
		signal.rightMotor.setPercentOutput(0.5);
		drive.setTalonSRXController(signal);
		drive.update(commands, state);
		assertThat("not updating correctly", drive.getDriveSignal(), equalTo(signal));
		signal.leftMotor.setPercentOutput(1);
		drive.update(commands, state);
		assertFalse("Signal was updated through external reference!", drive.getDriveSignal()==signal);

		// Test that pass by reference is ok
		DriveSignal newSignal = DriveSignal.getNeutralSignal();
		newSignal.leftMotor.setPercentOutput(1);
		newSignal.rightMotor.setPercentOutput(1);
		drive.setTalonSRXController(newSignal);
		drive.update(commands, state);
		assertThat("not updating correctly", drive.getDriveSignal(), equalTo(newSignal));
	}

	@Test
	public void testNeutral() throws Exception {
		Drive drive = Drive.getInstance();
		drive.setNeutral();
		assertThat("Drive output not neutral!", drive.getDriveSignal(), equalTo(DriveSignal.getNeutralSignal()));

		// TODO: Undo neutral and try again
	}
}
