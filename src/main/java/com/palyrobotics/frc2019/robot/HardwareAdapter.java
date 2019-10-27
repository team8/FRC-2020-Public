package com.palyrobotics.frc2019.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2019.config.PortConstants;
import com.palyrobotics.frc2019.config.constants.OtherConstants;
import com.palyrobotics.frc2019.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2019.util.config.Configs;
import com.palyrobotics.frc2019.util.control.LazySparkMax;
import com.palyrobotics.frc2019.util.input.Joystick;
import com.palyrobotics.frc2019.util.input.XboxController;
import com.revrobotics.CANError;
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

    /**
     * DRIVETRAIN - 6 CANSparkMax, 1 Gyro
     */
    public static class DrivetrainHardware {
        private static DrivetrainHardware sInstance = new DrivetrainHardware();

        private static DrivetrainHardware getInstance() {
            return sInstance;
        }

        final LazySparkMax
                leftMasterSpark, leftSlave1Spark, leftSlave2Spark,
                rightMasterSpark, rightSlave1Spark, rightSlave2Spark;

        final List<LazySparkMax> sparks;

        final PigeonIMU gyro;

        public void resetSensors() {
            gyro.setYaw(0, 0);
            gyro.setFusedHeading(0, 0);
            gyro.setAccumZAngle(0, 0);
            sparks.forEach(spark -> spark.getEncoder().setPosition(0.0));
        }

        DrivetrainHardware() {
            leftMasterSpark = new LazySparkMax(sPortConstants.vidarLeftDriveMasterDeviceID);
            leftSlave1Spark = new LazySparkMax(sPortConstants.vidarLeftDriveSlave1DeviceID);
            leftSlave2Spark = new LazySparkMax(sPortConstants.vidarLeftDriveSlave2DeviceID);
            rightMasterSpark = new LazySparkMax(sPortConstants.vidarRightDriveMasterDeviceID);
            rightSlave1Spark = new LazySparkMax(sPortConstants.vidarRightDriveSlave1DeviceID);
            rightSlave2Spark = new LazySparkMax(sPortConstants.vidarRightDriveSlave2DeviceID);
            sparks = List.of(leftMasterSpark, leftSlave1Spark, leftSlave2Spark, rightMasterSpark, rightSlave1Spark, rightSlave2Spark);
            gyro = new PigeonIMU(new WPI_TalonSRX(sPortConstants.vidarShovelDeviceID));
        }
    }

    /**
     * Elevator - 2 CANSparkMax, 1 Encoder, 1 DoubleSolenoid
     */
    static class ElevatorHardware {
        private static ElevatorHardware sInstance = new ElevatorHardware();

        private static ElevatorHardware getInstance() {
            return sInstance;
        }

        final LazySparkMax elevatorMasterSpark, elevatorSlaveSpark;
        final DoubleSolenoid elevatorShifter;

        void resetSensors() {
            sInstance.elevatorMasterSpark.getEncoder().setPosition(0);
            sInstance.elevatorSlaveSpark.getEncoder().setPosition(0);
        }

        ElevatorHardware() {
            elevatorMasterSpark = new LazySparkMax(sPortConstants.vidarElevatorMasterSparkID);
            elevatorSlaveSpark = new LazySparkMax(sPortConstants.vidarElevatorSlaveSparkID);
            elevatorShifter = new DoubleSolenoid(0, sPortConstants.vidarElevatorDoubleSolenoidForwardsID, sPortConstants.vidarElevatorDoubleSolenoidReverseID);
//            elevatorHolderSolenoid = new Solenoid(1,PortConstants.vidarElevatorHolderSolenoidID);
        }
    }

    /**
     * Intake - 2 CANSparkMax, 1 WPI_TalonSRX, 2 Ultrasonics
     */
    public static class IntakeHardware {
        private static IntakeHardware sInstance = new IntakeHardware();

        private static IntakeHardware getInstance() {
            return sInstance;
        }

        final WPI_TalonSRX intakeTalon;
        final LazySparkMax intakeMasterSpark;
        final LazySparkMax intakeSlaveSpark;
        final Ultrasonic intakeUltrasonicLeft, intakeUltrasonicRight;
        final AnalogPotentiometer potentiometer;

        /**
         * Set the initial position of the intake arm to be in-line with potentiometer.
         *
         * @return The reading from the arm potentiometer.
         */
        public double calibrateIntakeEncoderWithPotentiometer() {
            IntakeConfig intakeConfig = Configs.get(IntakeConfig.class);
            double intakeStartAngle;
            if (intakeConfig.useBrokenPotFix) {
                intakeStartAngle = intakeConfig.maxAngle;
            } else {
                double
                        maxArmAngle = intakeConfig.maxAngle,
                        potentiometerDegreesPerTick = 1 / IntakeConfig.kArmPotentiometerTicksPerDegree,
                        potentiometerTicks = potentiometer.get() - intakeConfig.potentiometerMaxAngleTicks;
                intakeStartAngle = maxArmAngle - potentiometerDegreesPerTick * Math.abs(potentiometerTicks);
            }
            Robot.getRobotState().intakeStartAngle = intakeStartAngle;
            intakeMasterSpark.getEncoder().setPosition(intakeStartAngle);
            return potentiometer.get();
        }

        IntakeHardware() {
            intakeTalon = new WPI_TalonSRX(sPortConstants.vidarIntakeTalonDeviceID);
            intakeMasterSpark = new LazySparkMax(sPortConstants.vidarIntakeMasterDeviceID);
            intakeSlaveSpark = new LazySparkMax(sPortConstants.vidarIntakeSlaveDeviceID);
            intakeUltrasonicLeft = new Ultrasonic(sPortConstants.vidarIntakeLeftUltrasonicPing, sPortConstants.vidarIntakeLeftUltrasonicEcho);
            intakeUltrasonicRight = new Ultrasonic(sPortConstants.vidarIntakeRightUltrasonicPing, sPortConstants.vidarIntakeRightUltrasonicEcho);
            potentiometer = new AnalogPotentiometer(sPortConstants.vidarAnalogPot);
        }
    }

    /**
     * Pusher - 1 WPI_VictorSPX, 2 Ultrasonics
     */
    public static class PusherHardware {
        private static PusherHardware sInstance = new PusherHardware();

        private static PusherHardware getInstance() {
            return sInstance;
        }

        final LazySparkMax pusherSpark;
        final Ultrasonic pusherUltrasonic;
//		public final Ultrasonic pusherSecondaryUltrasonic;
//		public final AnalogPotentiometer pusherPotentiometer;

        public boolean resetSensors() {
            return sInstance.pusherSpark.getEncoder().setPosition(0.0) == CANError.kOk;
        }

        PusherHardware() {
            pusherSpark = new LazySparkMax(sPortConstants.vidarPusherSparkID);
            pusherUltrasonic = new Ultrasonic(sPortConstants.vidarPusherUltrasonicPing, sPortConstants.vidarPusherUltrasonicEcho);
//			pusherSecondaryUltrasonic = new Ultrasonic(PortConstants.kVidarBackupUltrasonicPing, PortConstants.kVidarBackupUltrasonicEcho);
//			pusherPotentiometer = new AnalogPotentiometer(PortConstants.kVidarPusherPotID, 360, 0);
        }
    }

    public static class ShooterHardware {
        private static ShooterHardware sInstance = new ShooterHardware();

        private static ShooterHardware getInstance() {
            return sInstance;
        }

        final WPI_VictorSPX shooterMasterVictor, shooterSlaveVictor;

        ShooterHardware() {
            shooterMasterVictor = new WPI_VictorSPX(sPortConstants.vidarShooterMasterVictorDeviceID);
            shooterSlaveVictor = new WPI_VictorSPX(sPortConstants.vidarShooterSlaveVictorDeviceID);
        }
    }

    public static class FingersHardware {
        private static FingersHardware sInstance = new FingersHardware();

        public static FingersHardware getInstance() {
            return sInstance;
        }

        final DoubleSolenoid openCloseSolenoid, pusherSolenoid;

        FingersHardware() {
            openCloseSolenoid = new DoubleSolenoid(0, sPortConstants.vidarOpenCloseSolenoidForwardID, sPortConstants.vidarOpenCloseSolenoidReverseID);
            pusherSolenoid = new DoubleSolenoid(0, sPortConstants.vidarExpelSolenoidForwardID, sPortConstants.vidarExpelSolenoidReverseID);
        }
    }

    public static class Joysticks {
        private static Joysticks sInstance = new Joysticks();

        private static Joysticks getInstance() {
            return sInstance;
        }

        final Joystick driveStick = new Joystick(0), turnStick = new Joystick(1);
        //        public final Joystick backupStick = new Joystick(3);
        XboxController operatorXboxController;

        Joysticks() {
            if (OtherConstants.operatorXBoxController) {
                operatorXboxController = new XboxController(2);
            }
        }
    }

    /**
     * Miscellaneous Hardware - Compressor sensor(Analog Input), Compressor, PDP
     */
    public static class MiscellaneousHardware {
        private static MiscellaneousHardware sInstance = new MiscellaneousHardware();

        private static MiscellaneousHardware getInstance() {
            return sInstance;
        }

        final Compressor compressor;
        final PowerDistributionPanel pdp;

        MiscellaneousHardware() {
            compressor = new Compressor();
            pdp = new PowerDistributionPanel();
        }
    }

    // Wrappers to access hardware groups
    public DrivetrainHardware getDrivetrain() {
        return DrivetrainHardware.getInstance();
    }

    public ElevatorHardware getElevator() {
        return ElevatorHardware.getInstance();
    }

    public IntakeHardware getIntake() {
        return IntakeHardware.getInstance();
    }

    public ShooterHardware getShooter() {
        return ShooterHardware.getInstance();
    }

    public PusherHardware getPusher() {
        return PusherHardware.getInstance();
    }

    public FingersHardware getFingers() {
        return FingersHardware.getInstance();
    }

    Joysticks getJoysticks() {
        return Joysticks.getInstance();
    }

    MiscellaneousHardware getMiscellaneousHardware() {
        return MiscellaneousHardware.getInstance();
    }
}