package com.palyrobotics.frc2020.suites;

import com.palyrobotics.frc2020.auto.AutoModeSelectorTest;
import com.palyrobotics.frc2020.behavior.RoutineManagerTest;
import com.palyrobotics.frc2020.config.CommandsTest;
import com.palyrobotics.frc2020.config.RobotStateTest;
import com.palyrobotics.frc2020.subsystems.ClimberTest;
import com.palyrobotics.frc2020.subsystems.DriveTest;
import com.palyrobotics.frc2020.subsystems.IntakeTest;
import com.palyrobotics.frc2020.util.CheesyDriveHelperTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DriveTest.class, CommandsTest.class, CheesyDriveHelperTest.class, RoutineManagerTest.class, AutoModeSelectorTest.class,
	 ClimberTest.class, IntakeTest.class, CommandsTest.class, RobotStateTest.class, RoutineManagerTest.class
})
/**
 * Test suite for unit tests
 * 
 * @author Joseph Rumelhart
 *
 *         All of the included tests should pass to verify integrity of code Update with new unit tests as classes are created Should not include integration
 *         tests, place in other suite
 */
public class UnitTestSuite {
	//the class remains empty,
	//used only as a holder for the above annotations
}