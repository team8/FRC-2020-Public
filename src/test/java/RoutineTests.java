
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
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
		var commands = new Commands();
		var state = new RobotState();

		var conditionalFalse = new ConditionalRoutine(new TestingRoutine(10, "False"), robotState -> false);
		var conditionalTrue = new ConditionalRoutine(new TestingRoutine(10, "True"), robotState -> true);
		var twoConditionalFalse = new ConditionalRoutine(new TestingRoutine(10, "False1"), new TestingRoutine(9, "False2"), robotState -> false);
		var twoConditionalTrue = new ConditionalRoutine(new TestingRoutine(10, "True1"), new TestingRoutine(9, "True2"), robotState -> true);
		var twoConditionalSwitch = new ConditionalRoutine(new TestingRoutine(5, "First"), new TestingRoutine(5, "Second"), new Predicate<>() {

			int iterations = 0;

			@Override
			public boolean test(RobotState state) {
				iterations++;
				return iterations < 3;
			}
		});

		execute(commands, state, conditionalTrue, conditionalFalse);
		assertTrue(conditionalFalse.isFinished());
		assertFalse(conditionalTrue.isFinished());

		execute(commands, state, 9, conditionalTrue);
		assertTrue(conditionalTrue.isFinished());

		execute(commands, state, 9, twoConditionalFalse, twoConditionalTrue);
		assertTrue(twoConditionalFalse.isFinished());
		assertFalse(twoConditionalTrue.isFinished());
		execute(commands, state, twoConditionalTrue);
		assertTrue(twoConditionalTrue.isFinished());

		execute(commands, state, twoConditionalSwitch);
		assertEquals("First", twoConditionalSwitch.getRunningRoutine().getName());
		execute(commands, state, twoConditionalSwitch);
		assertEquals("Second", twoConditionalSwitch.getRunningRoutine().getName());
		assertFalse(twoConditionalSwitch.isFinished());
		execute(commands, state, 4, twoConditionalSwitch);
		assertTrue(twoConditionalSwitch.isFinished());
	}

	private void execute(Commands commands, RobotState state, RoutineBase... routines) {
		Arrays.stream(routines).forEach(routineBase -> routineBase.execute(commands, state));
	}

	private void execute(Commands commands, RobotState state, int times, RoutineBase... routines) {
		for (int i = 0; i < times; i++) {
			execute(commands, state, routines);
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
