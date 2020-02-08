import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.OperatorInterface;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Shooter;
import com.palyrobotics.frc2020.subsystems.Shooter.HoodState;
import com.palyrobotics.frc2020.util.config.Configs;

import org.junit.jupiter.api.Test;

public class ShooterTest {

	public static final ShooterConfig kShooterConfig = Configs.get(ShooterConfig.class);

	@Test
	public void testShooter() {
		var operatorInterface = new OperatorInterface();
		var commands = new Commands();
		operatorInterface.reset(commands);
		var state = new RobotState();
		var shooter = Shooter.getInstance();
		double testFlywheelVelocity = kShooterConfig.maxVelocity / 2.0;
		commands.setShooterCustomFlywheelVelocity(testFlywheelVelocity, HoodState.LOW);
		state.shooterFlywheelVelocity = testFlywheelVelocity;
		shooter.update(commands, state);
		assertTrue(shooter.isReadyToShoot());
		state.shooterFlywheelVelocity = testFlywheelVelocity + kShooterConfig.velocityTolerance * 2.0;
		shooter.update(commands, state);
		assertFalse(shooter.isReadyToShoot());
	}
}
