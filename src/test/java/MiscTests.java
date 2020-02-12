import static org.junit.jupiter.api.Assertions.*;

import com.palyrobotics.frc2020.util.InterpolatingDoubleTreeMap;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.control.*;

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
		}
	}

	@Test
	public void testTurnDifference() {
		assertEquals(Util.getDifferenceInAngleDegreesNeg180To180(180, 180), 0);
		assertEquals(Util.getDifferenceInAngleDegreesNeg180To180(30, 120), -90);
		assertEquals(Util.getDifferenceInAngleDegreesNeg180To180(180, -180), 0);
		assertEquals(Util.getDifferenceInAngleDegreesNeg180To180(-90, 45), -135);
		assertEquals(Util.getDifferenceInAngleDegreesNeg180To180(20, -40), 60);
		assertEquals(Util.getDifferenceInAngleDegreesNeg180To180(-120, 120), 120);
		assertEquals(Util.getDifferenceInAngleDegreesNeg180To180(30, 110), -80);
		assertEquals(Util.getDifferenceInAngleDegreesNeg180To180(10, 170), -160);

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
		var state = new TimedSolenoid(0, 0.1, true);
		state.setExtended(true);
		assertFalse(state.isExtended());
		Timer.delay(0.11);
		state.setExtended(true);
		assertTrue(state.isExtended());
		state.setExtended(false);
		assertTrue(state.isExtended());
		Timer.delay(0.11);
		state.setExtended(false);
		assertFalse(state.isExtended());
	}
}
