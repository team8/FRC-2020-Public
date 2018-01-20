package com.palyrobotics.frc2018.subsystems;

import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.robot.Robot;
import org.junit.Test;

import static com.palyrobotics.frc2018.subsystems.Climber.LockState;
import static com.palyrobotics.frc2018.subsystems.Climber.MotionSubstate;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ClimberTest {
    Climber climber = Climber.getInstance();
    RobotState robotState = Robot.getRobotState();
    Commands commands = Robot.getCommands();

    @Test
    public void testClimber() {
        commands.wantedClimbMovement = MotionSubstate.MOVING;
        climber.update(commands, robotState);
        assertThat("Climber State Set Correctly", climber.getMotionSubstate(), equalTo(MotionSubstate.MOVING));

        commands.wantedLockState = LockState.LOCKED;
        climber.update(commands, robotState);
        assertThat("Climber State Set Correctly", climber.getLock(), equalTo(LockState.LOCKED));

    }
}
