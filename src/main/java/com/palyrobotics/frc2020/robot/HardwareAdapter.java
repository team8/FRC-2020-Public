package com.palyrobotics.frc2020.robot;

import java.util.List;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2020.config.PortConstants;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.Falcon;
import com.palyrobotics.frc2020.util.control.Spark;
import com.palyrobotics.frc2020.util.control.Talon;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.revrobotics.CANEncoder;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Represents all hardware components of the robot. Singleton class. Should only
 * be used in robot package. Subdivides hardware into subsystems.
 *
 * @author Nihar
 */
public class HardwareAdapter {

	/**
	 * 6 Spark Maxes, 1 Pigeon Gyro via TalonSRX data cable.
	 */
	static class DrivetrainHardware {

		private static DrivetrainHardware sInstance = new DrivetrainHardware();
		final Spark leftMasterSpark = new Spark(sPortConstants.vidarLeftDriveMasterDeviceId),
				leftSlave1Spark = new Spark(sPortConstants.vidarLeftDriveSlave1DeviceId),
				leftSlave2Spark = new Spark(sPortConstants.vidarLeftDriveSlave2DeviceId),
				rightMasterSpark = new Spark(sPortConstants.vidarRightDriveMasterDeviceId),
				rightSlave1Spark = new Spark(sPortConstants.vidarRightDriveSlave1DeviceId),
				rightSlave2Spark = new Spark(sPortConstants.vidarRightDriveSlave2DeviceId);
		final CANEncoder leftMasterEncoder = leftMasterSpark.getEncoder(),
				rightMasterEncoder = rightMasterSpark.getEncoder();
		final List<Spark> sparks = List.of(leftMasterSpark, leftSlave1Spark, leftSlave2Spark, rightMasterSpark,
				rightSlave1Spark, rightSlave2Spark);
		final PigeonIMU gyro = new PigeonIMU(new WPI_TalonSRX(8));

		private DrivetrainHardware() {
		}

		static DrivetrainHardware getInstance() {
			return sInstance;
		}
	}

	static class FalconDrivetrainHardware {

		private static FalconDrivetrainHardware sInstance = new FalconDrivetrainHardware();

		Falcon leftMasterFalcon, leftSlaveFalcon, rightMasterFalcon, rightSlaveFalcon;

		private List<Falcon> falcons = List.of(leftMasterFalcon, leftSlaveFalcon, rightMasterFalcon, rightSlaveFalcon);

		private FalconDrivetrainHardware() {
		}

		static FalconDrivetrainHardware getInstance() {
			return sInstance;
		}
	}

	static class IntakeHardware {

		private static IntakeHardware sInstance = new IntakeHardware();
		final Talon intakeTalon = new Talon(sPortConstants.intakeTalonDeviceId);

		private IntakeHardware() {
		}

		static IntakeHardware getInstance() {
			return sInstance;
		}
	}

	static class IndexerHardware {

		private static IndexerHardware sInstance = new IndexerHardware();
		final Spark indexerHorizontalSpark = new Spark(sPortConstants.vidarIndexerHorizontalDeviceID),
				indexerVerticalSpark = new Spark(sPortConstants.vidarIndexerVerticalDeviceID);

		private IndexerHardware() {
		}

		static IndexerHardware getInstance() {
			return sInstance;
		}
	}

	static class ClimberHardware {

		private static ClimberHardware sInstance = new ClimberHardware();
		final Spark climberMainSpark = new Spark(sPortConstants.climberMainDeviceID);
		final Spark climberAdjustingSpark = new Spark(sPortConstants.climberAdjustingDeviceID);

		ClimberHardware() {
		}

		static ClimberHardware getInstance() {
			return sInstance;
		}
	}

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

	static class SpinnerHardware {

		private static SpinnerHardware sInstance = new SpinnerHardware();
		final Talon spinnerTalon = new Talon(sPortConstants.spinnerTalonDeviceId);
		final ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);

		private SpinnerHardware() {
		}

		static SpinnerHardware getInstance() {
			return sInstance;
		}
	}

	/**
	 * Compressor sensor (Analog Input), Compressor, PDP, Camera.
	 */
	static class MiscellaneousHardware {

		private static MiscellaneousHardware sInstance = new MiscellaneousHardware();
		final Compressor compressor = new Compressor();
		final PowerDistributionPanel pdp = new PowerDistributionPanel();

		private MiscellaneousHardware() {
		}
		// final UsbCamera fisheyeCam =
		// CameraServer.getInstance().startAutomaticCapture();

		static MiscellaneousHardware getInstance() {
			return sInstance;
		}
	}

	private static final PortConstants sPortConstants = Configs.get(PortConstants.class);

	private HardwareAdapter() {
	}
}
