import com.palyrobotics.frc2020.behavior.RoutineManager;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.WaitRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import edu.wpi.first.wpilibj.Timer;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

public class RoutineTests {

    private static class MockSubsystem extends Subsystem {

        public MockSubsystem() {
            super("mock");
        }

        @Override
        public void update(Commands commands, RobotState robotState) {

        }
    }

    private static MockSubsystem mockSubsystem = new MockSubsystem();

    private static class WaitOne extends WaitRoutine {
        public WaitOne() {
            super(0.5);
        }

        @Override
        public Set<Subsystem> getRequiredSubsystems() {
            return Set.of(mockSubsystem);
        }
    }

    private static class WaitTwo extends WaitRoutine {
        public WaitTwo() {
            super(0.5);
        }

        @Override
        public Set<Subsystem> getRequiredSubsystems() {
            return Set.of(mockSubsystem);
        }
    }

    @Test
    public void testRoutineManager() { // TODO: something better than just time based since that is kinda sketch
        Timer timer = new Timer();
        timer.start();
        Commands commands = Commands.getInstance();
        RoutineManager routineManager = RoutineManager.getInstance();
        commands.addWantedRoutine(new SequentialRoutine(new WaitOne(), new WaitTwo()));
        do {
            routineManager.update(commands);
            Timer.delay(0.02);
        } while (!routineManager.getCurrentRoutines().isEmpty());
        assertTrue(timer.get() > 1.0);
    }
}
