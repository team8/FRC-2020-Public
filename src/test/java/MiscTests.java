import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.util.control.Gains;
import com.palyrobotics.frc2020.util.control.ProfiledGains;
import com.palyrobotics.frc2020.util.control.Spark;

import org.junit.jupiter.api.Test;

public class MiscTests {

	// @Test
	// public void testArchitecture() {
	// var commands = Commands.reset();
	// var manager = new RoutineManager();
	// var operatorInterface = new OperatorInterface();
	// manager.update(operatorInterface.updateCommands(commands));
	// manager.reset(commands);
	// }

	@Test
	public void testSparkWrapper() {
		if (System.getProperty("os.name").startsWith("Windows")) {
			var spark = new Spark(0);
			var gains = new Gains();
			var output = new ControllerOutput();
			output.setTargetPosition(0.0, gains);
			gains.p = 0.2;
			assertTrue(spark.setOutput(output));
			gains.p = 0.3;
			assertTrue(spark.setOutput(output));
			assertFalse(spark.setOutput(output));
			gains.p = 0.0;
			assertTrue(spark.setOutput(output));
			assertFalse(spark.setOutput(output));
			output.setTargetPosition(0.1, gains);
			assertTrue(spark.setOutput(output));
			assertFalse(spark.setOutput(output));
			output.setTargetPosition(0.1, 0.1, gains);
			assertTrue(spark.setOutput(output));
			assertFalse(spark.setOutput(output));
			var profiledGains = new ProfiledGains();
			output.setTargetPositionProfiled(0.1, 0.1, profiledGains);
			assertTrue(spark.setOutput(output));
			assertFalse(spark.setOutput(output));
		} else {
			System.out.println("OS is not Windows, skipping lazy spark max tests");
		}
	}
}
