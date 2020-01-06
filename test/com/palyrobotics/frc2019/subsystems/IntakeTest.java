package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.Constants;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.robot.MockRobot;
import com.palyrobotics.frc2020.subsystems.Intake.OpenCloseState;
import com.palyrobotics.frc2020.subsystems.Intake.WheelState;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntakeTest {
	Intake mIntake = new Intake();
	RobotState mRobotState;
	Commands mCommands;

	@Before
	public void setUp() {
		mCommands = MockRobot.getCommands();
		mRobotState = MockRobot.getRobotState();
	}

	@After
	public void tearDown() {
		mCommands = null;
		mRobotState = null;
	}

	@Test
	public void testClose() {
		mCommands.wantedIntakeOpenCloseState = OpenCloseState.CLOSED;
		mIntake.update(mCommands, mRobotState);
		assertThat("Did not close properly", mIntake.getOpenCloseOutput(), is(DoubleSolenoid.Value.kForward));
	}

	@Test
	public void testIntake() {
		mCommands.wantedIntakingState = WheelState.INTAKING;
		mIntake.update(mCommands, mRobotState);
		assertThat("Did not intake properly", mIntake.getTalonOutput().getSetpoint(), is(Constants.kIntakingMotorVelocity));
	}

	@Test
	public void testExpel() {
		mCommands.wantedIntakingState = WheelState.EXPELLING;
		mIntake.update(mCommands, mRobotState);
		assertThat("Did not expel properly", mIntake.getTalonOutput().getSetpoint(), is(Constants.kExpellingMotorVelocity));
	}
}
