package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.robot.MockRobot;
import org.junit.Test;

/**
 * Tests instantion of all subsystems.
 *
 * @author Robbie Selwyn
 *
 */
public class SubsystemUpdateTest {

	@Test
	public void test() {
		Commands c = MockRobot.getCommands();
		RobotState r = MockRobot.getRobotState();

		Drive.getInstance().update(c, r);
	}

}
