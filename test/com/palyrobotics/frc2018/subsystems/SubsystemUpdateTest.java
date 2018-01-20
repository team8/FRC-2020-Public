package com.palyrobotics.frc2018.subsystems;

import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.robot.MockRobot;
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
		RobotState r = new RobotState();

		Drive.getInstance().update(c, r);
	}

}
