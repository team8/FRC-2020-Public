package com.palyrobotics.frc2020.robot;

import java.util.List;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2020.config.PortConstants;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Falcon;
import com.palyrobotics.frc2020.util.control.Spark;
import com.palyrobotics.frc2020.util.control.Talon;
import com.palyrobotics.frc2020.util.control.TimedSolenoid;
import com.palyrobotics.frc2020.util.input.Joystick;
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
	 * 1 NEO, 1 NEO 550 (both controlled by Spark MAX), 1 Solenoid
	 */
	static class ClimberHardware {

		private static ClimberHardware sInstance;
		final Spark verticalSpark = new Spark(sPortConstants.nariClimberVerticalId),
				horizontalSpark = new Spark(sPortConstants.nariClimberHorizontalId);
		final CANEncoder verticalSparkEncoder = verticalSpark.getEncoder(),
				horizontalSparkEncoder = horizontalSpark.getEncoder();
		final TimedSolenoid solenoid = new TimedSolenoid(sPortConstants.nariClimberSolenoidId, 0.2, true);

		ClimberHardware() {
		}

		static ClimberHardware getInstance() {
			if (sInstance == null) sInstance = new ClimberHardware();
			return sInstance;
		}
	}

	/**
	 * 4 Falcon 500s (controlled by Talon FX), 1 Pigeon IMU Gyro connected via Talon SRX data cable.
	 */
	static class DrivetrainHardware {

		private static DrivetrainHardware sInstance;

		final Falcon leftMasterFalcon = new Falcon(sPortConstants.nariDriveLeftMasterId),
				leftSlaveFalcon = new Falcon(sPortConstants.nariDriveLeftSlaveId);
		final Falcon rightMasterFalcon = new Falcon(sPortConstants.nariDriveRightMasterId),
				rightSlaveFalcon = new Falcon(sPortConstants.nariDriveRightSlaveId);

		final List<Falcon> falcons = List.of(leftMasterFalcon, leftSlaveFalcon,
				rightMasterFalcon, rightSlaveFalcon);

		final PigeonIMU gyro = new PigeonIMU(new WPI_TalonSRX(8));

		private DrivetrainHardware() {
		}

		static DrivetrainHardware getInstance() {
			if (sInstance == null) sInstance = new DrivetrainHardware();
			return sInstance;
		}
	}

	/**
	 * 2 NEOs (controlled by Spark MAX), 3 Single Solenoids, 3 Infrared Sensors
	 */
	static class IndexerHardware {

		private static IndexerHardware sInstance;
		final Spark masterSpark = new Spark(sPortConstants.nariIndexerMasterId),
				slaveSpark = new Spark(sPortConstants.nariIndexerSlaveId);
		final Talon talon = new Talon(sPortConstants.nariIndexerTalonId);
		final TimedSolenoid hopperSolenoid = new TimedSolenoid(sPortConstants.nariIndexerHopperSolenoidId, 0.8, true),
				blockingSolenoid = new TimedSolenoid(sPortConstants.nariIndexerBlockingSolenoidId, 0.2, true);
		final DigitalInput backInfrared = new DigitalInput(sPortConstants.nariIndexerBackInfraredDio),
				frontInfrared = new DigitalInput(sPortConstants.nariIndexerFrontInfraredDio),
				topInfrared = new DigitalInput(sPortConstants.nariIndexerTopInfraredDio);

		private IndexerHardware() {
		}

		static IndexerHardware getInstance() {
			if (sInstance == null) sInstance = new IndexerHardware();
			return sInstance;
		}
	}

	/**
	 * 1 775 (controlled by Talon SRX), 2 Solenoids
	 */
	static class IntakeHardware {

		private static IntakeHardware sInstance;
		final Talon talon = new Talon(sPortConstants.nariIntakeId);
		final TimedSolenoid solenoid = new TimedSolenoid(sPortConstants.nariIntakeSolenoidId, 1.0, false);

		private IntakeHardware() {
		}

		static IntakeHardware getInstance() {
			if (sInstance == null) sInstance = new IntakeHardware();
			return sInstance;
		}
	}

	/**
	 * 2 NEO (controlled by Spark MAX), 3 Solenoids
	 */
	static class ShooterHardware {

		private static ShooterHardware sInstance;
		final Spark masterSpark = new Spark(sPortConstants.nariShooterMasterId),
				slaveSpark = new Spark(sPortConstants.nariShooterSlaveId);
		final CANEncoder masterEncoder = masterSpark.getEncoder();
		final TimedSolenoid hoodSolenoid = new TimedSolenoid(sPortConstants.nariShooterHoodSolenoid, 0.3, true),
				blockingSolenoid = new TimedSolenoid(sPortConstants.nariShooterBlockingSolenoidId, 0.1, false);

		private ShooterHardware() {
		}

		static ShooterHardware getInstance() {
			if (sInstance == null) sInstance = new ShooterHardware();
			return sInstance;
		}
	}

	/**
	 * 1 775 (controlled by Talon SRX), 1 Color Sensor V3
	 */
	static class SpinnerHardware {

		private static SpinnerHardware sInstance = new SpinnerHardware();
		final Talon talon = new Talon(sPortConstants.nariSpinnerId);
		final ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);

		private SpinnerHardware() {
		}

		static SpinnerHardware getInstance() {
			if (sInstance == null) sInstance = new SpinnerHardware();
			return sInstance;
		}
	}

	/**
	 * 1Compressor, 1 PDP
	 */
	static class MiscellaneousHardware {

		private static MiscellaneousHardware sInstance;
		final Compressor compressor = new Compressor();
		final PowerDistributionPanel pdp = new PowerDistributionPanel();

		private MiscellaneousHardware() {
			compressor.stop();
		}

		static MiscellaneousHardware getInstance() {
			if (sInstance == null) sInstance = new MiscellaneousHardware();
			return sInstance;
		}
	}

	/**
	 * 2 Joysticks, 1 Xbox Controller
	 */
	static class Joysticks {

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
