
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

	private class TestingRoutine extends RoutineBase {

		private int mTime;
		private int mCycles;
		private String mName;

		public TestingRoutine(int cycles, String name) {
			mTime = 0;
			mCycles = cycles;
			mName = name;
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

		@Override
		public String getName() {
			return mName;
		}
	}

	private static MockSubsystem mockSubsystem = new MockSubsystem();

	@Test
	public void testRoutine() {
		var conditionalFalse = new ConditionalRoutine(new TestingRoutine(10, "True"), new Predicate<RobotState>() {

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
		var conditionalTrue = new ConditionalRoutine(new TestingRoutine(10, "False"), new Predicate<RobotState>() {

			@Override
			public boolean test(RobotState robotState) {
				return true;
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
		var twoConditionalFalse = new ConditionalRoutine(new TestingRoutine(10, "False1"), new TestingRoutine(10, "False2"), new Predicate<RobotState>() {

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
		var twoConditionalTrue = new ConditionalRoutine(new TestingRoutine(10, "True1"), new TestingRoutine(10, "True2"), new Predicate<RobotState>() {

			@Override
			public boolean test(RobotState robotState) {
				return true;
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

		for (int i = 0; i < 10; i++) {

		}
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
