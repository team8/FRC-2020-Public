package com.palyrobotics.frc2020.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2020.config.PortConstants;
import com.palyrobotics.frc2020.config.constants.OtherConstants;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.LazySparkMax;
import com.palyrobotics.frc2020.util.input.Joystick;
import com.palyrobotics.frc2020.util.input.XboxController;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANEncoder;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.*;

import java.util.List;

/**
 * Represents all hardware components of the robot. Singleton class. Should only be used in robot package, and 254lib. Subdivides hardware into subsystems.
 * Example call: HardwareAdapter.getInstance().getDrivetrain().getLeftMotor()
 *
 * @author Nihar
 */
public class HardwareAdapter {

    private static final PortConstants sPortConstants = Configs.get(PortConstants.class);
    // Hardware components at the top for maintenance purposes, variables and getters at bottom

    private static final HardwareAdapter sInstance = new HardwareAdapter();

    public static HardwareAdapter getInstance() {
        return sInstance;
    }

    // Wrappers to access hardware groups
    public DrivetrainHardware getDrivetrain() {
        return DrivetrainHardware.getInstance();
    }

    Joysticks getJoysticks() {
        return Joysticks.getInstance();
    }

    MiscellaneousHardware getMiscellaneousHardware() {
        return MiscellaneousHardware.getInstance();
    }

    /**
     * DRIVETRAIN - 6 CANSparkMax, 1 Gyro
     */
    public static class DrivetrainHardware {
        private static DrivetrainHardware sInstance = new DrivetrainHardware();
        final LazySparkMax
                leftMasterSpark, leftSlave1Spark, leftSlave2Spark,
                rightMasterSpark, rightSlave1Spark, rightSlave2Spark;
        final CANEncoder leftMasterEncoder, rightMasterEncoder;
        final List<LazySparkMax> sparks;

        final PigeonIMU gyro;

        DrivetrainHardware() {
            leftMasterSpark = new LazySparkMax(sPortConstants.vidarLeftDriveMasterDeviceID);
            leftSlave1Spark = new LazySparkMax(sPortConstants.vidarLeftDriveSlave1DeviceID);
            leftSlave2Spark = new LazySparkMax(sPortConstants.vidarLeftDriveSlave2DeviceID);
            rightMasterSpark = new LazySparkMax(sPortConstants.vidarRightDriveMasterDeviceID);
            rightSlave1Spark = new LazySparkMax(sPortConstants.vidarRightDriveSlave1DeviceID);
            rightSlave2Spark = new LazySparkMax(sPortConstants.vidarRightDriveSlave2DeviceID);
            leftMasterEncoder = leftMasterSpark.getEncoder();
            rightMasterEncoder = rightMasterSpark.getEncoder();
            sparks = List.of(leftMasterSpark, leftSlave1Spark, leftSlave2Spark, rightMasterSpark, rightSlave1Spark, rightSlave2Spark);
            gyro = new PigeonIMU(new WPI_TalonSRX(8));
        }

        private static DrivetrainHardware getInstance() {
            return sInstance;
        }
    }

    public static class Joysticks {
        private static Joysticks sInstance = new Joysticks();
        final Joystick driveStick = new Joystick(0), turnStick = new Joystick(1);
        //        public final Joystick backupStick = new Joystick(3);
        XboxController operatorXboxController;

        Joysticks() {
            if (OtherConstants.operatorXBoxController) {
                operatorXboxController = new XboxController(2);
            }
        }

        private static Joysticks getInstance() {
            return sInstance;
        }
    }

    /**
     * Miscellaneous Hardware - Compressor sensor(Analog Input), Compressor, PDP
     */
    public static class MiscellaneousHardware {
        private static MiscellaneousHardware sInstance = new MiscellaneousHardware();
        final Compressor compressor;
        final PowerDistributionPanel pdp;
        // final UsbCamera fisheyeCam;
        final ColorSensorV3 mColorSensor;

        MiscellaneousHardware() {
            compressor = new Compressor();
            pdp = new PowerDistributionPanel();
            // fisheyeCam = CameraServer.getInstance().startAutomaticCapture();
            mColorSensor = new ColorSensorV3(I2C.Port.kOnboard);

        }

        private static MiscellaneousHardware getInstance() {
            return sInstance;
        }
    }
}