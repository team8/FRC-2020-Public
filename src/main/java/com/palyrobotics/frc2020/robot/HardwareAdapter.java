package com.palyrobotics.frc2020.robot;

import java.util.List;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2020.config.PortConstants;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.DualSolenoid;
import com.palyrobotics.frc2020.util.control.Falcon;
import com.palyrobotics.frc2020.util.control.Spark;
import com.palyrobotics.frc2020.util.control.Talon;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.revrobotics.CANEncoder;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.*;

/**
 * Represents all hardware components of the robot. Singleton class. Should only
 * be used in robot package. Subdivides hardware into subsystems.
 */
public class HardwareAdapter {

	/**
	 * 1 NEO, 1 NEO 550 (both controlled by Spark MAX), 1 Solenoid
	 */
	static class ClimberHardware {

		private static ClimberHardware sInstance = new ClimberHardware();
		final Spark verticalSpark = new Spark(sPortConstants.nariClimberVerticalId);
		final Spark horizontalSpark = new Spark(sPortConstants.nariClimberHorizontalId);
		final Solenoid solenoid = new Solenoid(sPortConstants.nariClimberSolenoidId);

		ClimberHardware() {
		}

		static ClimberHardware getInstance() {
			return sInstance;
		}
	}

	/**
	 * 4 Falcon 500s (controlled by Talon FX), 1 Pigeon IMU Gyro connected via Talon
	 * SRX data cable.
	 */
	static class DrivetrainHardware {

		private static DrivetrainHardware sInstance = new DrivetrainHardware();

		final Falcon leftMasterFalcon = new Falcon(sPortConstants.nariDriveLeftMasterId),
				leftSlaveFalcon = new Falcon(sPortConstants.nariDriveLeftSlaveId);
		final Falcon rightMasterFalcon = new Falcon(sPortConstants.nariDriveRightMasterId),
				rightSlaveFalcon = new Falcon(sPortConstants.nariDriveRightSlaveId);

		final List<Falcon> falcons = List.of(leftMasterFalcon, leftSlaveFalcon, rightMasterFalcon, rightSlaveFalcon);

		// TODO: Update this
		final PigeonIMU gyro = new PigeonIMU(new WPI_TalonSRX(8));

		private DrivetrainHardware() {
		}

		static DrivetrainHardware getInstance() {
			return sInstance;
		}
	}

	/**
	 * 2 NEOs (controlled by Spark MAX), 3 Solenoids, 3 Ultrasonics
	 */
	static class IndexerHardware {

		private static IndexerHardware sInstance = new IndexerHardware();
		final Spark horizontalSpark = new Spark(sPortConstants.nariIndexerHorizontalId),
				verticalSpark = new Spark(sPortConstants.nariIndexerVerticalId);
		final Solenoid hopperSolenoid = new Solenoid(sPortConstants.nariIndexerHopperSolenoidId);
		final Solenoid blockingSolenoid = new Solenoid(sPortConstants.nariIndexerBlockingSolenoidId);
		final Ultrasonic backUltrasonic = new Ultrasonic(sPortConstants.nariIndexerBackUltrasonicPing,
				sPortConstants.nariIndexerBackUltrasonicEcho),
				frontUltrasonic = new Ultrasonic(sPortConstants.nariIndexerFrontUltrasonicPing,
						sPortConstants.nariIndexerFrontUltrasonicEcho),
				topUltrasonic = new Ultrasonic(sPortConstants.nariIndexerTopUltrasonicPing,
						sPortConstants.nariIndexerTopUltrasonicEcho);

		private IndexerHardware() {
		}

		static IndexerHardware getInstance() {
			return sInstance;
		}
	}

	/**
	 * 1 775 (controlled by Talon SRX), 2 Solenoids
	 */
	static class IntakeHardware {

		private static IntakeHardware sInstance = new IntakeHardware();
		final Talon talon = new Talon(sPortConstants.nariIntakeId);
		final Solenoid upDownSolenoid = new Solenoid(sPortConstants.nariIntakeSolenoidId);

		private IntakeHardware() {
		}

		static IntakeHardware getInstance() {
			return sInstance;
		}
	}

	/**
	 * 2 NEO (controlled by Spark MAX), 3 Solenoids
	 */
	static class ShooterHardware {

		private static ShooterHardware sInstance = new ShooterHardware();
		final Spark masterSpark = new Spark(sPortConstants.nariShooterMasterId),
				slaveSpark = new Spark(sPortConstants.nariShooterSlaveId);
		final CANEncoder masterEncoder = masterSpark.getEncoder();
		final DualSolenoid upDownSolenoid = new DualSolenoid(sPortConstants.nariShooterExtendingSolenoidId,
				sPortConstants.nariShooterRetractingSolenoidId);
		// TODO: add proper second PCM CAN ID
		final Solenoid blockingSolenoid = new Solenoid(1, sPortConstants.nariShooterBlockingSolenoidId);

		private ShooterHardware() {
		}

		static ShooterHardware getInstance() {
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
			return sInstance;
		}
	}

	/**
	 * 1Compressor, 1 PDP
	 */
	static class MiscellaneousHardware {

		private static MiscellaneousHardware sInstance = new MiscellaneousHardware();
		final Compressor compressor = new Compressor();
		final PowerDistributionPanel pdp = new PowerDistributionPanel();

		private MiscellaneousHardware() {
		}

		static MiscellaneousHardware getInstance() {
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
