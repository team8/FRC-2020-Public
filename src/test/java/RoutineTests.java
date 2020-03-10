
import java.util.Set;
import java.util.function.Predicate;

import com.palyrobotics.frc2020.behavior.ConditionalRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
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

	private class ConditionalTestingRoutine extends RoutineBase {

		private int mTime;
		private int mCycles;

		public ConditionalTestingRoutine(int cycles) {
			mTime = 0;
			mCycles = cycles;
		}

		@Override
		protected void update(Commands commands, RobotState state) {
			mTime++;
		}

		@Override
		public boolean checkFinished(RobotState state) {
			return mCycles <= mTime;
		}

		@Override
		public Set<SubsystemBase> getRequiredSubsystems() {
			return null;
		}
	}

	private static MockSubsystem mockSubsystem = new MockSubsystem();

	@Test
	public void testRoutine() {
		var conditionalFalse = new ConditionalRoutine(new ConditionalTestingRoutine(10), new Predicate<RobotState>() {

			@Override
			public boolean test(RobotState robotState) {
				return false;
			}

			@Override
			public Predicate<RobotState> and(Predicate<? super RobotState> other) {
				return null;
			}

			@Override
			public Predicate<RobotState> negate() {
				return null;
			}

			@Override
			public Predicate<RobotState> or(Predicate<? super RobotState> other) {
				return null;
			}
		});
	}

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
