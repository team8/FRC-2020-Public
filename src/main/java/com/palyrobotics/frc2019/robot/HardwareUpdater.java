package com.palyrobotics.frc2019.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.*;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.logger.Logger;
import com.palyrobotics.frc2019.util.trajectory.Kinematics;
import com.palyrobotics.frc2019.util.trajectory.RigidTransform2d;
import com.palyrobotics.frc2019.util.trajectory.Rotation2d;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;

import java.util.Optional;
import java.util.logging.Level;

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
	private AutoPlacer mAutoPlacer;

	/**
	 * Hardware Updater for Vidar
	 */
	protected HardwareUpdater(Drive drive, Elevator elevator, Shooter shooter, Pusher pusher, Shovel shovel, Fingers fingers, AutoPlacer autoplacer, Intake intake) {
		this.mDrive = drive;
		this.mElevator = elevator;
		this.mShooter = shooter;
		this.mPusher = pusher;
		this.mShovel = shovel;
		this.mFingers = fingers;
		this.mAutoPlacer = autoplacer;
		this.mIntake = intake;
	}

	/**
	 * Initialize all hardware
	 */
	void initHardware() {
		Logger.getInstance().logRobotThread(Level.INFO, "Init hardware");
		configureHardware();
	}

	void disableTalons() {
		Logger.getInstance().logRobotThread(Level.INFO, "Disabling talons");

		//Disable drivetrain talons
		HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().leftSlave1Victor.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().leftSlave2Victor.set(ControlMode.Disabled, 0);

		HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().rightSlave1Victor.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getDrivetrain().rightSlave2Victor.set(ControlMode.Disabled, 0);

		//Disable elevator talons
        HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.disable();
        HardwareAdapter.getInstance().getElevator().elevatorSlaveSpark.disable();

		//Disable intake talons
		HardwareAdapter.getInstance().getIntake().intakeMasterSpark.disable();
		HardwareAdapter.getInstance().getIntake().intakeSlaveSpark.disable();
		HardwareAdapter.getInstance().getIntake().intakeVictor.set(ControlMode.Disabled, 0);

		//Disable pusher talons
		HardwareAdapter.getInstance().getPusher().pusherVictor.set(ControlMode.Disabled, 0);

		// Disable shooter talons
		HardwareAdapter.getInstance().getShooter().shooterMasterVictor.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getShooter().shooterSlaveVictor.set(ControlMode.Disabled, 0);
	}

	void configureHardware() {
		configureDriveHardware();
		configureElevatorHardware();
		configureIntakeHardware();
		configureShooterHardware();
		configurePusherHardware();
		configureShovelHardware();
	}

	void configureDriveHardware() {
		PigeonIMU gyro = HardwareAdapter.getInstance().getDrivetrain().gyro;
		gyro.setYaw(0, 0);
		gyro.setFusedHeading(0, 0);

		WPI_TalonSRX leftMasterTalon = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		WPI_VictorSPX leftSlave1Victor = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Victor;
        WPI_VictorSPX leftSlave2Victor = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Victor;

        WPI_TalonSRX rightMasterTalon = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		WPI_VictorSPX rightSlave1Victor = HardwareAdapter.getInstance().getDrivetrain().rightSlave1Victor;
        WPI_VictorSPX rightSlave2Victor = HardwareAdapter.getInstance().getDrivetrain().rightSlave2Victor;

        leftMasterTalon.enableVoltageCompensation(true);
        leftSlave1Victor.enableVoltageCompensation(true);
        leftSlave2Victor.enableVoltageCompensation(true);
        rightMasterTalon.enableVoltageCompensation(true);
        rightSlave1Victor.enableVoltageCompensation(true);
        rightSlave2Victor.enableVoltageCompensation(true);

		leftMasterTalon.configVoltageCompSaturation(14, 0);
		leftSlave1Victor.configVoltageCompSaturation(14, 0);
        leftSlave2Victor.configVoltageCompSaturation(14, 0);
        rightMasterTalon.configVoltageCompSaturation(14, 0);
		rightSlave1Victor.configVoltageCompSaturation(14, 0);
        rightSlave2Victor.configVoltageCompSaturation(14, 0);

        leftMasterTalon.configForwardSoftLimitEnable(false, 0);
		leftMasterTalon.configReverseSoftLimitEnable(false, 0);
		leftSlave1Victor.configForwardSoftLimitEnable(false, 0);
		leftSlave1Victor.configReverseSoftLimitEnable(false, 0);
        leftSlave2Victor.configForwardSoftLimitEnable(false, 0);
        leftSlave2Victor.configReverseSoftLimitEnable(false, 0);

		rightMasterTalon.configForwardSoftLimitEnable(false, 0);
		rightMasterTalon.configReverseSoftLimitEnable(false, 0);
		rightSlave1Victor.configForwardSoftLimitEnable(false, 0);
		rightSlave1Victor.configReverseSoftLimitEnable(false, 0);
        rightSlave2Victor.configForwardSoftLimitEnable(false, 0);
        rightSlave2Victor.configReverseSoftLimitEnable(false, 0);

		leftMasterTalon.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
		leftMasterTalon.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);
		leftSlave1Victor.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
		leftSlave1Victor.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);
        leftSlave2Victor.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
        leftSlave2Victor.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);

		rightMasterTalon.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
		rightMasterTalon.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);
        rightSlave1Victor.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
        rightSlave1Victor.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);
		rightSlave2Victor.configPeakOutputForward(Constants.kDriveMaxClosedLoopOutput, 0);
		rightSlave2Victor.configPeakOutputReverse(-Constants.kDriveMaxClosedLoopOutput, 0);

		//Configure master talon feedback devices
		leftMasterTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		rightMasterTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);

		leftMasterTalon.setSensorPhase(false);
		rightMasterTalon.setSensorPhase(false);

		leftMasterTalon.overrideLimitSwitchesEnable(false);
		rightMasterTalon.overrideLimitSwitchesEnable(false);

		leftMasterTalon.setStatusFramePeriod(0, 5, 0);
		rightMasterTalon.setStatusFramePeriod(0, 5, 0);

		leftMasterTalon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_50Ms, 0);
		rightMasterTalon.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_50Ms, 0);

		leftMasterTalon.configVelocityMeasurementWindow(16, 0);
		rightMasterTalon.configVelocityMeasurementWindow(16, 0);

		//Zero encoders
		leftMasterTalon.setSelectedSensorPosition(0, 0, 0);
		rightMasterTalon.setSelectedSensorPosition(0, 0, 0);

		leftMasterTalon.configClosedloopRamp(0.2, 0);
		rightMasterTalon.configClosedloopRamp(0.2, 0);

		leftMasterTalon.configOpenloopRamp(0.0, 0);
		rightMasterTalon.configOpenloopRamp(0.0, 0);

		//Reverse right side
		leftMasterTalon.setInverted(true);
		leftSlave1Victor.setInverted(true);
		leftSlave2Victor.setInverted(true);

		rightMasterTalon.setInverted(false);
		rightSlave1Victor.setInverted(false);
		rightSlave2Victor.setInverted(false);

		//Set slave victors to follower mode
		leftSlave1Victor.follow(leftMasterTalon);
        leftSlave2Victor.follow(leftMasterTalon);
        rightSlave1Victor.follow(rightMasterTalon);
        rightSlave2Victor.follow(rightMasterTalon);
    }

    void configureElevatorHardware() {
	    CANSparkMax masterSpark = HardwareAdapter.getInstance().getElevator().elevatorMasterSpark;
	    CANSparkMax slaveSpark = HardwareAdapter.getInstance().getElevator().elevatorSlaveSpark;

	    masterSpark.setInverted(true);
	    slaveSpark.setInverted(false);

	    slaveSpark.follow(masterSpark);

        masterSpark.setRampRate(0.4);
        slaveSpark.setRampRate(0.4);
	}

	void configureIntakeHardware() {
		CANSparkMax intakeMasterSpark = HardwareAdapter.getInstance().getIntake().intakeMasterSpark;
		CANSparkMax intakeSlaveSpark = HardwareAdapter.getInstance().getIntake().intakeSlaveSpark;
		WPI_VictorSPX intakeVictor = HardwareAdapter.getInstance().getIntake().intakeVictor;

		Ultrasonic ultrasonic1 = HardwareAdapter.getInstance().getIntake().ultrasonic1;
		Ultrasonic ultrasonic2 = HardwareAdapter.getInstance().getIntake().ultrasonic2;

		intakeMasterSpark.setInverted(true);
		intakeSlaveSpark.setInverted(true);
		intakeVictor.setInverted(true);

		intakeVictor.setNeutralMode(NeutralMode.Brake);

		intakeMasterSpark.setRampRate(0.4);
		intakeSlaveSpark.setRampRate(0.4);

		intakeVictor.enableVoltageCompensation(true);
		intakeVictor.configVoltageCompSaturation(14, 0);
		intakeVictor.configForwardSoftLimitEnable(false, 0);
		intakeVictor.configReverseSoftLimitEnable(false, 0);

		intakeVictor.configPeakOutputForward(1, 0);
		intakeVictor.configPeakOutputReverse(-1, 0);

		//Set slave talons to follower mode
		intakeSlaveSpark.follow(intakeMasterSpark);

        ultrasonic1.setAutomaticMode(true);
		ultrasonic1.setEnabled(true);
		ultrasonic2.setAutomaticMode(true);
		ultrasonic2.setAutomaticMode(true);

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
		WPI_VictorSPX pusherVictor = HardwareAdapter.getInstance().getPusher().pusherVictor;

		pusherVictor.setInverted(false);
		pusherVictor.setNeutralMode(NeutralMode.Brake);
		pusherVictor.configOpenloopRamp(0.09, 0);
		pusherVictor.enableVoltageCompensation(true);
		pusherVictor.configVoltageCompSaturation(14, 0);
		pusherVictor.configForwardSoftLimitEnable(false, 0);

		Ultrasonic pusherUltrasonicRight = HardwareAdapter.getInstance().getPusher().pusherUltrasonicRight;
		Ultrasonic pusherUltrasonicLeft = HardwareAdapter.getInstance().getPusher().pusherUltrasonicLeft;

		pusherUltrasonicRight.setAutomaticMode(true);
		pusherUltrasonicRight.setEnabled(true);
		pusherUltrasonicLeft.setAutomaticMode(true);
		pusherUltrasonicLeft.setEnabled(true);
	}

	void configureShovelHardware() {
		WPI_VictorSPX shovelVictor = HardwareAdapter.getInstance().getShovel().ShovelVictor;

		shovelVictor.setNeutralMode(NeutralMode.Brake);
		shovelVictor.configOpenloopRamp(0.09, 0);
		shovelVictor.enableVoltageCompensation(true);
		shovelVictor.configVoltageCompSaturation(14, 0);
		shovelVictor.configForwardSoftLimitEnable(false, 0);
		shovelVictor.configReverseSoftLimitEnable(false, 0);
	}

	/**
	 * Updates all the sensor data taken from the hardware
	 */
	void updateState(RobotState robotState) {

		WPI_TalonSRX leftMasterTalon = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		WPI_TalonSRX rightMasterTalon = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;

		robotState.leftControlMode = leftMasterTalon.getControlMode();
		robotState.rightControlMode = rightMasterTalon.getControlMode();

		robotState.leftStickInput.update(HardwareAdapter.getInstance().getJoysticks().driveStick);
		robotState.rightStickInput.update(HardwareAdapter.getInstance().getJoysticks().turnStick);
		if(Constants.operatorXBoxController) {
			robotState.operatorXboxControllerInput.update(HardwareAdapter.getInstance().getJoysticks().operatorXboxController);
		} else {
			robotState.climberStickInput.update(HardwareAdapter.getInstance().getJoysticks().climberStick);
			robotState.operatorJoystickInput.update(HardwareAdapter.getInstance().getJoysticks().operatorJoystick);
		}

		robotState.hatchIntakeUp = HardwareAdapter.getInstance().getShovel().upDownHFX.get();
		robotState.shovelCurrentDraw = HardwareAdapter.getInstance().getMiscellaneousHardware().pdp.getCurrent(Constants.kShovelPDPPort);

		switch(robotState.leftControlMode) {
			//Fall through
			case Position:
			case Velocity:
			case MotionProfileArc:
			case MotionProfile:
			case MotionMagic:
				robotState.leftSetpoint = leftMasterTalon.getClosedLoopTarget(0);
				break;
			case Current:
				robotState.leftSetpoint = leftMasterTalon.getOutputCurrent();
				break;
			//Fall through
			case Follower:
			case PercentOutput:
				robotState.leftSetpoint = leftMasterTalon.getMotorOutputPercent();
				break;
			default:
				break;
		}

		switch(robotState.rightControlMode) {
			//Fall through
			case Position:
			case Velocity:
			case MotionProfileArc:
			case MotionProfile:
			case MotionMagic:
				robotState.rightSetpoint = rightMasterTalon.getClosedLoopTarget(0);
				break;
			case Current:
				robotState.rightSetpoint = rightMasterTalon.getOutputCurrent();
				break;
			//Fall through
			case Follower:
			case PercentOutput:
				robotState.rightSetpoint = rightMasterTalon.getMotorOutputPercent();
				break;
			default:
				break;
		}

		robotState.elevatorPosition = HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.getEncoder().getPosition();
		robotState.elevatorVelocity = HardwareAdapter.getInstance().getElevator().elevatorSlaveSpark.getEncoder().getVelocity();

		// Change HFX Talon location
		robotState.elevatorHFX = HardwareAdapter.getInstance().getElevator().elevatorHFX.get();

		PigeonIMU gyro = HardwareAdapter.getInstance().getDrivetrain().gyro;
		if(gyro != null) {
			robotState.drivePose.heading = gyro.getFusedHeading();
			robotState.drivePose.headingVelocity = (robotState.drivePose.heading - robotState.drivePose.lastHeading) / Constants.kNormalLoopsDt;
			robotState.drivePose.lastHeading = gyro.getFusedHeading();
		} else {
			robotState.drivePose.heading = -0;
			robotState.drivePose.headingVelocity = -0;
		}

		robotState.drivePose.lastLeftEnc = robotState.drivePose.leftEnc;
		robotState.drivePose.leftEnc = leftMasterTalon.getSelectedSensorPosition(0);
		robotState.drivePose.leftEncVelocity = leftMasterTalon.getSelectedSensorVelocity(0);
		robotState.drivePose.lastRightEnc = robotState.drivePose.rightEnc;
		robotState.drivePose.rightEnc = rightMasterTalon.getSelectedSensorPosition(0);
		robotState.drivePose.rightEncVelocity = rightMasterTalon.getSelectedSensorVelocity(0);

		double robotVelocity = (robotState.drivePose.leftEncVelocity + robotState.drivePose.rightEncVelocity) / (2 * Constants.kDriveSpeedUnitConversion);
		robotState.robotAccel = (robotVelocity - robotState.robotVelocity) / Constants.deltaTime;
		robotState.robotVelocity = robotVelocity;

		if(leftMasterTalon.getControlMode().equals(ControlMode.MotionMagic)) {
			robotState.drivePose.leftMotionMagicPos = Optional.of(leftMasterTalon.getActiveTrajectoryPosition());
			robotState.drivePose.leftMotionMagicVel = Optional.of(leftMasterTalon.getActiveTrajectoryVelocity());
		} else {
			robotState.drivePose.leftMotionMagicPos = Optional.empty();
			robotState.drivePose.leftMotionMagicVel = Optional.empty();
		}

		if(rightMasterTalon.getControlMode().equals(ControlMode.MotionMagic)) {
			robotState.drivePose.rightMotionMagicPos = Optional.of(rightMasterTalon.getActiveTrajectoryPosition());
			robotState.drivePose.rightMotionMagicVel = Optional.of(rightMasterTalon.getActiveTrajectoryVelocity());
		} else {
			robotState.drivePose.rightMotionMagicPos = Optional.empty();
			robotState.drivePose.rightMotionMagicVel = Optional.empty();
		}

		robotState.drivePose.leftError = Optional.of(leftMasterTalon.getClosedLoopError(0));
		robotState.drivePose.rightError = Optional.of(rightMasterTalon.getClosedLoopError(0));

		double time = Timer.getFPGATimestamp();

		//Rotation2d gyro_angle = Rotation2d.fromRadians((right_distance - left_distance) * Constants.kTrackScrubFactor
		///Constants.kTrackEffectiveDiameter);
		Rotation2d gyro_angle = Rotation2d.fromDegrees(robotState.drivePose.heading);
		Rotation2d gyro_velocity = Rotation2d.fromDegrees(robotState.drivePose.headingVelocity);

		RigidTransform2d odometry = robotState.generateOdometryFromSensors((robotState.drivePose.leftEnc - robotState.drivePose.lastLeftEnc) / Constants.kDriveTicksPerInch,
				(robotState.drivePose.rightEnc - robotState.drivePose.lastRightEnc) / Constants.kDriveTicksPerInch, gyro_angle);

		RigidTransform2d.Delta velocity = Kinematics.forwardKinematics(robotState.drivePose.leftEncVelocity / Constants.kDriveSpeedUnitConversion,
				robotState.drivePose.rightEncVelocity / Constants.kDriveSpeedUnitConversion, gyro_velocity.getRadians());

		robotState.addObservations(time, odometry, velocity);

		//Update pusher sensors
		robotState.pusherPosition = HardwareAdapter.getInstance().getPusher().pusherPotentiometer.get() /
				Constants.kPusherTicksPerInch;
		robotState.pusherVelocity = (robotState.pusherPosition - robotState.pusherCachePosition) / Constants.kNormalLoopsDt;
		StickyFaults pusherStickyFaults = new StickyFaults();
		HardwareAdapter.getInstance().getPusher().pusherVictor.clearStickyFaults(0);
		HardwareAdapter.getInstance().getPusher().pusherVictor.getStickyFaults(pusherStickyFaults);
		robotState.hasPusherStickyFaults = false;
		robotState.pusherCachePosition = robotState.pusherPosition;


		CANSparkMax.FaultID intakeStickyFaults = CANSparkMax.FaultID.kSensorFault;
		HardwareAdapter.getInstance().getIntake().intakeMasterSpark.clearFaults();
		HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getStickyFault(intakeStickyFaults);


		updateUltrasonicSensors(robotState);
		updateIntakeState(robotState);
	}

	void updateIntakeState(RobotState robotState) {
		//Update intake sensors

		robotState.intakeVelocity = HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getVelocity() / Constants.kArmEncoderTicksPerDegree;
	}

	void startIntakeArm() {
		Robot.getRobotState().intakeAngle = Constants.kIntakeMaxAngle -
				1/Constants.kArmEncoderTicksPerDegree * (Constants.kIntakeMaxAngleTicks - HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getPosition());

	}

	void updateUltrasonicSensors(RobotState robotState) {
		// HAS CARGO IN INTAKE

		// left side
		Ultrasonic mUltrasonicLeft = HardwareAdapter.getInstance().getIntake().ultrasonic1;
		robotState.mLeftReadings.add(mUltrasonicLeft.getRangeInches());
		if(robotState.mLeftReadings.size() > 10) {
			robotState.mLeftReadings.remove(0);
		}
		// right side
		Ultrasonic mUltrasonicRight = HardwareAdapter.getInstance().getIntake().ultrasonic2;
		robotState.mRightReadings.add(mUltrasonicRight.getRangeInches());
		if(robotState.mRightReadings.size() > 10) {
			robotState.mRightReadings.remove(0);
		}

		int leftTotal = (int) robotState.mLeftReadings.stream().filter(i -> (i < Constants.kIntakeCargoInchTolerance)).count();
		int rightTotal = (int) robotState.mRightReadings.stream().filter(i -> (i < Constants.kIntakeCargoInchTolerance)).count();

		robotState.hasCargo = (leftTotal > Constants.kRequiredUltrasonicCount && rightTotal > Constants.kRequiredUltrasonicCount);
		robotState.cargoDistance = (mUltrasonicLeft.getRangeInches() + mUltrasonicRight.getRangeInches()) / 2;

		// HAS CARGO IN CARRIAGE

		//Left Side Cargo Distance from Pusher
		Ultrasonic mPusherUltrasonicLeft = HardwareAdapter.getInstance().getPusher().pusherUltrasonicLeft;
		robotState.mLeftPusherReadings.add(mPusherUltrasonicLeft.getRangeInches());
		if(robotState.mLeftPusherReadings.size() > 10) {
			robotState.mLeftPusherReadings.remove(0);
		}

		//Right Side Cargo Distance from Pusher
		Ultrasonic mPusherUltrasonicRight = HardwareAdapter.getInstance().getPusher().pusherUltrasonicRight;
		robotState.mRightPusherReadings.add(mPusherUltrasonicRight.getRangeInches());
		if(robotState.mRightPusherReadings.size() > 10) {
			robotState.mRightPusherReadings.remove(0);
		}

		int leftPusherTotal = (int) robotState.mLeftPusherReadings.stream().filter(i -> i < Constants.kVidarPusherCargoTolerance).count();
		int rightPusherTotal = (int) robotState.mRightPusherReadings.stream().filter(i -> i < Constants.kVidarPusherCargoTolerance).count();
		robotState.hasPusherCargo = (leftPusherTotal > Constants.kRequiredUltrasonicCount && rightPusherTotal > Constants.kRequiredUltrasonicCount);
		robotState.cargoPusherDistance = (mPusherUltrasonicLeft.getRangeInches() + mPusherUltrasonicRight.getRangeInches())/2;
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
		updateAutoPlacer();
		updateMiscellaneousHardware();
	}

	/**
	 * Updates the drivetrain Uses TalonSRXOutput and can run off-board control loops through SRX
	 */
	private void updateDrivetrain() {
		updateTalonSRX(HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon, mDrive.getDriveSignal().leftMotor);
		updateTalonSRX(HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon, mDrive.getDriveSignal().rightMotor);
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
    }

    /**
     * Runs the compressor only when the pressure too low or the current draw is
     * low enough
     */
    private boolean shouldCompress() {
    	return !(RobotState.getInstance().gamePeriod == RobotState.GamePeriod.AUTO);
    }

    /**
     * Updates the shooter
     */
    private void updateShooter() {
        HardwareAdapter.getInstance().getShooter().shooterMasterVictor.set(mShooter.getOutput());
    }

    /**
     * Updates the auto placer
     */
    private void updateAutoPlacer() {
        HardwareAdapter.getInstance().getAutoPlacer().solenoid.set(mAutoPlacer.getOutput());
    }


    /*
     * Updates the elevator
     */
    private void updateElevator() {
        if(mElevator.getmGearboxState() == Elevator.GearboxState.ELEVATOR) {
            if (mElevator.getIsAtTop() && mElevator.movingUpwards()) {
                SparkMaxOutput elevatorHoldOutput = new SparkMaxOutput();
                elevatorHoldOutput.setPercentOutput(Constants.kElevatorHoldVoltage);
                updateSparkMax(HardwareAdapter.getInstance().getElevator().elevatorMasterSpark, elevatorHoldOutput);
            } else {
                updateSparkMax(HardwareAdapter.getInstance().getElevator().elevatorMasterSpark, mElevator.getOutput());
            }
        } else {
            updateSparkMax(HardwareAdapter.getInstance().getElevator().elevatorMasterSpark, mElevator.getOutput());
        }
        HardwareAdapter.getInstance().getElevator().elevatorDoubleSolenoid.set(mElevator.getSolenoidOutput());
    }

	/**
	 * Updates the pusher
	 */
	private void updatePusher() {
		HardwareAdapter.getInstance().getPusher().pusherVictor.set(mPusher.getPusherOutput());
	}

    /**
     * Updates the shovel
     */
	private void updateShovel() {
		HardwareAdapter.getInstance().getShovel().ShovelVictor.set(mShovel.getVictorOutput());
		HardwareAdapter.getInstance().getShovel().upDownSolenoid.set(mShovel.getUpDownOutput());
	}

	/**
	 * Updates fingers
	 */
	private void updateFingers() {
        HardwareAdapter.getInstance().getFingers().openCloseSolenoid.set(mFingers.getOpenCloseOutput());
        HardwareAdapter.getInstance().getFingers().expelSolenoid.set(mFingers.getExpelOutput());
    }

    /**
     * Updates intake
     */
    private void updateIntake() {
		updateSparkMax(HardwareAdapter.getInstance().getIntake().intakeMasterSpark, mIntake.getSparkOutput());
		HardwareAdapter.getInstance().getIntake().intakeVictor.set(mIntake.getVictorOutput());
	}

	void enableBrakeMode() {
		WPI_TalonSRX leftMasterTalon = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		WPI_VictorSPX leftSlave1Victor = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Victor;
		WPI_VictorSPX leftSlave2Victor = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Victor;

		WPI_TalonSRX rightMasterTalon = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		WPI_VictorSPX rightSlave1Victor = HardwareAdapter.getInstance().getDrivetrain().rightSlave1Victor;
		WPI_VictorSPX rightSlave2Victor = HardwareAdapter.getInstance().getDrivetrain().rightSlave2Victor;

		leftMasterTalon.setNeutralMode(NeutralMode.Brake);
		leftSlave1Victor.setNeutralMode(NeutralMode.Brake);
		leftSlave2Victor.setNeutralMode(NeutralMode.Brake);
		rightMasterTalon.setNeutralMode(NeutralMode.Brake);
		rightSlave1Victor.setNeutralMode(NeutralMode.Brake);
		rightSlave2Victor.setNeutralMode(NeutralMode.Brake);
	}

	void disableBrakeMode() {
		WPI_TalonSRX leftMasterTalon = HardwareAdapter.getInstance().getDrivetrain().leftMasterTalon;
		WPI_VictorSPX leftSlave1Victor = HardwareAdapter.getInstance().getDrivetrain().leftSlave1Victor;
		WPI_VictorSPX leftSlave2Victor = HardwareAdapter.getInstance().getDrivetrain().leftSlave2Victor;

		WPI_TalonSRX rightMasterTalon = HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon;
		WPI_VictorSPX rightSlave1Victor = HardwareAdapter.getInstance().getDrivetrain().rightSlave1Victor;
		WPI_VictorSPX rightSlave2Victor = HardwareAdapter.getInstance().getDrivetrain().rightSlave2Victor;

		leftMasterTalon.setNeutralMode(NeutralMode.Coast);
		leftSlave1Victor.setNeutralMode(NeutralMode.Coast);
		leftSlave2Victor.setNeutralMode(NeutralMode.Coast);
		rightMasterTalon.setNeutralMode(NeutralMode.Coast);
		rightSlave1Victor.setNeutralMode(NeutralMode.Coast);
		rightSlave2Victor.setNeutralMode(NeutralMode.Coast);
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
        if(output.getControlType().equals(ControlType.kPosition) || output.getControlType().equals(ControlType.kVelocity)) {
            updateSparkGains(spark, output);
        }
        if(output.getArbitraryFF() != 0.0 && output.getControlType().equals(ControlType.kPosition)) {
            spark.getPIDController().setReference(output.getSetpoint(), output.getControlType(), 0, output.getArbitraryFF());
        } else {
            spark.getPIDController().setReference(output.getSetpoint(), output.getControlType());
        }
    }

	private void updateSparkGains(CANSparkMax spark, SparkMaxOutput output) {
		spark.getPIDController().setP(output.getGains().P);
		spark.getPIDController().setD(output.getGains().D);
		spark.getPIDController().setI(output.getGains().I);
		spark.getPIDController().setFF(output.getGains().F);
		spark.getPIDController().setIZone(output.getGains().izone);
		spark.setRampRate(output.getGains().rampRate);
	}
}