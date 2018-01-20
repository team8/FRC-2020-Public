package com.palyrobotics.frc2018.subsystems;


import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.config.MockRobotState;
import com.palyrobotics.frc2018.config.RobotState.GamePeriod;
import com.palyrobotics.frc2018.robot.MockRobot;
import com.palyrobotics.frc2018.subsystems.Elevator.ElevatorState;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class ElevatorTest {

	private Elevator elevator;
	private Commands commands;
	private MockRobotState robotState;

	// Test calibration with elevator starting in the middle
	@Test
	public void testInitCalibrationMiddle() {
		robotState.elevatorPosition = 500;
		elevator.update(commands, robotState);

		assertThat("Elevator is not calibrating", elevator.getState(), equalTo(ElevatorState.CALIBRATING));

		int counter = 0;
		while (elevator.getState() == ElevatorState.CALIBRATING) {
			robotState.elevatorPosition += elevator.getOutput().getSetpoint() * 10;
			if (robotState.elevatorPosition == 0) {
				robotState.elevatorBottomHFX = true;
			}
			elevator.update(commands, robotState);
			assertFalse("Calibration timed out!", ++counter > 5000);
		}
		assertThat("Elevator bottom setpoint not properly set",
				elevator.getElevatorBottomPosition(), equalTo(Optional.of(0.0)));
		assertThat("Elevator top setpoint not properly set",
				elevator.getElevatorTopPosition(), equalTo(Optional.of(Constants.kTopBottomEncoderDifference)));

		assertFalse("Elevator is calibrating when it should be finished",
				elevator.getState() == ElevatorState.CALIBRATING);
	}

	// Test calibration with elevator starting at the bottom
	@Test
	public void testInitCalibrationBottom() {
		robotState.elevatorBottomHFX = true;

		elevator.update(commands, robotState);

		assertThat("Elevator bottom setpoint not properly set", elevator.getElevatorBottomPosition(),
				equalTo(Optional.of(0.0)));
		assertThat("Elevator top setpoint not properly set", elevator.getElevatorTopPosition(),
				equalTo(Optional.of(Constants.kTopBottomEncoderDifference)));

		assertFalse("Elevator is calibrating when it should be finished",
				elevator.getState() == ElevatorState.CALIBRATING);
	}

	// Test calibration with elevator starting at the top
	@Test
	public void testInitCalibrationTop() {
		robotState.elevatorPosition = Constants.kTopBottomEncoderDifference;
		robotState.elevatorTopHFX = true;
		elevator.update(commands, robotState);

		assertThat("Elevator bottom setpoint not properly set",
				elevator.getElevatorBottomPosition().get(), equalTo(0.0));
		assertThat("Elevator top setpoint not properly set",
				elevator.getElevatorTopPosition().get(), equalTo(Constants.kTopBottomEncoderDifference));
		assertFalse("Elevator is calibrating when it should be finished",
				elevator.getState() == ElevatorState.CALIBRATING);
	}

	// If an elevator state is requested during a match and the robot isn't
	// calibrated, does it recalibrate?
	@Test
	public void testRecalibration() {
		// Calibrate once, then undo it by resetting the sensors
		robotState.elevatorBottomHFX = true;
		elevator.update(commands, robotState);
		robotState.elevatorBottomHFX = false;

		elevator.setBottomPosition(Optional.empty());
		elevator.setTopPosition(Optional.empty());
		elevator.update(commands, robotState);

		// Request custom positioning
		commands.wantedElevatorState = ElevatorState.CUSTOM_POSITIONING;
		commands.robotSetpoints.elevatorPositionSetpoint = Optional.of(500.0);
		elevator.update(commands, robotState);

		assertThat("Elevator doesn't go to calibration upon state request", elevator.getState(),
				equalTo(ElevatorState.CALIBRATING));

		// Finish recalibration
		robotState.elevatorBottomHFX = true;
		elevator.update(commands, robotState);

		assertThat("Elevator doesn't properly transition to waiting state", elevator.getState(),
				equalTo(ElevatorState.CUSTOM_POSITIONING));
	}

	// Does the elevator properly transition in and out of hold during manual control
	@Test
	public void testHoldDuringManual() {
		// Obligatory calibration
		robotState.elevatorBottomHFX = true;
		elevator.update(commands, robotState);
		robotState.elevatorBottomHFX = false;

		commands.wantedElevatorState = ElevatorState.MANUAL_POSITIONING;

		for (double i = -1; i <= 1; i += .01) {
			robotState.elevatorStickInput.setY(i);
			elevator.update(commands, robotState);
			if (Math.abs(i) <= Constants.kDeadband) {
				assertThat("Elevator doesn't hold when it should",
						elevator.getState(), equalTo(ElevatorState.HOLD));
			} else {
				assertThat("Elevator holds when it shouldn't",
						elevator.getState(), equalTo(ElevatorState.MANUAL_POSITIONING));
			}
		}
	}

	// Test to make sure that calibration handles other state requests properly
	@Test
	public void testCalibrationInterruption() {
		// Begin calibration
		elevator.update(commands, robotState);

		// Attempt to interrupt with custom positioning
		commands.wantedElevatorState = ElevatorState.CUSTOM_POSITIONING;
		commands.robotSetpoints.elevatorPositionSetpoint = Optional.of(500.0);
		elevator.update(commands, robotState);
		assertThat("Custom positioning interrupts calibration when it shouldn't", elevator.getState(),
				equalTo(ElevatorState.CALIBRATING));

		// Attempt to interrupt with manual positioning
		robotState.gamePeriod = GamePeriod.TELEOP;
		robotState.elevatorStickInput.setY(1);
		commands.wantedElevatorState = ElevatorState.CALIBRATING;
		elevator.update(commands, robotState);
		assertThat("Manual input doesn't interrupt calibration when it should", elevator.getState(),
				equalTo(ElevatorState.MANUAL_POSITIONING));

		// Finish calibration
		robotState.elevatorBottomHFX = true;
		robotState.elevatorStickInput.setY(0);
		elevator.update(commands, robotState);
		robotState.elevatorBottomHFX = false;

		// Request manual positioning
		robotState.elevatorStickInput.setY(1);
		elevator.update(commands, robotState);
		assertThat("Manual input ignored after calibration", elevator.getState(),
				equalTo(ElevatorState.MANUAL_POSITIONING));

		// Request custom positioning
		robotState.elevatorStickInput.setY(0);
		commands.wantedElevatorState = ElevatorState.CUSTOM_POSITIONING;
		commands.robotSetpoints.elevatorPositionSetpoint = Optional.of(500.0);
		elevator.update(commands, robotState);
		assertThat("Custom positioning ignored after calibration", elevator.getState(),
				equalTo(ElevatorState.CALIBRATING));
	}

	// Ensure that the robot updates the top/bottom encoder values when it
	// hits the HFX again
	@Test
	public void testUpdateCalibration() {
		// Obligatory calibration
		robotState.elevatorBottomHFX = true;
		elevator.update(commands, robotState);
		robotState.elevatorBottomHFX = false;

		// Trigger top HFX
		robotState.elevatorPosition = 500;
		robotState.elevatorTopHFX = true;
		elevator.update(commands, robotState);
		assertThat("Top encoder position not properly updated!", elevator.getElevatorTopPosition(),
				equalTo(Optional.of(500.0)));
		robotState.elevatorTopHFX = false;

		// Trigger bottom HFX
		robotState.elevatorPosition = -500;
		robotState.elevatorBottomHFX = true;
		elevator.update(commands, robotState);
		assertThat("Bottom encoder position not properly updated!", elevator.getElevatorBottomPosition(),
				equalTo(Optional.of(-500.0)));
		robotState.elevatorBottomHFX = false;
	}

	@Before
	public void initMockRobot() {
		robotState = MockRobot.getRobotState();
		elevator = Elevator.getInstance();
		commands = MockRobot.getCommands();
		Elevator.resetInstance();
		Commands.reset();

		commands.wantedElevatorState = ElevatorState.CALIBRATING;

		robotState.elevatorPosition = 0;
		robotState.elevatorBottomHFX = false;
		robotState.elevatorTopHFX = false;
	}
}
