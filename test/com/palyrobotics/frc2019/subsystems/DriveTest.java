package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.MockCommands;
import com.palyrobotics.frc2020.config.MockRobotState;
import com.palyrobotics.frc2020.robot.MockRobot;
import com.palyrobotics.frc2020.util.CheesyDriveHelper;
import com.palyrobotics.frc2020.util.DriveSignal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * 
 * 
 * Created by Nihar on 1/22/17. Tests {@link Drive}
 */
public class DriveTest {
	private static MockCommands commands;
	private static MockRobotState state;
	private static Drive drive;

	@Before
	public void setUp() {
		commands = MockRobot.getCommands();
		state = MockRobot.getRobotState();
		drive = Drive.getInstance();
		drive.setNeutral();
	}

	@After
	public void tearDown() {
		commands = null;
		state = null;
		drive = null;
	}

	@Test
	public void testOffboard() {
		drive.update(commands, state);

		DriveSignal signal = DriveSignal.getNeutralSignal();
		signal.leftMotor.setPercentOutput(0.5);
		signal.rightMotor.setPercentOutput(0.5);
		
		drive.setTalonSRXController(signal);
		commands.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
		
		drive.update(commands, state);
		assertThat("not updating correctly", drive.getDriveSignal(), equalTo(signal));
		signal.leftMotor.setPercentOutput(1);
		drive.update(commands, state);
		assertFalse("Signal was updated through external reference!", drive.getDriveSignal() == signal);
	}

	@Test
	public void testPassByReference() {
		drive.update(commands, state);

		DriveSignal signal = DriveSignal.getNeutralSignal();
		signal.leftMotor.setPercentOutput(0.5);
		signal.rightMotor.setPercentOutput(0.5);
		
		drive.setTalonSRXController(signal);
		commands.wantedDriveState = Drive.DriveState.OFF_BOARD_CONTROLLER;
		
		drive.update(commands, state);
		assertThat("not updating correctly", drive.getDriveSignal(), equalTo(signal));
	}

	@Test
	public void testNeutral() throws Exception {
		drive.setNeutral();
		assertThat("Drive output not neutral!", drive.getDriveSignal(), equalTo(DriveSignal.getNeutralSignal()));
		//TODO: Undo neutral and try again
	}

	@Test
	public void testChezyDrive() {
		commands.wantedDriveState = Drive.DriveState.CHEZY;
		drive.update(commands, state);
		assertThat("Did not sucessfully set CheesyDrive", drive.getDriveSignal(), equalTo(new CheesyDriveHelper().cheesyDrive(commands, state)));
	}

	@Test
	public void testOpenLoop() {
		commands.wantedDriveState = Drive.DriveState.OPEN_LOOP;
		commands.robotSetpoints.drivePowerSetpoint = Optional.of(DriveSignal.getNeutralSignal());
		drive.update(commands, state);
		assertThat("Drive output not corresponding to driveSetpoint", commands.robotSetpoints.drivePowerSetpoint.orElse(null), equalTo(drive.getDriveSignal()));
	}
}
