<<<<<<< HEAD:test/com/palyrobotics/frc2019/behavior/RoutineManagerTest.java
package com.palyrobotics.frc2019.behavior;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.robot.MockRobot;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Subsystem;
=======
package com.palyrobotics.frc2018.behavior;

import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.robot.MockRobot;
import com.palyrobotics.frc2018.subsystems.Drive;
import com.palyrobotics.frc2018.subsystems.Subsystem;
>>>>>>> bf48357... Added Elevator:test/com/palyrobotics/frc2018/behavior/RoutineManagerTest.java
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Nihar on 1/22/17. Unit tests for {@link RoutineManager}
 * 
 * @author Ailyn
 */
public class RoutineManagerTest {
	private RoutineManager mRoutineManager = new RoutineManager();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testUpdate() {
		Commands commands = MockRobot.getCommands();
<<<<<<< HEAD:test/com/palyrobotics/frc2019/behavior/RoutineManagerTest.java

		thrown.expect(NullPointerException.class);
		mRoutineManager.addNewRoutine(null);

=======

		thrown.expect(NullPointerException.class);
		mRoutineManager.addNewRoutine(null);

>>>>>>> bf48357... Added Elevator:test/com/palyrobotics/frc2018/behavior/RoutineManagerTest.java
		MockRoutine mRoutine = new MockRoutine();
		ArrayList<Routine> correct = new ArrayList<Routine>();
		correct.add(mRoutine);

		mRoutineManager.addNewRoutine(mRoutine);
		mRoutineManager.update(commands);
		assertThat("New routine was not added correctly", mRoutineManager.getCurrentRoutines(), equalTo(correct));

		//Sets sample routine to isFinished
		mRoutine.cancel(commands);
		mRoutine.update(commands);
		assertThat("Finished routines not removed correctly", mRoutineManager.getCurrentRoutines().isEmpty(), equalTo(true));

		commands.wantedRoutines.add(mRoutine);
		mRoutineManager.update(commands);
		assertThat("New routine was not added correctly from commands", mRoutineManager.getCurrentRoutines(), equalTo(correct));

		commands.cancelCurrentRoutines = true;
		mRoutineManager.update(commands);
		assertThat("Routines were not cancelled correctly from commands", mRoutineManager.getCurrentRoutines().isEmpty(), equalTo(true));
	}

	/**
	 * Test that the reset method <br/>
	 * Resets running routines, cancels routines, updates commands
	 */
	@Test
	public void testResetMethod() {
		Commands commands = MockRobot.getCommands();
		mRoutineManager.reset(commands);
		MockRoutine mRoutine = new MockRoutine();
<<<<<<< HEAD:test/com/palyrobotics/frc2019/behavior/RoutineManagerTest.java
//		DrivetrainRoutine mDriveRoutine = new DrivetrainRoutine();
=======
		DrivetrainRoutine mDriveRoutine = new DrivetrainRoutine();
>>>>>>> bf48357... Added Elevator:test/com/palyrobotics/frc2018/behavior/RoutineManagerTest.java
		mRoutineManager.addNewRoutine(mRoutine);
//		mRoutineManager.addNewRoutine(mDriveRoutine);

		//TODO: This throws a null pointer exception!
		mRoutineManager.update(commands);

		//TODO: Test actual functionality of the reset method
		mRoutineManager.reset(commands);
		assertThat("Routines were not emptied", mRoutineManager.getCurrentRoutines().isEmpty(), equalTo(true));
		assertThat("First routine was not canceled", mRoutine.finished(), equalTo(true));
//		assertThat("Second routine was not canceled", mDriveRoutine.finished(), equalTo(true));
	}

	@Test
	public void testConflictingRoutinesMethod() {
		ArrayList<Routine> correctConflicts = new ArrayList<Routine>();
		correctConflicts.add(new MockRoutine());
		@SuppressWarnings("unchecked")
		ArrayList<Routine> routinesList = (ArrayList<Routine>) correctConflicts.clone();
		routinesList.add(new DrivetrainRoutine());
		ArrayList<Routine> conflicts = mRoutineManager.conflictingRoutines(routinesList, new MockRoutine());
		assertThat("Not all conflicts were detected with just one conflict", conflicts, equalTo(correctConflicts));

		correctConflicts = new ArrayList<Routine>();
		correctConflicts.add(new DrivetrainRoutine());
		@SuppressWarnings("unchecked")
		ArrayList<Routine> routinesList2 = (ArrayList<Routine>) correctConflicts.clone();
<<<<<<< HEAD:test/com/palyrobotics/frc2019/behavior/RoutineManagerTest.java
		routinesList2.add(new MockRoutine());
=======
		routinesList2.add(new MockRoutine());
>>>>>>> bf48357... Added Elevator:test/com/palyrobotics/frc2018/behavior/RoutineManagerTest.java
		conflicts = mRoutineManager.conflictingRoutines(routinesList2, new DrivetrainRoutine());
		assertThat("Not all conflicts were detected with multiple conflicts", conflicts, equalTo(correctConflicts));

		correctConflicts = new ArrayList<Routine>();
<<<<<<< HEAD:test/com/palyrobotics/frc2019/behavior/RoutineManagerTest.java
		correctConflicts.add(new MockRoutine());
=======
		correctConflicts.add(new MockRoutine());
>>>>>>> bf48357... Added Elevator:test/com/palyrobotics/frc2018/behavior/RoutineManagerTest.java
		@SuppressWarnings("unchecked")
		ArrayList<Routine> routinesList3 = (ArrayList<Routine>) correctConflicts.clone();
		routinesList3.add(new DrivetrainRoutine());
		conflicts = mRoutineManager.conflictingRoutines(routinesList3, new MockRoutine());
		assertThat("Not all conflicts were detected with multiple non-conflicts", conflicts, equalTo(correctConflicts));

		correctConflicts = new ArrayList<Routine>();
		correctConflicts.add(new DrivetrainRoutine());
		ArrayList<Routine> routinesList4 = (ArrayList<Routine>) correctConflicts.clone();
		routinesList4.add(new MockRoutine());
		assertThat("Not all conflicts were detected with multiple conflicting subsystems", conflicts, equalTo(correctConflicts));

		thrown.expect(NullPointerException.class);
		mRoutineManager.conflictingRoutines(null, null);
	}
<<<<<<< HEAD:test/com/palyrobotics/frc2019/behavior/RoutineManagerTest.java

	//ERROR: using MockRoutine gives NullPointerException
	@Test
	public void testSubsystemSuperset() {
		ArrayList<Routine> routinesList = new ArrayList<Routine>();
		//routinesList.add(new MockRoutine());
		//Subsystem[] superset1 = {};
		//assertThat("Should not detect any subsystems", RoutineManager.subsystemSuperset(routinesList),
		//equalTo(superset1));

=======

// 	ERROR: using MockRoutine gives NullPointerException
	@Test
	public void testSubsystemSuperset() {
		ArrayList<Routine> routinesList = new ArrayList<Routine>();
//		routinesList.add(new MockRoutine());
//		Subsystem[] superset1 = {};
//		assertThat("Should not detect any subsystems", RoutineManager.subsystemSuperset(routinesList), equalTo(superset1));

>>>>>>> bf48357... Added Elevator:test/com/palyrobotics/frc2018/behavior/RoutineManagerTest.java
		routinesList.add(new DrivetrainRoutine());
		Subsystem[] superset2 = { Drive.getInstance() };
		assertThat("Not all subsystems detected with single subsystem", RoutineManager.subsystemSuperset(routinesList), equalTo(superset2));

		routinesList.add(new DrivetrainRoutine());
		assertThat("Duplicates should not exist", RoutineManager.subsystemSuperset(routinesList), equalTo(superset2));

		Subsystem[] superset3 = { Drive.getInstance() };
		assertThat("Not all subsystems were detected", RoutineManager.subsystemSuperset(routinesList), equalTo(superset3));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSharedSubsystems() {
		ArrayList<Routine> routinesList = new ArrayList<Routine>();
		routinesList.add(new MockRoutine());
		routinesList.add(new DrivetrainRoutine());
		assertThat("Conflicts when none should exist", RoutineManager.sharedSubsystems(routinesList).length, equalTo(0));

		routinesList = new ArrayList<Routine>();
		routinesList.add(new MockRoutine());
		routinesList.add(new DrivetrainRoutine());
		Subsystem[] conflicts = { Drive.getInstance() };
		assertThat("Not catching conflicts with conflicts", RoutineManager.sharedSubsystems(routinesList), equalTo(conflicts));

		Subsystem[] conflicts2 = { Drive.getInstance() };
		assertThat("Not catching all conflicts with multiple conflicts", RoutineManager.sharedSubsystems(routinesList), equalTo(conflicts2));
	}
}
