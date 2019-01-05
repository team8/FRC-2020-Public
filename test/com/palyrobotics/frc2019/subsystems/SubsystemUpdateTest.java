package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.robot.MockRobot;
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
