package com.palyrobotics.frc2020.behavior;

import com.palyrobotics.frc2020.behavior.routines.elevator.ElevatorCustomPositioningRoutine;
import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.MockCommands;
import com.palyrobotics.frc2020.config.MockRobotState;
import com.palyrobotics.frc2020.robot.MockRobot;
import com.palyrobotics.frc2020.subsystems.Elevator;
import com.palyrobotics.frc2020.subsystems.Elevator.ElevatorState;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ElevatorCustomPositioningRoutineTest {
	
	private Elevator elevator;
	private Commands commands;
	private MockRobotState robotState;
	private ElevatorCustomPositioningRoutine routine;
	
	@Before
	public void initMockRobot() {
		robotState = MockRobot.getRobotState();
		Elevator.resetInstance();
		elevator = Elevator.getInstance();
		commands = MockRobot.getCommands();
		MockCommands.reset();
	}
	
	@Test
	public void testCalibrationTransition() {
		routine = new ElevatorCustomPositioningRoutine(1000, 5);
		Elevator.resetInstance();
		MockCommands.reset();
		commands.addWantedRoutine(routine);
		routine.start();
		robotState.elevatorBottomHFX = false;
		robotState.elevatorTopHFX = false;
		robotState.elevatorPosition = 100;
		for(int i = 0; i < 10; i++) {
			commands = routine.update(commands);
			elevator.update(commands, robotState);
			assertThat("Elevator did not calibrate first!", elevator.getState(), equalTo(ElevatorState.CALIBRATING));
		}
		robotState.elevatorBottomHFX = true;
		robotState.elevatorPosition = 100;
		commands = routine.update(commands);
		elevator.update(commands, robotState);
		assertThat("Routine did not transition into moving!", elevator.getState(), equalTo(ElevatorState.CUSTOM_POSITIONING));
	}
	
	@Test
	public void testUpdateOrder() {
		try{
			routine = new ElevatorCustomPositioningRoutine(1000, 5);
			elevator.update(commands, robotState);
			commands = routine.update(commands);
		}
		catch(Exception e){
			fail("Updating routines and subsystems in the wrong order throws errors but shouldn't");
		}
	}
	
	@Test
	public void testRoutineEnd() {
		routine = new ElevatorCustomPositioningRoutine(1000, 5);
		Elevator.resetInstance();
		MockCommands.reset();
		commands.addWantedRoutine(routine);
//		System.out.println(commands.wantedRoutines);
		routine.start();
		robotState.elevatorBottomHFX = true;
		robotState.elevatorPosition = 0;
		robotState.elevatorVelocity = 100;
		commands = routine.update(commands);
		elevator.update(commands, robotState);

		assertThat("Routine didn't calibrate!", elevator.getState(), not(ElevatorState.CALIBRATING));

		routine.finished();

		for(int i = 0; i < 10; i++) {
			robotState.elevatorBottomHFX = false;
			robotState.elevatorPosition = 10;
			robotState.elevatorVelocity = 100;
			commands = routine.update(commands);
			elevator.update(commands, robotState);

			assertThat("Routine finished early!", routine.finished(), equalTo(false));
		}

		robotState.elevatorBottomHFX = false;
		robotState.elevatorPosition = 1000;
		robotState.elevatorVelocity = 0;

		commands = routine.update(commands);
		elevator.update(commands, robotState);

		assertThat("Routine did not finish properly!", routine.finished(), equalTo(true));
	}
	
	@Test
	public void testTimeout() {
		routine = new ElevatorCustomPositioningRoutine(1000, 5);
		Elevator.resetInstance();
		MockCommands.reset();
		commands.addWantedRoutine(routine);
		routine.start();
		commands = routine.update(commands);
		elevator.update(commands, robotState);

		long startTime = System.currentTimeMillis();
		//10 ms for buffer time
		while((System.currentTimeMillis() - startTime) <= 5*1000 - 10) {
			commands = routine.update(commands);
			elevator.update(commands, robotState);
			assertThat("Routine timed out early!", routine.finished(), equalTo(false));
		}

		//10 ms buffer time
		while(System.currentTimeMillis() - startTime <= 5*1000 + 10) {
			commands = routine.update(commands);
			elevator.update(commands, robotState);
		}

		assertThat("Routine didn't time out!", routine.finished(), equalTo(true));
	}
	
	@Test
	public void testInterruptManualPositioning() {
		routine = new ElevatorCustomPositioningRoutine(1000, 5);
		Elevator.resetInstance();
		MockCommands.reset();
		commands.addWantedRoutine(routine);
		robotState.elevatorBottomHFX = true;
		robotState.elevatorTopHFX = false;
		robotState.elevatorPosition = 0;
		routine.start();
		commands = routine.update(commands);
		elevator.update(commands, robotState);
		commands.cancelCurrentRoutines = true;
		commands = routine.update(commands);
		commands = routine.cancel(commands);
		assertThat("Routine didn't cancel correctly!", commands.wantedElevatorState, equalTo(ElevatorState.HOLD));
		commands.wantedElevatorState = ElevatorState.MANUAL_POSITIONING;
		elevator.update(commands, robotState);
		assertThat("Routine didn't transition to manual positioning correctly!", elevator.getState(), equalTo(ElevatorState.MANUAL_POSITIONING));
	}
}
