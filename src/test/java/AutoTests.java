import java.util.concurrent.atomic.AtomicReference;

import com.palyrobotics.frc2020.robot.Robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class AutoTests {

	@Test
	@Tag ("slow")
	public void testAutos() {
		RobotBase.startRobot(() -> {
			AtomicReference<Robot> robotReference = new AtomicReference<>(new Robot());
			new Thread(() -> {
				Timer.delay(5.0);
				System.out.println("Ending competition!");
				robotReference.get().endCompetition();
			}).start();
			return robotReference.get();
		});
	}
}
