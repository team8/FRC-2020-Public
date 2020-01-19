import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineManager;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Subsystem;

import edu.wpi.first.wpilibj.Timer;

import org.junit.jupiter.api.Test;

public class RoutineTests {

	private static class MockSubsystem extends Subsystem {

		@Override
		public void update(Commands commands, RobotState robotState) {
		}
	}

	private static class TimedOne extends TimedRoutine {

		public TimedOne() {
			super(0.1);
		}

		@Override
		public Set<Subsystem> getRequiredSubsystems() {
			return Set.of(mockSubsystem);
		}
	}

	private static class TimedTwo extends TimedOne {
	}

	private static MockSubsystem mockSubsystem = new MockSubsystem();

	// TODO: don't rely on time for test
	@Test
	public void testRoutineManager() {
		Timer timer = new Timer();
		timer.start();
		Commands commands = Commands.getInstance();
		RoutineManager routineManager = RoutineManager.getInstance();
		commands.addWantedRoutine(new SequentialRoutine(new TimedOne(), new TimedTwo()));
		do {
			routineManager.update(commands);
			Timer.delay(0.02);
		} while (!routineManager.getCurrentRoutines().isEmpty());
		assertTrue(timer.get() > 0.2);
	}
}
