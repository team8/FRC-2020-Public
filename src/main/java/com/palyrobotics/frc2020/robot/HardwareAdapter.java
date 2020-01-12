package com.palyrobotics.frc2020.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2020.config.PortConstants;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.SparkMax;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.revrobotics.CANEncoder;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

import java.util.List;

/**
 * Represents all hardware components of the robot. Singleton class.
 * Should only be used in robot package. Subdivides hardware into subsystems.
 *
 * @author Nihar
 */
public class HardwareAdapter {

    private static final PortConstants sPortConstants = Configs.get(PortConstants.class);

    static HardwareAdapter getInstance() {
        return sInstance;
    }

    private static final HardwareAdapter sInstance = new HardwareAdapter();

    // Wrappers to access hardware groups
    DrivetrainHardware getDrivetrainHardware() {
        return DrivetrainHardware.getInstance();
    }

    IntakeHardware getIntakeHardware() {
        return IntakeHardware.getInstance();
    }

    Joysticks getJoysticks() {
        return Joysticks.getInstance();
    }

    MiscellaneousHardware getMiscellaneousHardware() {
        return MiscellaneousHardware.getInstance();
    }

    SpinnerHardware getSpinnerHardware() {
        return SpinnerHardware.getInstance();
    }

    /**
     * DRIVETRAIN - 6 CANSparkMax, 1 Gyro via TalonSRX data cable
     */
    static class DrivetrainHardware {

        private static DrivetrainHardware sInstance = new DrivetrainHardware();

        private static DrivetrainHardware getInstance() {
            return sInstance;
        }

        final SparkMax
                leftMasterSpark, leftSlave1Spark, leftSlave2Spark,
                rightMasterSpark, rightSlave1Spark, rightSlave2Spark;
        final CANEncoder leftMasterEncoder, rightMasterEncoder;
        final List<SparkMax> sparks;

        final PigeonIMU gyro;

        DrivetrainHardware() {
            leftMasterSpark = new SparkMax(sPortConstants.vidarLeftDriveMasterDeviceId);
            leftSlave1Spark = new SparkMax(sPortConstants.vidarLeftDriveSlave1DeviceId);
            leftSlave2Spark = new SparkMax(sPortConstants.vidarLeftDriveSlave2DeviceId);
            rightMasterSpark = new SparkMax(sPortConstants.vidarRightDriveMasterDeviceId);
            rightSlave1Spark = new SparkMax(sPortConstants.vidarRightDriveSlave1DeviceId);
            rightSlave2Spark = new SparkMax(sPortConstants.vidarRightDriveSlave2DeviceId);
            leftMasterEncoder = leftMasterSpark.getEncoder();
            rightMasterEncoder = rightMasterSpark.getEncoder();
            sparks = List.of(leftMasterSpark, leftSlave1Spark, leftSlave2Spark, rightMasterSpark, rightSlave1Spark, rightSlave2Spark);
            gyro = new PigeonIMU(new WPI_TalonSRX(8));
        }
    }

    static class IntakeHardware {

        private static IntakeHardware sInstance = new IntakeHardware();

        private static IntakeHardware getInstance() {
            return sInstance;
        }

        final WPI_VictorSPX intakeVictor;

        IntakeHardware() {
            intakeVictor = new WPI_VictorSPX(sPortConstants.vidarIntakeDeviceId);
        }
    }

    static class Joysticks {

        private static final Joysticks sInstance = new Joysticks();

        private static Joysticks getInstance() {
            return sInstance;
        }

        final Joystick driveStick = new Joystick(0), turnStick = new Joystick(1);
        final XboxController operatorXboxController = new XboxController(2);
    }

    static class SpinnerHardware {

        private static SpinnerHardware sInstance = new SpinnerHardware();

        private static SpinnerHardware getInstance() {
            return sInstance;
        }

        final WPI_TalonSRX spinnerTalon;
        final ColorSensorV3 colorSensor;

        SpinnerHardware() {
            colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
            spinnerTalon = new WPI_TalonSRX(sPortConstants.spinnerTalonDeviceId);
        }
    }

    /**
     * Miscellaneous Hardware - Compressor sensor(Analog Input), Compressor, PDP
     */
    static class MiscellaneousHardware {

        private static MiscellaneousHardware sInstance = new MiscellaneousHardware();

        private static MiscellaneousHardware getInstance() {
            return sInstance;
        }

        final Compressor compressor;
        final PowerDistributionPanel pdp;
        // final UsbCamera fisheyeCam;

        MiscellaneousHardware() {
            compressor = new Compressor();
            pdp = new PowerDistributionPanel();
            // fisheyeCam = CameraServer.getInstance().startAutomaticCapture();
        }
    }
}