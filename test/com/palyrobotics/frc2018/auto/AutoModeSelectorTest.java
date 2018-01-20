package com.palyrobotics.frc2018.auto;

import com.palyrobotics.frc2018.auto.modes.TestAutoMode;
import com.palyrobotics.frc2018.auto.modes.TestTrajectoryAutoMode;
import com.palyrobotics.frc2018.config.Constants;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Nihar on 1/22/17.
 * Tests {@link AutoModeSelector}
 */
public class AutoModeSelectorTest {
	/* TODO: possible tests
	 * Test that you can get an auto mode
	 * Test that the setAutoMode by name works
	 * Test that registerAutonomous works
	 * Test that you can get the list of auto modes
	 */

	//TODO add new automodes and test those

	@Test
	public void testGetAutoMode() throws IndexOutOfBoundsException {
		AutoModeSelector auto = AutoModeSelector.getInstance();

		// Using automodes registered in constructor
		assertThat("Incorrect auto mode retrieved", auto.getAutoMode().getClass(), equalTo(new TestAutoMode().getClass()));

		// Check index out of bounds
	}

	@Test
	public void testGetAutoModeList() {
		AutoModeSelector auto = AutoModeSelector.getInstance();

		// TODO: Hard coded is sketchy
		ArrayList<String> expectedAutoModeList = new ArrayList<String>();
		expectedAutoModeList.add("Test");
		expectedAutoModeList.add("DoNothing");
		expectedAutoModeList.add("BaseLine");
		expectedAutoModeList.add("CenterPeg");
		expectedAutoModeList.add("CenterPeg_CrossLeft");
		expectedAutoModeList.add("CenterPeg_CrossRight");
		expectedAutoModeList.add("LeftPeg");
		expectedAutoModeList.add("RightPeg");
		expectedAutoModeList.add("DoNothing");	// TODO: sometimes test is run individually, "this one is registered during testRegisterAutonomous()"
		ArrayList<String> test = auto.getAutoModeList();

		assertThat("Not all auto modes were retrieved", test.size(), equalTo(expectedAutoModeList.size()));
		assertThat("Auto modes are incorrect", test, equalTo(expectedAutoModeList));
	}

	@Test
	public void testSetAutoModeByName() {
		AutoModeSelector auto = AutoModeSelector.getInstance();
		// Intentionally register two copies of the same auto mode class
		auto.registerAutonomous(new TestTrajectoryAutoMode());
		auto.registerAutonomous(new TestTrajectoryAutoMode());
		assertThat("Should not set auto mode when duplicates exist", auto.setAutoModeByName("TestTrajectoryAutoMode"), equalTo(false));
		assertThat("Found auto mode when none exists", auto.setAutoModeByName("1234"), equalTo(false));

		// TODO: Use a sample auto mode to guarantee it has exactly 1 copy
		assertThat("Auto mode has been registered", auto.setAutoModeByName("SideAutoMode"), equalTo(true));
	}

	/**
	 * Test that new autonomous modes are registered
	 */
	@Test
	public void testRegisterAutonomous() {
		AutoModeSelector auto = AutoModeSelector.getInstance();
		int initSize = auto.getAutoModeList().size();
		ArrayList<String> autoNames = auto.getAutoModeList();
		AutoModeBase newAuto = new TestTrajectoryAutoMode();
		autoNames.add(newAuto.toString());
		auto.registerAutonomous(newAuto);

		// Index of the newest auto mode should be the original list length
		assertThat("AutoMode was registered incorrectly", auto.getAutoMode(initSize), equalTo(newAuto));
		assertThat("AutoMode was registered incorrectly", auto.getAutoModeList(), equalTo(autoNames));
	}

	@Test
	public void ticksPerDegreeCalculation() {
		System.out.println(Constants.kDriveInchesPerDegree * Constants.kDriveTicksPerInch);
	}
}
