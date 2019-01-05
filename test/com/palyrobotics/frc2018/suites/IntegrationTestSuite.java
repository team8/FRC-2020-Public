package com.palyrobotics.frc2018.suites;

import com.palyrobotics.frc2018.behavior.IntakeRoutineTest;
import com.palyrobotics.frc2018.behavior.RoutineManagerTest;
import com.palyrobotics.frc2018.subsystems.SubsystemUpdateTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ SubsystemUpdateTest.class, IntakeRoutineTest.class, RoutineManagerTest.class })
/**
 * Test suite for integration tests
 * 
 * @author Joseph Rumelhart
 *
 *         All of the included tests should pass to verify integrity of robot sections Update with new integration tests as classes are created Should not
 *         include unit tests, place in other suite
 */
public class IntegrationTestSuite {
	//the class remains empty,
	//used only as a holder for the above annotations
}