package com.palyrobotics.frc2019.config;

import com.palyrobotics.frc2019.robot.MockRobot;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.DriveSignal;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Nihar on 1/22/17. Tests the {@link Commands}
 * 
 * @author Nihar
 */
public class CommandsTest {
	private Commands mCommands = MockRobot.getCommands();

	/**
	 * Tests for null pointer exceptions when initially setting values in Commands
	 */
	@Test
	public void testNullPointers() {
		//Check for variable construction in Commands if a line throws an Exception
		mCommands.wantedDriveState.toString();
		mCommands.robotSetpoints.toString();
		System.out.println("No null pointer exceptions!");
	}

	/**
	 * Test that the copy method works
	 */
	@Test
	public void testCopyMethod() {
		mCommands = MockRobot.getCommands();
		Commands copy = mCommands.copy();

		//Test the
		mCommands.wantedDriveState = Drive.DriveState.NEUTRAL;
		copy.wantedDriveState = Drive.DriveState.CHEZY;
		assertThat("Copy modified original drivestate", mCommands.wantedDriveState, equalTo(Drive.DriveState.NEUTRAL));

		mCommands.robotSetpoints.drivePowerSetpoint = Optional.of(DriveSignal.getNeutralSignal());
		copy.robotSetpoints.drivePowerSetpoint = null;

		assertThat("Copy modified original setpoints", mCommands.robotSetpoints.drivePowerSetpoint.get(), equalTo(DriveSignal.getNeutralSignal()));

		System.out.println(copy.cancelCurrentRoutines);
		System.out.println(copy.wantedRoutines);
	}
}
