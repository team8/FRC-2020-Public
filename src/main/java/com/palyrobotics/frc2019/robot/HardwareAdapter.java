package com.palyrobotics.frc2019.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.Constants.PortConstants;
import com.palyrobotics.frc2019.util.XboxController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.*;

/**
 * Represents all hardware components of the robot. Singleton class. Should only be used in robot package, and 254lib. Subdivides hardware into subsystems.
 * Example call: HardwareAdapter.getInstance().getDrivetrain().getLeftMotor()
 *
 * @author Nihar
 */
public class HardwareAdapter {
	//Hardware components at the top for maintenance purposes, variables and getters at bottom
	/**
	 * DRIVETRAIN - 6 CANSparkMax, 1 Gyro
	 */
	public static class DrivetrainHardware {
		private static DrivetrainHardware instance = new DrivetrainHardware();

		private static DrivetrainHardware getInstance() {
			return instance;
		}

		public final CANSparkMax leftMasterSpark;
		public final CANSparkMax leftSlave1Spark;
		public final CANSparkMax leftSlave2Spark;
        public final CANSparkMax rightMasterSpark;
		public final CANSparkMax rightSlave1Spark;
		public final CANSparkMax rightSlave2Spark;
		
		public final PigeonIMU gyro;

		public static void resetSensors() {
			instance.gyro.setYaw(0, 0);
			instance.gyro.setFusedHeading(0, 0);
			instance.gyro.setAccumZAngle(0, 0);
		}

		protected DrivetrainHardware() {
			leftMasterSpark = new CANSparkMax(PortConstants.kVidarLeftDriveMasterDeviceID, CANSparkMaxLowLevel.MotorType.kBrushless);
			leftSlave1Spark = new CANSparkMax(PortConstants.kVidarLeftDriveSlave1DeviceID, CANSparkMaxLowLevel.MotorType.kBrushless);
			leftSlave2Spark = new CANSparkMax(PortConstants.kVidarLeftDriveSlave2DeviceID, CANSparkMaxLowLevel.MotorType.kBrushless);
            rightMasterSpark = new CANSparkMax(PortConstants.kVidarRightDriveMasterDeviceID, CANSparkMaxLowLevel.MotorType.kBrushless);
			rightSlave1Spark = new CANSparkMax(PortConstants.kVidarRightDriveSlave1DeviceID, CANSparkMaxLowLevel.MotorType.kBrushless);
			rightSlave2Spark = new CANSparkMax(PortConstants.kVidarRightDriveSlave2DeviceID, CANSparkMaxLowLevel.MotorType.kBrushless);

			gyro = new PigeonIMU(PortConstants.kGyroPort);
		}
	}

    /**
     *  Elevator - 2 CANSparkMax, 1 HFX, 1 Encoder, 1 DoubleSolenoid
     */
    public static class ElevatorHardware {
        private static ElevatorHardware instance = new ElevatorHardware();

        private static ElevatorHardware getInstance() {
            return instance;
        }

        public final CANSparkMax elevatorMasterSpark;
        public final CANSparkMax elevatorSlaveSpark;
        public final DoubleSolenoid elevatorDoubleSolenoid;
        public final DigitalInput elevatorHFX;

        protected ElevatorHardware() {
            elevatorMasterSpark = new CANSparkMax(PortConstants.kVidarElevatorMasterSparkID, CANSparkMaxLowLevel.MotorType.kBrushless);
            elevatorSlaveSpark = new CANSparkMax(PortConstants.kVidarElevatorSlaveSparkID, CANSparkMaxLowLevel.MotorType.kBrushless);
            elevatorDoubleSolenoid = new DoubleSolenoid(PortConstants.kVidarElevatorDoubleSolenoidForwardsID, PortConstants.kVidarElevatorDoubleSolenoidReverseID);
			elevatorHFX = new DigitalInput(PortConstants.kElevatorHFXPort);
        }
    }

	/**
	 * Intake - 2 CANSparkMax, 1 WPI_VictorSPX, 2 Ultrasonics
	 */
	public static class IntakeHardware {
		private static IntakeHardware instance = new IntakeHardware();

		private static IntakeHardware getInstance() {
			return instance;
		}

		public final WPI_VictorSPX intakeVictor;
		public final CANSparkMax intakeMasterSpark;
		public final CANSparkMax intakeSlaveSpark;
		public final Ultrasonic ultrasonic1;
		public final Ultrasonic ultrasonic2;
		public final AnalogPotentiometer potentiometer;

		protected IntakeHardware() {
			intakeVictor = new WPI_VictorSPX(PortConstants.kVidarIntakeVictorDeviceID);
			intakeMasterSpark = new CANSparkMax(PortConstants.kVidarIntakeMasterDeviceID, CANSparkMaxLowLevel.MotorType.kBrushless);
			intakeSlaveSpark = new CANSparkMax(PortConstants.kVidarIntakeSlaveDeviceID, CANSparkMaxLowLevel.MotorType.kBrushless);
			ultrasonic1 = new Ultrasonic(OtherConstants.kLeftUltrasonicPing, OtherConstants.kLeftUltrasonicEcho);
			ultrasonic2 = new Ultrasonic(OtherConstants.kRightUltrasonicPing,OtherConstants.kRightUltrasonicEcho);
			potentiometer = new AnalogPotentiometer(PortConstants.kVidarAnalogPot);
		}
	}

	/**
	 * Pusher - 1 WPI_VictorSPX, 2 Ultrasonics
	 */
	public static class PusherHardware {
		private static PusherHardware instance = new PusherHardware();

		private static PusherHardware getInstance() { return instance; }

		public final WPI_VictorSPX pusherVictor;
		public final Ultrasonic pusherUltrasonicRight;
		public final Ultrasonic pusherUltrasonicLeft;
		public final AnalogPotentiometer pusherPotentiometer;

		protected PusherHardware() {
			pusherVictor = new WPI_VictorSPX(PortConstants.kVidarPusherVictorID);
			pusherUltrasonicRight = new Ultrasonic(OtherConstants.kVidarPusherRightUltrasonicPing, OtherConstants.kVidarPusherRightUltrasonicEcho);
			pusherUltrasonicLeft = new Ultrasonic(OtherConstants.kVidarPusherLeftUltrasonicPing, OtherConstants.kVidarPusherLeftUltrasonicEcho);
			pusherPotentiometer = new AnalogPotentiometer(PortConstants.kVidarPusherPotID, 360, 0);
		}
	}

	public static class ShooterHardware{
		private static ShooterHardware instance = new ShooterHardware();

		private static ShooterHardware getInstance() {
		    return instance;
		}

		public final WPI_VictorSPX shooterMasterVictor;
		public final WPI_VictorSPX shooterSlaveVictor;

		protected ShooterHardware() {
			shooterMasterVictor = new WPI_VictorSPX(PortConstants.kVidarShooterMasterVictorDeviceID);
			shooterSlaveVictor = new WPI_VictorSPX(PortConstants.kVidarShooterSlaveVictorDeviceID);
		}
	}

	/**
	 * Hatch Intake - 1 WPI_VictorSPX, 1 SingleSolenoid
	 */
	public static class ShovelHardware {
		private static ShovelHardware instance = new ShovelHardware();

		private static ShovelHardware getInstance() {
		    return instance;
		}

		public final WPI_VictorSPX ShovelVictor;
		public final Solenoid upDownSolenoid;
		public final DigitalInput upDownHFX;

		protected ShovelHardware() {
			ShovelVictor = new WPI_VictorSPX(PortConstants.kVidarShovelDeviceID);
			upDownSolenoid = new Solenoid(0, PortConstants.kVidarShovelSolenoidUpDownID);
			upDownHFX = new DigitalInput(PortConstants.kVidarShovelHFXPort);
		}
	}

	public static class FingersHardware{
		private static FingersHardware instance = new FingersHardware();

		public static FingersHardware getInstance() { return instance; }

		public final DoubleSolenoid openCloseSolenoid;
		public final DoubleSolenoid expelSolenoid;

		protected FingersHardware() {
			openCloseSolenoid = new DoubleSolenoid(PortConstants.kVidarOpenCloseSolenoidForwardID, PortConstants.kVidarOpenCloseSolenoidReverseID);
			expelSolenoid = new DoubleSolenoid(PortConstants.kVidarExpelSolenoidForwardID, PortConstants.kVidarExpelSolenoidReverseID);
		}
	}

	//Joysticks for operator interface
	public static class Joysticks {
		private static Joysticks instance = new Joysticks();

		private static Joysticks getInstance() {
			return instance;
		}

		public final Joystick driveStick = new Joystick(0);
		public final Joystick turnStick = new Joystick(1);
		public final Joystick backupStick = new Joystick(2);
		public XboxController operatorXboxController = null;

		protected Joysticks() {
			if(OtherConstants.operatorXBoxController) {
				operatorXboxController = new XboxController(2, false);
			}
		}
	}

    /**
     * Miscellaneous Hardware - Compressor sensor(Analog Input), Compressor, PDP
     */
	public static class MiscellaneousHardware {
	    private static MiscellaneousHardware instance = new MiscellaneousHardware();

	    private static MiscellaneousHardware getInstance() {
	        return instance;
        }

        public final Compressor compressor;
		public final PowerDistributionPanel pdp;

        protected MiscellaneousHardware() {
            compressor = new Compressor();
            pdp = new PowerDistributionPanel();
        }

    }

	//Wrappers to access hardware groups
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

	public ShovelHardware getShovel() {
	    return ShovelHardware.getInstance();
	}

	public PusherHardware getPusher() { return PusherHardware.getInstance(); }

	public FingersHardware getFingers() { return FingersHardware.getInstance(); }

	public Joysticks getJoysticks() {
		return Joysticks.getInstance();
	}

	public MiscellaneousHardware getMiscellaneousHardware() {
	    return MiscellaneousHardware.getInstance();
    }

	//Singleton set up
	private static final HardwareAdapter instance = new HardwareAdapter();

	public static HardwareAdapter getInstance() {
		return instance;
	}
}