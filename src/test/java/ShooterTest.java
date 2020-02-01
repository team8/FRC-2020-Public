
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Shooter;

import org.junit.jupiter.api.Test;

public class ShooterTest {

	@Test
	public void testShooter() {
		var shooter = Shooter.getInstance();
		var commands = new Commands();
		commands.setShooterIdle();
		var state = new RobotState();
		shooter.update(commands, state);
		// TODO: finish off shooter
	}
}
