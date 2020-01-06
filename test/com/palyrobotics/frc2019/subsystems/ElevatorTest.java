package com.palyrobotics.frc2020.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2020.config.*;
import com.palyrobotics.frc2020.robot.MockRobot;
import com.palyrobotics.frc2020.subsystems.Elevator.ElevatorState;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class ElevatorTest {

	private Elevator elevator;
	private Commands commands;
	private MockRobotState robotState;

	//Test calibration with elevator starting in the middle
	@Test
	public void testInitCalibrationMiddle() {
		robotState.elevatorPosition = 500;
		elevator.update(commands, robotState);

		assertThat("Elevator is not calibrating", elevator.getState(), equalTo(ElevatorState.CALIBRATING));

		int counter = 0;
		while(elevator.getState() == ElevatorState.CALIBRATING) {
			robotState.elevatorPosition += elevator.getOutput().getSetpoint() * 10;
			if(robotState.elevatorPosition == 0) {
				robotState.elevatorBottomHFX = true;
			}
			elevator.update(commands, robotState);
			assertFalse("Calibration timed out!", ++counter > 5000);
		}
		assertTrue("Elevator bottom set point not properly set", Math.abs(elevator.getElevatorBottomPosition().get()) <= 4 );
		assertThat("Elevator top set point not properly set", elevator.getElevatorTopPosition(), equalTo(Optional.of(Constants.kElevatorTopBottomDifferenceInches * Constants.kElevatorTicksPerInch)));

		assertFalse("Elevator is calibrating when it should be finished", elevator.getState() == ElevatorState.CALIBRATING);
	}

	//Test calibration with elevator starting at the bottom
	@Test
	public void testInitCalibrationBottom() {
		robotState.elevatorBottomHFX = true;

		elevator.update(commands, robotState);
		elevator.update(commands, robotState);
        elevator.update(commands, robotState);

		assertThat("Elevator bottom set point not properly set", elevator.getElevatorBottomPosition(), equalTo(Optional.of(0.0)));
		assertThat("Elevator top set point not properly set", elevator.getElevatorTopPosition(), equalTo(Optional.of(Constants.kElevatorTopBottomDifferenceInches * Constants.kElevatorTicksPerInch)));

		assertFalse("Elevator is calibrating when it should be finished", elevator.getState() == ElevatorState.CALIBRATING);
	}

	//Test calibration with elevator starting at the top
	@Test
	public void testInitCalibrationTop() {
		robotState.elevatorPosition = Constants.kElevatorTopBottomDifferenceInches * Constants.kElevatorTicksPerInch;
		robotState.elevatorTopHFX = true;
		elevator.update(commands, robotState);
		elevator.update(commands, robotState);
        elevator.update(commands, robotState);

		assertThat("Elevator bottom set point not properly set", elevator.getElevatorBottomPosition().get(), equalTo(0.0));
		assertThat("Elevator top set point not properly set", elevator.getElevatorTopPosition().get(), equalTo(Constants.kElevatorTopBottomDifferenceInches * Constants.kElevatorTicksPerInch));
		assertFalse("Elevator is calibrating when it should be finished", elevator.getState() == ElevatorState.CALIBRATING);
	}

	//If an elevator state is requested during a match and the robot isn't
	//calibrated, does it recalibrate?
	@Test
	public void testRecalibration() {
		//Calibrate once, then undo it by resetting the sensors
		robotState.elevatorBottomHFX = true;
		elevator.update(commands, robotState);
		robotState.elevatorBottomHFX = false;

		elevator.setBottomPosition(Optional.empty());
		elevator.setTopPosition(Optional.empty());
		elevator.update(commands, robotState);

		//Request custom positioning
		commands.wantedElevatorState = ElevatorState.CUSTOM_POSITIONING;
		commands.robotSetpoints.elevatorPositionSetpoint = Optional.of(500.0);
		elevator.update(commands, robotState);

		assertThat("Elevator custom positioning is interrupted by calibration", elevator.getState(), equalTo(ElevatorState.CUSTOM_POSITIONING));

		//Finish recalibration
		robotState.elevatorBottomHFX = true;
		elevator.update(commands, robotState);

		assertThat("Elevator doesn't properly transition to waiting state", elevator.getState(), equalTo(ElevatorState.CUSTOM_POSITIONING));
	}

	//Does the elevator properly transition in and out of hold during manual control
	@Test
	public void testHoldDuringManual() {
		//Obligatory calibration
		robotState.elevatorBottomHFX = true;
		elevator.update(commands, robotState);
		robotState.elevatorBottomHFX = false;

		commands.wantedElevatorState = ElevatorState.MANUAL_POSITIONING;

		for(double i = -1; i <= 1; i += .01) {
			robotState.elevatorStickInput.setY(i);
			elevator.update(commands, robotState);
			if(Math.abs(i) <= Constants.kDeadband) {
				assertThat("Elevator doesn't hold when it should", elevator.getState(), equalTo(ElevatorState.HOLD));
			} else {
				assertThat("Elevator holds when it shouldn't", elevator.getState(), equalTo(ElevatorState.MANUAL_POSITIONING));
			}
		}
	}

	//Test to make sure that calibration handles other state requests properly
	@Test
	public void testCalibrationInterruption() {
		//Begin calibration
		elevator.update(commands, robotState);

		//Attempt to interrupt with custom positioning
		commands.wantedElevatorState = ElevatorState.CUSTOM_POSITIONING;
		commands.robotSetpoints.elevatorPositionSetpoint = Optional.of(500.0);
		elevator.update(commands, robotState);
		assertFalse("Custom positioning doesn't interrupts calibration when it should", elevator.getState() == ElevatorState.CALIBRATING);
	}

	//Ensure that the robot updates the top/bottom encoder values when it
	//hits the HFX again
	@Test
	public void testUpdateCalibration() {
		//Obligatory calibration
		robotState.elevatorBottomHFX = true;
		elevator.update(commands, robotState);
		robotState.elevatorBottomHFX = false;

        //Trigger top HFX
		robotState.elevatorPosition = 500;
		robotState.elevatorTopHFX = true;
		elevator.update(commands, robotState);
//        System.out.println(elevator.getElevatorTopPosition());
		assertThat("Top encoder position not properly updated!", elevator.getElevatorTopPosition(), equalTo(Optional.of(500.0)));
		robotState.elevatorTopHFX = false;
	}
	
	@Test
	public void testHold() {
		//Obligatory calibration
		robotState.elevatorBottomHFX = true;
		robotState.elevatorPosition = 0;
		elevator.update(commands, robotState);
		robotState.elevatorBottomHFX = true;
		
		commands.wantedElevatorState = ElevatorState.HOLD;
		elevator.update(commands, robotState);
		assertThat("Elevator gives power when at the bottom for hold!", elevator.getOutput().getControlMode(), equalTo(ControlMode.PercentOutput));
		assertThat("Elevator gives power when at the bottom for hold!", elevator.getOutput().getSetpoint(), equalTo(0.0));
		
		robotState.elevatorPosition = 100;
		elevator.update(commands, robotState);
		assertThat("Elevator gives power when at the bottom for hold!", elevator.getOutput().getControlMode(), equalTo(ControlMode.PercentOutput));
		assertThat("Elevator gives power when at the bottom for hold!", elevator.getOutput().getSetpoint(), equalTo(0.0));
		
		robotState.elevatorBottomHFX = false;
		robotState.elevatorTopHFX = true;
		elevator.update(commands, robotState);
		assertThat("Elevator doesn't give power when not at the bottom!", elevator.getOutput().getControlMode(), equalTo(ControlMode.Position));
		assertThat("Elevator doesn't give power when not at the bottom!", elevator.getOutput().getSetpoint(), equalTo(0.0));

	}

	@Test
	public void testClosedLoopManualControl() {
		for (int i = 0; i < 100; i++) {
			commands.wantedElevatorState = ElevatorState.MANUAL_POSITIONING;
			robotState.operatorStickInput.setY(1.0);
			elevator.update(commands, robotState);
//			System.out.println(elevator.getElevatorWantedPosition().orElse(-1.0));
		}
	}

	@Test
	public void testStickyFaultsReaction() {
		robotState.hasElevatorStickyFaults = true;
		robotState.gamePeriod = RobotState.GamePeriod.TELEOP;
		commands.wantedElevatorState = ElevatorState.CUSTOM_POSITIONING;
		elevator.update(commands, robotState);
		assertThat("Didn't prevent closed loop control with sticky faults in teleop!", elevator.getState(), equalTo(ElevatorState.MANUAL_POSITIONING));
		robotState.gamePeriod = RobotState.GamePeriod.AUTO;
		elevator.update(commands, robotState);
		assertThat("Didn't prevent closed loop control with sticky faults in auto!", elevator.getState(), equalTo(ElevatorState.IDLE));

		robotState.gamePeriod = RobotState.GamePeriod.TELEOP;
		commands.wantedElevatorState = ElevatorState.HOLD;
		elevator.update(commands, robotState);
		assertThat("Didn't prevent hold with sticky faults in teleop!", elevator.getState(), equalTo(ElevatorState.MANUAL_POSITIONING));
		robotState.gamePeriod = RobotState.GamePeriod.AUTO;
		elevator.update(commands, robotState);
		assertThat("Didn't prevent hold with sticky faults in auto!", elevator.getState(), equalTo(ElevatorState.IDLE));
	}

	@Before
	public void initMockRobot() {
		robotState = MockRobot.getRobotState();
		Elevator.resetInstance();
		elevator = Elevator.getInstance();
		MockCommands.reset();
		commands = MockRobot.getCommands();
		commands.wantedElevatorState = ElevatorState.CALIBRATING;
		robotState.elevatorPosition = 0;
		robotState.elevatorBottomHFX = false;
		robotState.elevatorTopHFX = false;
	}
}
