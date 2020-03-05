
import java.util.Set;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

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

//	@Test
//	public void testRoutine() {
//		var conditional = new ConditionalRoutine(new RoutineBase() {
//
//			@Override
//			public boolean checkFinished(@ReadOnly RobotState state) {
//				return false;
//			}
//
//			@Override
//			protected void start(Commands commands, @ReadOnly RobotState state) {
//				System.out.println("Start");
//			}
//
//			@Override
//			protected void update(Commands commands, @ReadOnly RobotState state) {
//				System.out.println("u");
//			}
//
//			@Override
//			public Set<SubsystemBase> getRequiredSubsystems() {
//				return Set.of();
//			}
//		}, state -> true);
//		var commands = new Commands();
//		commands.addWantedRoutine(conditional);
//		var routineManger = new RoutineManager();
//		var state = new RobotState();
//		routineManger.update(commands, state);
//		routineManger.update(commands, state);
//		routineManger.update(commands, state);
//	}
}
