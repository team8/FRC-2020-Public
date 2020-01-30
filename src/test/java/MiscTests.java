import static org.junit.jupiter.api.Assertions.*;

import com.palyrobotics.frc2020.util.InterpolatingDoubleTreeMap;
import com.palyrobotics.frc2020.util.SolenoidState;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.util.control.Gains;
import com.palyrobotics.frc2020.util.control.ProfiledGains;
import com.palyrobotics.frc2020.util.control.Spark;

import edu.wpi.first.wpilibj.Timer;

import org.junit.jupiter.api.Test;

public class MiscTests {

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

	@Test
	public void testInterpolatingTreeMap() {
		var map = new InterpolatingDoubleTreeMap();
		map.put(0.0, 0.0);
		map.put(1.0, 1.0);
		map.put(5.0, 9.0);
		assertEquals(0.5, map.getInterpolated(0.5));
		assertEquals(5.0, map.getInterpolated(3.0));
		assertEquals(9.0, map.getInterpolated(100.0));
		assertEquals(0.0, map.getInterpolated(-100.0));
	}

	@Test
	public void testSolenoidState() {
		var state = new SolenoidState();
		state.updateExtended(true);
		assertFalse(state.isExtended());
		Timer.delay(SolenoidState.kChangeDurationSeconds * 2);
		state.updateExtended(true);
		assertTrue(state.isExtended());
		state.updateExtended(false);
		assertTrue(state.isExtended());
		Timer.delay(SolenoidState.kChangeDurationSeconds * 2);
		state.updateExtended(false);
		assertFalse(state.isExtended());
	}
}
