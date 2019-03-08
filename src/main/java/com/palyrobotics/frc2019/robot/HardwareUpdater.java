package com.palyrobotics.frc2019.robot;

import java.util.Optional;
import java.util.logging.Level;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.Constants.ElevatorConstants;
import com.palyrobotics.frc2019.config.Constants.IntakeConstants;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.Constants.PortConstants;
import com.palyrobotics.frc2019.config.Constants.PusherConstants;
import com.palyrobotics.frc2019.config.Constants.ShovelConstants;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Elevator;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.subsystems.Intake;
import com.palyrobotics.frc2019.subsystems.Pusher;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.subsystems.Shovel;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.logger.DataLogger;
import com.palyrobotics.frc2019.util.logger.Logger;
import com.palyrobotics.frc2019.util.loops.Loop;
import com.palyrobotics.frc2019.util.trajectory.Kinematics;
import com.palyrobotics.frc2019.util.trajectory.RigidTransform2d;
import com.palyrobotics.frc2019.util.trajectory.Rotation2d;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * Should only be used in robot package.
 */

class HardwareUpdater {

	//Subsystem references
	private Drive mDrive;
	private Intake mIntake;
	private Elevator mElevator;
	private Shooter mShooter;
	private Pusher mPusher;
	private Shovel mShovel;
	private Fingers mFingers;

	public Loop logLoop = new Loop() {
		@Override
			public void onStart(double timestamp) {

			}
			@Override
			public void onLoop(double timestamp) {
				//Log data here at 200Hz (change write speed in Looper)

				RigidTransform2d pose = RobotState.getInstance().getLatestFieldToVehicle().getValue();
				if (pose == null) pose = new RigidTransform2d();

				// DataLogger.getInstance().logData(Level.FINE, "robot_x", pose.getTranslation().getX());
				// DataLogger.getInstance().logData(Level.FINE, "robot_y", pose.getTranslation().getY());
				// DataLogger.getInstance().logData(Level.FINE, "robot_heading", pose.getRotation().getDegrees());

				// PowerDistributionPanel pdp = HardwareAdapter.getInstance().getMiscellaneousHardware().pdp;

				// DataLogger.getInstance().logData(Level.FINE, "l_1_pdp_current", pdp.getCurrent(0));
				// DataLogger.getInstance().logData(Level.FINE, "l_2_pdp_current", pdp.getCurrent(1));
				// DataLogger.getInstance().logData(Level.FINE, "l_3_pdp_current", pdp.getCurrent(2));
				// DataLogger.getInstance().logData(Level.FINE, "r_1_pdp_current", pdp.getCurrent(15));
				// DataLogger.getInstance().logData(Level.FINE, "r_2_pdp_current", pdp.getCurrent(14));
				// DataLogger.getInstance().logData(Level.FINE, "r_3_pdp_current", pdp.getCurrent(13));

				// CANSparkMax leftMasterSpark = HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark;
				// CANSparkMax leftSlave1Spark = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Spark;
				// CANSparkMax leftSlave2Spark = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Spark;
		
				// CANSparkMax rightMasterSpark = HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark;
				// CANSparkMax rightSlave1Spark = HardwareAdapter.getInstance().getDrivetrain().rightSlave1Spark;
				// CANSparkMax rightSlave2Spark = HardwareAdapter.getInstance().getDrivetrain().rightSlave2Spark;
				
				// DataLogger.getInstance().logData(Level.FINE, "l_1_current", leftMasterSpark.getOutputCurrent());
				// DataLogger.getInstance().logData(Level.FINE, "l_2_current", leftSlave1Spark.getOutputCurrent());
				// DataLogger.getInstance().logData(Level.FINE, "l_3_current", leftSlave2Spark.getOutputCurrent());
				// DataLogger.getInstance().logData(Level.FINE, "r_1_current", rightMasterSpark.getOutputCurrent());
				// DataLogger.getInstance().logData(Level.FINE, "r_2_current", rightSlave1Spark.getOutputCurrent());
				// DataLogger.getInstance().logData(Level.FINE, "r_3_current", rightSlave2Spark.getOutputCurrent());

				// DataLogger.getInstance().logData(Level.FINE, "total_current", pdp.getTotalCurrent());

				// DataLogger.getInstance().logData(Level.FINE, "l_1_voltage", leftMasterSpark.getAppliedOutput());
				// DataLogger.getInstance().logData(Level.FINE, "l_2_voltage", leftSlave1Spark.getAppliedOutput());
				// DataLogger.getInstance().logData(Level.FINE, "l_3_voltage", leftSlave2Spark.getAppliedOutput());
				// DataLogger.getInstance().logData(Level.FINE, "r_1_voltage", rightMasterSpark.getAppliedOutput());
				// DataLogger.getInstance().logData(Level.FINE, "r_2_voltage", rightSlave1Spark.getAppliedOutput());
				// DataLogger.getInstance().logData(Level.FINE, "r_3_voltage", rightSlave2Spark.getAppliedOutput());

				// DataLogger.getInstance().cycle();
			}
			@Override
			public void onStop(double timestamp) {

			}
	};

	/**
	 * Hardware Updater for Vidar
	 */
	protected HardwareUpdater(Drive drive, Elevator elevator, Shooter shooter, Pusher pusher, Shovel shovel, Fingers fingers, Intake intake) {
		this.mDrive = drive;
		this.mElevator = elevator;
		this.mShooter = shooter;
		this.mPusher = pusher;
		this.mShovel = shovel;
		this.mFingers = fingers;
		this.mIntake = intake;
	}

	/**
	 * Initialize all hardware
	 */
	void initHardware() {
		Logger.getInstance().logRobotThread(Level.INFO, "Init hardware");
		configureHardware();
		startUltrasonics();
	}

	void disableSpeedControllers() {
		Logger.getInstance().logRobotThread(Level.INFO, "Disabling sparks");

		//Disable drivetrain sparks
		HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark.disable();
		HardwareAdapter.getInstance().getDrivetrain().leftSlave1Spark.disable();
		HardwareAdapter.getInstance().getDrivetrain().leftSlave2Spark.disable();

		HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark.disable();
		HardwareAdapter.getInstance().getDrivetrain().rightSlave1Spark.disable();
		HardwareAdapter.getInstance().getDrivetrain().rightSlave2Spark.disable();

		//Disable elevator sparks
        HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.disable();
        HardwareAdapter.getInstance().getElevator().elevatorSlaveSpark.disable();

		//Disable intake sparks
		HardwareAdapter.getInstance().getIntake().intakeMasterSpark.disable();
		HardwareAdapter.getInstance().getIntake().intakeSlaveSpark.disable();
		HardwareAdapter.getInstance().getIntake().intakeVictor.set(ControlMode.Disabled, 0);

		//Disable pusher sparks
		HardwareAdapter.getInstance().getPusher().pusherSpark.disable();

		Logger.getInstance().logRobotThread(Level.INFO, "Disabling victors");

		// Disable shooter victors
		HardwareAdapter.getInstance().getShooter().shooterMasterVictor.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getShooter().shooterSlaveVictor.set(ControlMode.Disabled, 0);
	}

	void configureHardware() {
		configureShovelHardware();
		configureDriveHardware();
		configureElevatorHardware();
		configureIntakeHardware();
		configureShooterHardware();
		configurePusherHardware();
		startIntakeArm();
	}

	void configureDriveHardware() {

		HardwareAdapter.getInstance().getDrivetrain().resetSensors();

		CANSparkMax leftMasterSpark = HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark;
		CANSparkMax leftSlave1Spark = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Spark;
		CANSparkMax leftSlave2Spark = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Spark;

		CANSparkMax rightMasterSpark = HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark;
		CANSparkMax rightSlave1Spark = HardwareAdapter.getInstance().getDrivetrain().rightSlave1Spark;
		CANSparkMax rightSlave2Spark = HardwareAdapter.getInstance().getDrivetrain().rightSlave2Spark;

		leftMasterSpark.enableVoltageCompensation(12);
		leftSlave1Spark.enableVoltageCompensation(12);
		leftSlave2Spark.enableVoltageCompensation(12);
		rightMasterSpark.enableVoltageCompensation(12);
		rightSlave1Spark.enableVoltageCompensation(12);
		rightSlave2Spark.enableVoltageCompensation(12);

		leftMasterSpark.getEncoder().setPositionConversionFactor(DrivetrainConstants.kDriveInchesPerRotation);
		leftSlave1Spark.getEncoder().setPositionConversionFactor(DrivetrainConstants.kDriveInchesPerRotation);
		leftSlave2Spark.getEncoder().setPositionConversionFactor(DrivetrainConstants.kDriveInchesPerRotation);
		rightMasterSpark.getEncoder().setPositionConversionFactor(DrivetrainConstants.kDriveInchesPerRotation);
		rightSlave1Spark.getEncoder().setPositionConversionFactor(DrivetrainConstants.kDriveInchesPerRotation);
		rightSlave2Spark.getEncoder().setPositionConversionFactor(DrivetrainConstants.kDriveInchesPerRotation);

        leftMasterSpark.getEncoder().setVelocityConversionFactor(DrivetrainConstants.kDriveSpeedUnitConversion);
        leftSlave1Spark.getEncoder().setVelocityConversionFactor(DrivetrainConstants.kDriveSpeedUnitConversion);
        leftSlave2Spark.getEncoder().setVelocityConversionFactor(DrivetrainConstants.kDriveSpeedUnitConversion);
        rightMasterSpark.getEncoder().setVelocityConversionFactor(DrivetrainConstants.kDriveSpeedUnitConversion);
        rightSlave1Spark.getEncoder().setVelocityConversionFactor(DrivetrainConstants.kDriveSpeedUnitConversion);
        rightSlave2Spark.getEncoder().setVelocityConversionFactor(DrivetrainConstants.kDriveSpeedUnitConversion);

		leftMasterSpark.getPIDController().setOutputRange(-DrivetrainConstants.kDriveMaxClosedLoopOutput, DrivetrainConstants.kDriveMaxClosedLoopOutput);
		leftSlave1Spark.getPIDController().setOutputRange(-DrivetrainConstants.kDriveMaxClosedLoopOutput, DrivetrainConstants.kDriveMaxClosedLoopOutput);
		leftSlave2Spark.getPIDController().setOutputRange(-DrivetrainConstants.kDriveMaxClosedLoopOutput, DrivetrainConstants.kDriveMaxClosedLoopOutput);

		rightMasterSpark.getPIDController().setOutputRange(-DrivetrainConstants.kDriveMaxClosedLoopOutput, DrivetrainConstants.kDriveMaxClosedLoopOutput);
		rightSlave1Spark.getPIDController().setOutputRange(-DrivetrainConstants.kDriveMaxClosedLoopOutput, DrivetrainConstants.kDriveMaxClosedLoopOutput);
        rightSlave2Spark.getPIDController().setOutputRange(-DrivetrainConstants.kDriveMaxClosedLoopOutput, DrivetrainConstants.kDriveMaxClosedLoopOutput);

        // leftMasterSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 3);
        // rightMasterSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 3);

		//Reverse right side
		leftMasterSpark.setInverted(false);
		leftSlave1Spark.setInverted(false);
		leftSlave2Spark.setInverted(false);

		rightMasterSpark.setInverted(true);
		rightSlave1Spark.setInverted(true);
		rightSlave2Spark.setInverted(true);

		leftMasterSpark.setSmartCurrentLimit(DrivetrainConstants.kCurrentLimit);
		leftSlave1Spark.setSmartCurrentLimit(DrivetrainConstants.kCurrentLimit);
		leftSlave2Spark.setSmartCurrentLimit(DrivetrainConstants.kCurrentLimit);

		rightMasterSpark.setSmartCurrentLimit(DrivetrainConstants.kCurrentLimit);
		rightSlave1Spark.setSmartCurrentLimit(DrivetrainConstants.kCurrentLimit);
		rightSlave2Spark.setSmartCurrentLimit(DrivetrainConstants.kCurrentLimit);

		// Set slave sparks to follower mode
		leftSlave1Spark.follow(leftMasterSpark);
       	leftSlave2Spark.follow(leftMasterSpark);
       	rightSlave1Spark.follow(rightMasterSpark);
		rightSlave2Spark.follow(rightMasterSpark);
		
		updateSparkGains(leftMasterSpark, Gains.vidarVelocity);
		updateSparkGains(rightMasterSpark, Gains.vidarVelocity);

    }

    void configureElevatorHardware() {

	    HardwareAdapter.getInstance().getElevator().resetSensors();

	    CANSparkMax masterSpark = HardwareAdapter.getInstance().getElevator().elevatorMasterSpark;
	    CANSparkMax slaveSpark = HardwareAdapter.getInstance().getElevator().elevatorSlaveSpark;

	    masterSpark.enableVoltageCompensation(12);
	    slaveSpark.enableVoltageCompensation(12);

	    masterSpark.setInverted(false);
	    slaveSpark.setInverted(false);

	    slaveSpark.follow(masterSpark);

	    masterSpark.getPIDController().setOutputRange(-0.65,0.65);
		slaveSpark.getPIDController().setOutputRange(-0.65,0.65);
		
		updateSparkGains(masterSpark, Gains.elevatorPosition);

//        masterSpark.setOpenLoopRampRate(0.4);
//        slaveSpark.setOpenLoopRampRate(0.4);
	}

	void configureIntakeHardware() {

	    HardwareAdapter.getInstance().getIntake().resetSensors();

		CANSparkMax intakeMasterSpark = HardwareAdapter.getInstance().getIntake().intakeMasterSpark;
		CANSparkMax intakeSlaveSpark = HardwareAdapter.getInstance().getIntake().intakeSlaveSpark;
		WPI_VictorSPX intakeVictor = HardwareAdapter.getInstance().getIntake().intakeVictor;

		intakeMasterSpark.restoreFactoryDefaults();
		intakeSlaveSpark.restoreFactoryDefaults();

		intakeMasterSpark.enableVoltageCompensation(12);
		intakeSlaveSpark.enableVoltageCompensation(12);

		intakeMasterSpark.getEncoder().setPositionConversionFactor(IntakeConstants.kArmDegreesPerRevolution);
		intakeSlaveSpark.getEncoder().setPositionConversionFactor(IntakeConstants.kArmDegreesPerRevolution);
		intakeMasterSpark.getEncoder().setVelocityConversionFactor(IntakeConstants.kArmEncoderSpeedUnitConversion);
		intakeSlaveSpark.getEncoder().setVelocityConversionFactor(IntakeConstants.kArmEncoderSpeedUnitConversion);

		intakeMasterSpark.setInverted(false);
		intakeSlaveSpark.setInverted(false);

		intakeVictor.setInverted(true);

		intakeMasterSpark.getPIDController().setOutputRange(-0.75,0.75);
		intakeSlaveSpark.getPIDController().setOutputRange(-0.75,0.75);

		intakeVictor.setNeutralMode(NeutralMode.Brake);

		intakeMasterSpark.setOpenLoopRampRate(1.0);
		intakeSlaveSpark.setOpenLoopRampRate(1.0);

		intakeVictor.enableVoltageCompensation(true);
		intakeVictor.configVoltageCompSaturation(14, 0);
		intakeVictor.configForwardSoftLimitEnable(false, 0);
		intakeVictor.configReverseSoftLimitEnable(false, 0);

		intakeVictor.configPeakOutputForward(1, 0);
		intakeVictor.configPeakOutputReverse(-1, 0);

//		Set slave sparks to follower mode
		intakeSlaveSpark.follow(intakeMasterSpark);

		updateSparkGains(intakeMasterSpark, Gains.intakeSmartMotion, 1);
		updateSparkGains(intakeMasterSpark, Gains.intakePosition, 0);

	}

	void configureShooterHardware() {
		WPI_VictorSPX masterVictor = HardwareAdapter.getInstance().getShooter().shooterMasterVictor;
		WPI_VictorSPX slaveVictor = HardwareAdapter.getInstance().getShooter().shooterSlaveVictor;

		masterVictor.setInverted(false);
		slaveVictor.setInverted(false);

		slaveVictor.follow(masterVictor);

		masterVictor.setNeutralMode(NeutralMode.Brake);
		slaveVictor.setNeutralMode(NeutralMode.Brake);

		masterVictor.configOpenloopRamp(0.09, 0);
		slaveVictor.configOpenloopRamp(0.09, 0);

		masterVictor.enableVoltageCompensation(true);
		slaveVictor.enableVoltageCompensation(true);

		masterVictor.configVoltageCompSaturation(14, 0);
		slaveVictor.configVoltageCompSaturation(14, 0);

		masterVictor.configForwardSoftLimitEnable(false, 0);
		masterVictor.configReverseSoftLimitEnable(false, 0);
		slaveVictor.configForwardSoftLimitEnable(false, 0);
		slaveVictor.configReverseSoftLimitEnable(false, 0);
	}

	void configurePusherHardware() {

	    HardwareAdapter.getInstance().getPusher().resetSensors();

		CANSparkMax pusherSpark = HardwareAdapter.getInstance().getPusher().pusherSpark;

		pusherSpark.enableVoltageCompensation(12);

		pusherSpark.getEncoder().setPositionConversionFactor(PusherConstants.kPusherInchesPerRotation);
		pusherSpark.getEncoder().setVelocityConversionFactor(PusherConstants.kPusherEncSpeedUnitConversion);
		pusherSpark.getPIDController().setOutputRange(-0.6,0.6);

		pusherSpark.setInverted(true);
		pusherSpark.setIdleMode(CANSparkMax.IdleMode.kBrake);

		updateSparkGains(pusherSpark, Gains.pusherPosition);
	}

	void startUltrasonics() {
        Ultrasonic intakeUltrasonicLeft = HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft;
        Ultrasonic intakeUltrasonicRight = HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight;
        Ultrasonic pusherUltrasonic = HardwareAdapter.getInstance().getPusher().pusherUltrasonic;

        intakeUltrasonicLeft.setAutomaticMode(true);
        intakeUltrasonicRight.setAutomaticMode(true);
        pusherUltrasonic.setAutomaticMode(true);

		intakeUltrasonicLeft.setEnabled(true);
        intakeUltrasonicRight.setEnabled(true);
        pusherUltrasonic.setEnabled(true);
    }

	void configureShovelHardware() {
		WPI_TalonSRX shovelTalon = HardwareAdapter.getInstance().getShovel().shovelTalon;

		shovelTalon.setNeutralMode(NeutralMode.Brake);
		shovelTalon.configOpenloopRamp(0.09, 0);
		shovelTalon.enableVoltageCompensation(true);
		shovelTalon.configVoltageCompSaturation(14, 0);
		shovelTalon.configForwardSoftLimitEnable(false, 0);
		shovelTalon.configReverseSoftLimitEnable(false, 0);
	}

	/**
	 * Updates all the sensor data taken from the hardware
	 */
	void updateState(RobotState robotState) {
		CANSparkMax leftMasterSpark = HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark;
		CANSparkMax rightMasterSpark = HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark;

		robotState.leftStickInput.update(HardwareAdapter.getInstance().getJoysticks().driveStick);
		robotState.rightStickInput.update(HardwareAdapter.getInstance().getJoysticks().turnStick);

		robotState.operatorXboxControllerInput.update(HardwareAdapter.getInstance().getJoysticks().operatorXboxController);
//		robotState.backupStickInput.update(HardwareAdapter.getInstance().getJoysticks().backupStick);

		robotState.hatchIntakeUp = HardwareAdapter.getInstance().getShovel().upDownHFX.get();
		robotState.shovelCurrentDraw = HardwareAdapter.getInstance().getMiscellaneousHardware().pdp.getCurrent(PortConstants.kVidarShovelPDPPort);
		robotState.hasHatch = (robotState.shovelCurrentDraw > ShovelConstants.kMaxShovelCurrentDraw);

		robotState.elevatorPosition = HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.getEncoder().getPosition();
		robotState.elevatorVelocity = HardwareAdapter.getInstance().getElevator().elevatorSlaveSpark.getEncoder().getVelocity();

		PigeonIMU gyro = HardwareAdapter.getInstance().getDrivetrain().gyro;
		if(gyro != null) {
			robotState.drivePose.lastHeading = robotState.drivePose.heading;
			robotState.drivePose.heading = gyro.getFusedHeading();
			robotState.drivePose.headingVelocity = (robotState.drivePose.heading - robotState.drivePose.lastHeading) /
                    DrivetrainConstants.kNormalLoopsDt;
		} else {
			robotState.drivePose.heading = -0;
			robotState.drivePose.headingVelocity = -0;
		}
		
		robotState.drivePose.lastLeftEnc = robotState.drivePose.leftEnc;
		robotState.drivePose.leftEnc = leftMasterSpark.getEncoder().getPosition();
		robotState.drivePose.leftEncVelocity = leftMasterSpark.getEncoder().getVelocity();
		robotState.drivePose.lastRightEnc = robotState.drivePose.rightEnc;
		robotState.drivePose.rightEnc = rightMasterSpark.getEncoder().getPosition();
		robotState.drivePose.rightEncVelocity = rightMasterSpark.getEncoder().getVelocity();
		
		double robotVelocity = (robotState.drivePose.leftEncVelocity + robotState.drivePose.rightEncVelocity) / 2;

		double[] accelerometer_angle = new double[3];
		HardwareAdapter.getInstance().getDrivetrain().gyro.getAccelerometerAngles(accelerometer_angle);
		robotState.robotAccel = accelerometer_angle[0];
		robotState.robotVelocity = robotVelocity;

		robotState.intakeVelocity = HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getVelocity();

		double time = Timer.getFPGATimestamp();

		//Rotation2d gyro_angle = Rotation2d.fromRadians((right_distance - left_distance) * Constants.kTrackScrubFactor
		///Constants.kTrackEffectiveDiameter);
		Rotation2d gyro_angle = Rotation2d.fromDegrees(robotState.drivePose.heading);
		Rotation2d gyro_velocity = Rotation2d.fromDegrees(robotState.drivePose.headingVelocity);

		RigidTransform2d odometry = robotState.generateOdometryFromSensors(robotState.drivePose.leftEnc -
                        robotState.drivePose.lastLeftEnc,robotState.drivePose.rightEnc - robotState.drivePose.lastRightEnc, gyro_angle);

		RigidTransform2d.Delta velocity = Kinematics.forwardKinematics(
		        robotState.drivePose.leftEncVelocity, robotState.drivePose.rightEncVelocity, gyro_velocity.getRadians());

		robotState.addObservations(time, odometry, velocity);

		//Update pusher sensors
		robotState.pusherPosition = HardwareAdapter.getInstance().getPusher().pusherSpark.getEncoder().getPosition();
		robotState.pusherVelocity = HardwareAdapter.getInstance().getPusher().pusherSpark.getEncoder().getVelocity();
		
		updateIntakeSensors();
		updateUltrasonicSensors(robotState);


	}

	void startIntakeArm() {
		Robot.getRobotState().intakeStartAngle = IntakeConstants.kMaxAngle -
				1/IntakeConstants.kArmPotentiometerTicksPerDegree * Math.abs(HardwareAdapter.getInstance().getIntake().potentiometer.get() -
						IntakeConstants.kMaxAngleTicks);
		HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().setPosition(Robot.getRobotState().intakeStartAngle);

	}

	void updateIntakeSensors() {
		Robot.getRobotState().intakeAngle = HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getPosition();
		DataLogger.getInstance().logData(Level.FINE, "intake_pot",  HardwareAdapter.getInstance().getIntake().potentiometer.get());
		DataLogger.getInstance().logData(Level.FINE, "intake_enc", Robot.getRobotState().intakeAngle);
		DataLogger.getInstance().logData(Level.FINE, "intake_master_output", HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getAppliedOutput());
		DataLogger.getInstance().logData(Level.FINE, "intake_slave_output", HardwareAdapter.getInstance().getIntake().intakeSlaveSpark.getAppliedOutput());
		// DataLogger.getInstance().logData(Level.FINE, "intake_faults", HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getFaults() + HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getStickyFaults() + HardwareAdapter.getInstance().getIntake().intakeSlaveSpark.getFaults() + HardwareAdapter.getInstance().getIntake().intakeSlaveSpark.getStickyFaults());
	}

	void updateUltrasonicSensors(RobotState robotState) {
		// HAS CARGO IN INTAKE

		// left side
		Ultrasonic mUltrasonicLeft = HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft;
		robotState.mLeftReadings.add(mUltrasonicLeft.getRangeInches());
		if(robotState.mLeftReadings.size() > 10) {
			robotState.mLeftReadings.remove(0);
		}
		// right side
		Ultrasonic mUltrasonicRight = HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight;
		robotState.mRightReadings.add(mUltrasonicRight.getRangeInches());
		if(robotState.mRightReadings.size() > 10) {
			robotState.mRightReadings.remove(0);
		}

		int leftTotal = (int) robotState.mLeftReadings.stream().filter(i -> (i < IntakeConstants.kCargoInchTolerance)).count();
		int rightTotal = (int) robotState.mRightReadings.stream().filter(i -> (i < IntakeConstants.kCargoInchTolerance)).count();
		robotState.hasCargo = (leftTotal >= OtherConstants.kRequiredUltrasonicCount || rightTotal >= OtherConstants.kRequiredUltrasonicCount);
		robotState.cargoDistance = Math.min(mUltrasonicLeft.getRangeInches(), mUltrasonicRight.getRangeInches());


		// HAS CARGO IN CARRIAGE

		//Cargo Distance from Pusher
		Ultrasonic mPusherUltrasonic = HardwareAdapter.getInstance().getPusher().pusherUltrasonic;
		robotState.mPusherReadings.add(mPusherUltrasonic.getRangeInches());
		if(robotState.mPusherReadings.size() > 10) {
			robotState.mPusherReadings.remove(0);
		}


		int pusherTotal = (int) robotState.mPusherReadings.stream().filter(i -> i < PusherConstants.kVidarCargoTolerance).count();
		robotState.hasPusherCargo = (pusherTotal > OtherConstants.kRequiredUltrasonicCount);
		robotState.cargoPusherDistance = (mPusherUltrasonic.getRangeInches());

	}

	/**
	 * Updates the hardware to run with output values of subsystems
	 */
	void updateHardware() {
		updateDrivetrain();
		updateElevator();
		updateShooter();
		updatePusher();
		updateShovel();
		updateFingers();
//		updateIntake();
		updateMiscellaneousHardware();
	}

	/**
	 * Updates the drivetrain Uses SparkOutput and can run off-board control loops through SRX
	 */
	private void updateDrivetrain() {
		updateSparkMax(HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark, mDrive.getDriveSignal().leftMotor);
		updateSparkMax(HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark, mDrive.getDriveSignal().rightMotor);
	}

    /**
     * Checks if the compressor should compress and updates it accordingly
     */
	private void updateMiscellaneousHardware() {
	    if(shouldCompress()) {
	        HardwareAdapter.getInstance().getMiscellaneousHardware().compressor.start();
        } else {
            HardwareAdapter.getInstance().getMiscellaneousHardware().compressor.stop();
        }

	    HardwareAdapter.getInstance().getJoysticks().operatorXboxController.setRumble(shouldRumble());
    }

    /**
     * Runs the compressor only when the pressure too low or the current draw is
     * low enough
     */
    private boolean shouldCompress() {
    	return !(RobotState.getInstance().gamePeriod == RobotState.GamePeriod.AUTO || RobotState.getInstance().isQuickturning);
    }

    /**
     * Determines when the rumble for the xbox controller should be on
     */
    private boolean shouldRumble() {
        boolean rumble;
        double intakeRumbleLength = mIntake.getRumbleLength();
        double shovelRumbleLength = mShovel.getRumbleLength();
        double shooterRumbleLength = mShooter.getRumbleLength();

        if(intakeRumbleLength > 0) {
            rumble = true;
			mIntake.decreaseRumbleLength();
		} else if(shovelRumbleLength > 0) {
            rumble = true;
			mShovel.decreaseRumbleLength();
		} else if(shooterRumbleLength > 0) {
            rumble = true;
			mShooter.decreaseRumbleLength();
		} else {
            rumble = false;
        }

        return rumble;
    }

    /**
     * Updates the shooter
     */
    private void updateShooter() {
        HardwareAdapter.getInstance().getShooter().shooterMasterVictor.set(mShooter.getOutput());
    }

    /*
     * Updates the elevator
     */
    private void updateElevator() {
        if(mElevator.getmGearboxState() == Elevator.GearboxState.ELEVATOR) {
            if (mElevator.getIsAtTop() && mElevator.movingUpwards()) {
                SparkMaxOutput elevatorHoldOutput = new SparkMaxOutput();
                elevatorHoldOutput.setPercentOutput(ElevatorConstants.kHoldVoltage);
                updateSparkMax(HardwareAdapter.getInstance().getElevator().elevatorMasterSpark, elevatorHoldOutput);
            } else {
                updateSparkMax(HardwareAdapter.getInstance().getElevator().elevatorMasterSpark, mElevator.getOutput());
            }
        } else {
            updateSparkMax(HardwareAdapter.getInstance().getElevator().elevatorMasterSpark, mElevator.getOutput());
        }
        HardwareAdapter.getInstance().getElevator().elevatorShifter.set(mElevator.getSolenoidOutput());
    }

	/**
	 * Updates the pusher
	 */
	private void updatePusher() {
	    updateSparkMax(HardwareAdapter.getInstance().getPusher().pusherSpark, mPusher.getPusherOutput());
	}

    /**
     * Updates the shovel
     */
	private void updateShovel() {
		HardwareAdapter.getInstance().getShovel().shovelTalon.set(mShovel.getPercentOutput());
		HardwareAdapter.getInstance().getShovel().upDownSolenoid.set(mShovel.getUpDownOutput() ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}

	/**
	 * Updates fingers
	 */
	private void updateFingers() {
        HardwareAdapter.getInstance().getFingers().openCloseSolenoid.set(mFingers.getOpenCloseOutput());
        HardwareAdapter.getInstance().getFingers().pusherSolenoid.set(mFingers.getExpelOutput());
    }

    /**
     * Updates intake
     */
    private void updateIntake() {
		updateSparkMax(HardwareAdapter.getInstance().getIntake().intakeMasterSpark, mIntake.getSparkOutput());
		DataLogger.getInstance().logData(Level.FINE, "intake_target", mIntake.getSparkOutput().getSetpoint());
		DataLogger.getInstance().logData(Level.FINE, "intake_arbff", mIntake.getSparkOutput().getArbitraryFF());
       	HardwareAdapter.getInstance().getIntake().intakeVictor.set(mIntake.getVictorOutput());

    }

	void enableBrakeMode() {
		CANSparkMax leftMasterSpark = HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark;
		CANSparkMax leftSlave1Spark = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Spark;
		CANSparkMax leftSlave2Spark = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Spark;

		CANSparkMax rightMasterSpark = HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark;
		CANSparkMax rightSlave1Spark = HardwareAdapter.getInstance().getDrivetrain().rightSlave1Spark;
		CANSparkMax rightSlave2Spark = HardwareAdapter.getInstance().getDrivetrain().rightSlave2Spark;

		leftMasterSpark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		leftSlave1Spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		leftSlave2Spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		rightMasterSpark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		rightSlave1Spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		rightSlave2Spark.setIdleMode(CANSparkMax.IdleMode.kBrake);
	}

	void disableBrakeMode() {
		CANSparkMax leftMasterSpark = HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark;
		CANSparkMax leftSlave1Spark = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Spark;
		CANSparkMax leftSlave2Spark = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Spark;

		CANSparkMax rightMasterSpark = HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark;
		CANSparkMax rightSlave1Spark = HardwareAdapter.getInstance().getDrivetrain().rightSlave1Spark;
		CANSparkMax rightSlave2Spark = HardwareAdapter.getInstance().getDrivetrain().rightSlave2Spark;

		leftMasterSpark.setIdleMode(CANSparkMax.IdleMode.kCoast);
		leftSlave1Spark.setIdleMode(CANSparkMax.IdleMode.kCoast);
		leftSlave2Spark.setIdleMode(CANSparkMax.IdleMode.kCoast);
		rightMasterSpark.setIdleMode(CANSparkMax.IdleMode.kCoast);
		rightSlave1Spark.setIdleMode(CANSparkMax.IdleMode.kCoast);
		rightSlave2Spark.setIdleMode(CANSparkMax.IdleMode.kCoast);
	}

	/**
	 * Helper method for processing a TalonSRXOutput for an SRX
	 */
	private void updateTalonSRX(WPI_TalonSRX talon, TalonSRXOutput output) {
		if(output.getControlMode().equals(ControlMode.Position) || output.getControlMode().equals(ControlMode.Velocity)
				|| output.getControlMode().equals(ControlMode.MotionMagic)) {
			talon.config_kP(output.profile, output.gains.P, 0);
			talon.config_kI(output.profile, output.gains.I, 0);
			talon.config_kD(output.profile, output.gains.D, 0);
			talon.config_kF(output.profile, output.gains.F, 0);
			talon.config_IntegralZone(output.profile, output.gains.izone, 0);
			talon.configClosedloopRamp(output.gains.rampRate, 0);
		}
		if(output.getControlMode().equals(ControlMode.MotionMagic)) {
			talon.configMotionAcceleration(output.accel, 0);
			talon.configMotionCruiseVelocity(output.cruiseVel, 0);
		}
		if(output.getControlMode().equals(ControlMode.Velocity)) {
			talon.configAllowableClosedloopError(output.profile, 0, 0);
		}
		if (output.getArbitraryFF() != 0.0 && output.getControlMode().equals(ControlMode.Position)) {
			talon.set(output.getControlMode(), output.getSetpoint(), DemandType.ArbitraryFeedForward, output.getArbitraryFF());
		} else {
			talon.set(output.getControlMode(), output.getSetpoint(), DemandType.Neutral, 0.0);
		}
	}

    /**
     * Helper method for processing a SparkMaxOutput
     * @param spark
     * @param output
     */
    private void updateSparkMax(CANSparkMax spark, SparkMaxOutput output) {
        if (output.getControlType().equals(ControlType.kSmartMotion)){
			spark.getPIDController().setReference(output.getSetpoint(), output.getControlType(), 1, output.getArbitraryFF());
		} else {
            spark.getPIDController().setReference(output.getSetpoint(), output.getControlType(), 0, output.getArbitraryFF());
        }
    }

	private void updateSparkGains(CANSparkMax spark, Gains gains) {
		updateSparkGains(spark, gains, 0);
	}
	
	private void updateSparkGains(CANSparkMax spark, Gains gains, int slotID) {
		spark.getPIDController().setP(gains.P, slotID);
		spark.getPIDController().setD(gains.D, slotID);
		spark.getPIDController().setI(gains.I, slotID);
		spark.getPIDController().setFF(gains.F, slotID);
		spark.getPIDController().setIZone(gains.izone, slotID);
		// spark.setClosedLoopRampRate(gains.rampRate);
	}
}