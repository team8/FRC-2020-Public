package com.palyrobotics.frc2020.util;

import com.palyrobotics.frc2020.config.MockRobotState;
import com.palyrobotics.frc2020.robot.MockRobot;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class MockJoystickInputTest {

	private MockRobotState robotState;

	@Test
	public void test() {
		robotState.elevatorStickInput.setY(1);
		assertThat(robotState.elevatorStickInput.getY(), equalTo(1.0));
	}

	@Before
	public void initMockRobot() {
		robotState = MockRobot.getRobotState();
	}

}
