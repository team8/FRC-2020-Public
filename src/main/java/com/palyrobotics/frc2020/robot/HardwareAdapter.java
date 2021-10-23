package com.palyrobotics.frc2020.robot;

import java.util.List;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2020.config.PortConstants;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Falcon;
import com.palyrobotics.frc2020.util.control.Spark;
import com.palyrobotics.frc2020.util.control.Talon;
import com.palyrobotics.frc2020.util.control.TimedSolenoid;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.revrobotics.CANEncoder;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.*;

/**
 * Represents all hardware components of the robot. Singleton class. Should only be used in robot
 * package. Subdivides hardware into subsystems.
 */
public class HardwareAdapter {

	/**
	 * 1 NEO (controlled by Spark MAX), 1 Solenoid
	 */
	public static class ClimberHardware {

		private static ClimberHardware sInstance;
		public final Spark spark = new Spark(sPortConstants.nariClimberId, "Climber");
		public final CANEncoder sparkEncoder = spark.getEncoder();
		public final TimedSolenoid solenoid = new TimedSolenoid(sPortConstants.nariClimberSolenoidId, 0.2, true);

		ClimberHardware() {
		}

		public static ClimberHardware getInstance() {
			if (sInstance == null) sInstance = new ClimberHardware();
			return sInstance;
		}
	}

	/**
	 * 4 Falcon 500s (controlled by Talon FX), 1 Pigeon IMU Gyro connected via Talon SRX data cable.
	 */
	public static class DriveHardware {

		private static DriveHardware sInstance;

		public final Falcon leftMasterFalcon = new Falcon(sPortConstants.nariDriveLeftMasterId, "Drive Left Master"),
				leftSlaveFalcon = new Falcon(sPortConstants.nariDriveLeftSlaveId, "Drive Left Slave");
		public final Falcon rightMasterFalcon = new Falcon(sPortConstants.nariDriveRightMasterId, "Drive Right Master"),
				rightSlaveFalcon = new Falcon(sPortConstants.nariDriveRightSlaveId, "Drive Right Slave");

		public final List<Falcon> falcons = List.of(leftMasterFalcon, leftSlaveFalcon,
				rightMasterFalcon, rightSlaveFalcon);

		public final PigeonIMU gyro = new PigeonIMU(sPortConstants.nariDriveGyroId);

		private DriveHardware() {
		}

		public static DriveHardware getInstance() {
			if (sInstance == null) sInstance = new DriveHardware();
			return sInstance;
		}
	}

	/**
	 * 2 NEOs (controlled by Spark MAX), 2 775s (controlled by Talon SRX), 2 Single Solenoids, 3
	 * Infrared Sensors
	 */
	public static class IndexerHardware {

		private static IndexerHardware sInstance;
		public final Spark masterSpark = new Spark(sPortConstants.nariIndexerMasterId, "Indexer Master"),
				slaveSpark = new Spark(sPortConstants.nariIndexerSlaveId, "Indexer Slave");
		public final List<Spark> sparks = List.of(masterSpark, slaveSpark);
		public final CANEncoder masterEncoder = masterSpark.getEncoder(), slaveEncoder = slaveSpark.getEncoder();
		public final Talon leftVTalon = new Talon(sPortConstants.nariIndexerLeftVTalonId, "Indexer Left V"),
				rightVTalon = new Talon(sPortConstants.nariIndexerRightVTalonId, "Indexer Right V");
		public final List<Talon> vTalons = List.of(leftVTalon, rightVTalon);
		public final TimedSolenoid hopperSolenoid = new TimedSolenoid(sPortConstants.nariIndexerHopperSolenoidId, 0.8, true),
				blockingSolenoid = new TimedSolenoid(sPortConstants.nariIndexerBlockingSolenoidId, 0.2, true);
		public final DigitalInput backInfrared = new DigitalInput(sPortConstants.nariIndexerBackInfraredDio),
				frontInfrared = new DigitalInput(sPortConstants.nariIndexerFrontInfraredDio),
				topInfrared = new DigitalInput(sPortConstants.nariIndexerTopInfraredDio);

		private IndexerHardware() {
		}

		public static IndexerHardware getInstance() {
			if (sInstance == null) sInstance = new IndexerHardware();
			return sInstance;
		}
	}

	/**
	 * 1 775 (controlled by Talon SRX), 2 Solenoids
	 */
	public static class IntakeHardware {

		private static IntakeHardware sInstance;
		public final Talon talon = new Talon(sPortConstants.nariIntakeId, "Intake");
		public final TimedSolenoid solenoid = new TimedSolenoid(sPortConstants.nariIntakeSolenoidId, 1.0, false);

		private IntakeHardware() {
		}

		public static IntakeHardware getInstance() {
			if (sInstance == null) sInstance = new IntakeHardware();
			return sInstance;
		}
	}

	/**
	 * 1 WS2812B LED Strip connected to roboRIO via PWM
	 */
	public static class LightingHardware {

		private static LightingHardware sInstance;
		public final AddressableLED ledStrip = new AddressableLED(sPortConstants.nariLightingPwmPort);

		private LightingHardware() {
		}

		public static LightingHardware getInstance() {
			if (sInstance == null) sInstance = new LightingHardware();
			return sInstance;
		}
	}

	/**
	 * 2 NEO (controlled by Spark MAX), 3 Solenoids
	 */
	public static class ShooterHardware {

		private static ShooterHardware sInstance;
		public final Spark masterSpark = new Spark(sPortConstants.nariShooterMasterId, "Shooter Master"),
				slaveSpark = new Spark(sPortConstants.nariShooterSlaveId, "Shooter Slave");
		public final CANEncoder masterEncoder = masterSpark.getEncoder();
		public final TimedSolenoid hoodSolenoid = new TimedSolenoid(sPortConstants.nariShooterHoodSolenoid, 0.4, true),
				blockingSolenoid = new TimedSolenoid(sPortConstants.nariShooterBlockingSolenoidId, 0.2, false);

		private ShooterHardware() {
		}

		public static ShooterHardware getInstance() {
			if (sInstance == null) sInstance = new ShooterHardware();
			return sInstance;
		}
	}

	/**
	 * 1 775 (controlled by Talon SRX), 1 Color Sensor V3
	 */
	public static class SpinnerHardware {

		private static SpinnerHardware sInstance = new SpinnerHardware();
		public final Talon talon = new Talon(sPortConstants.nariSpinnerId, "Spinner");
		public final ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);

		private SpinnerHardware() {
		}

		public static SpinnerHardware getInstance() {
			if (sInstance == null) sInstance = new SpinnerHardware();
			return sInstance;
		}
	}

	/**
	 * 1 Compressor, 1 PDP, 1 Fisheye USB Camera
	 */
	public static class MiscellaneousHardware {

		private static MiscellaneousHardware sInstance;
		public final Compressor compressor = new Compressor();
		public final PowerDistributionPanel pdp = new PowerDistributionPanel();
//		final UsbCamera fisheyeCam = CameraServer.getInstance().startAutomaticCapture();

		private MiscellaneousHardware() {
			compressor.stop();
		}

		public static MiscellaneousHardware getInstance() {
			if (sInstance == null) sInstance = new MiscellaneousHardware();
			return sInstance;
		}
	}

	/**
	 * 2 Joysticks, 1 Xbox Controller
	 */
	public static class Joysticks {

		private static final Joysticks sInstance = new Joysticks();
		final Joystick driveStick = new Joystick(0), turnStick = new Joystick(1);
		final XboxController operatorXboxController = new XboxController(2);

		private Joysticks() {
		}

		static Joysticks getInstance() {
			return sInstance;
		}
	}

	private static final PortConstants sPortConstants = Configs.get(PortConstants.class);

	private HardwareAdapter() {
	}
}
