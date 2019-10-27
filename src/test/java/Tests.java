import com.palyrobotics.frc2019.util.control.Gains;
import com.palyrobotics.frc2019.util.control.LazySparkMax;
import com.palyrobotics.frc2019.util.control.SmartGains;
import com.revrobotics.ControlType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Tests {

//    @Test
//    public void testArchitecture() {
//        var commands = Commands.reset();
//        var manager = new RoutineManager();
//        var operatorInterface = new OperatorInterface();
//        manager.update(operatorInterface.updateCommands(commands));
//        manager.reset(commands);
//    }

    @Test
    public void testLazySpark() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            var spark = new LazySparkMax(0);
            var gains = new Gains();
            gains.p = 0.2;
            assertTrue(spark.set(ControlType.kPosition, 0.0, 0.0, gains));
            gains.p = 0.3;
            assertTrue(spark.set(ControlType.kPosition, 0.0, 0.0, gains));
            assertFalse(spark.set(ControlType.kPosition, 0.0, 0.0, gains));
            gains = new Gains();
            assertTrue(spark.set(ControlType.kPosition, 0.0, 0.0, gains));
            assertFalse(spark.set(ControlType.kPosition, 0.0, 0.0, gains));
            assertTrue(spark.set(ControlType.kPosition, 0.1, 0.0, gains));
            assertFalse(spark.set(ControlType.kPosition, 0.1, 0.0, gains));
            assertTrue(spark.set(ControlType.kPosition, 0.1, 0.1, gains));
            assertFalse(spark.set(ControlType.kPosition, 0.1, 0.1, gains));
            assertTrue(spark.set(ControlType.kSmartMotion, 0.1, 0.1, new SmartGains()));
            assertFalse(spark.set(ControlType.kSmartMotion, 0.1, 0.1, new SmartGains()));
        } else {
            System.out.println("OS is not Windows, skipping lazy spark max tests");
        }
    }
}
