import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineManager;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.Timer;

import org.junit.jupiter.api.Test;

public class RoutineTests {

	private static class MockSubsystem extends SubsystemBase {

		@Override
		public void update(Commands commands, RobotState state) {
		}
	}

	private static class TimedOne extends TimedRoutine {

		public TimedOne() {
			super(0.1);
		}

		@Override
		public Set<SubsystemBase> getRequiredSubsystems() {
			return Set.of(mockSubsystem);
		}
	}

	private static class TimedTwo extends TimedOne {
	}

	private static MockSubsystem mockSubsystem = new MockSubsystem();
	
}
