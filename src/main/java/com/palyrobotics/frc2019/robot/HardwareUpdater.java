package com.palyrobotics.frc2019.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2019.config.Constants.*;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.ElevatorConfig;
import com.palyrobotics.frc2019.subsystems.*;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.configv2.Configs;
import com.palyrobotics.frc2019.util.trajectory.Kinematics;
import com.palyrobotics.frc2019.util.trajectory.RigidTransform2d;
import com.palyrobotics.frc2019.util.trajectory.Rotation2d;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANPIDController.AccelStrategy;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * Should only be used in robot package.
 */

class HardwareUpdater {

    // Subsystem references
    private Drive mDrive;
    private Intake mIntake;
    private Elevator mElevator;
    private Shooter mShooter;
    private Pusher mPusher;
    private Shovel mShovel;
    private Fingers mFingers;

    HardwareUpdater(Drive drive, Elevator elevator, Shooter shooter, Pusher pusher, Shovel shovel, Fingers fingers, Intake intake) {
        mDrive = drive;
        mElevator = elevator;
        mShooter = shooter;
        mPusher = pusher;
        mShovel = shovel;
        mFingers = fingers;
        mIntake = intake;
    }

    void initHardware() {
//        Logger.getInstance().logRobotThread(Level.INFO, "Init hardware");
        configureHardware();
        startUltrasonics();
    }

    void disableSpeedControllers() {
//        Logger.getInstance().logRobotThread(Level.INFO, "Disabling sparks");

        //Disable drivetrain sparks
        HardwareAdapter.getInstance().getDrivetrain().sparks.forEach(CANSparkMax::disable);

        //Disable elevator sparks
        HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.disable();
        HardwareAdapter.getInstance().getElevator().elevatorSlaveSpark.disable();

        //Disable intake sparks
        HardwareAdapter.getInstance().getIntake().intakeMasterSpark.disable();
        HardwareAdapter.getInstance().getIntake().intakeSlaveSpark.disable();
        HardwareAdapter.getInstance().getIntake().intakeTalon.set(ControlMode.Disabled, 0);

        //Disable pusher sparks
        HardwareAdapter.getInstance().getPusher().pusherSpark.disable();

//        Logger.getInstance().logRobotThread(Level.INFO, "Disabling victors");

        // Disable shooter victors
        HardwareAdapter.getInstance().getShooter().shooterMasterVictor.set(ControlMode.Disabled, 0);
        HardwareAdapter.getInstance().getShooter().shooterSlaveVictor.set(ControlMode.Disabled, 0);
    }

    private void configureHardware() {
        configureShovelHardware();
        configureDriveHardware();
        configureElevatorHardware();
        configureIntakeHardware();
        configureShooterHardware();
        configurePusherHardware();
        startIntakeArm();
    }

    private void configureDriveHardware() {

        HardwareAdapter.DrivetrainHardware drivetrainHardware = HardwareAdapter.getInstance().getDrivetrain();
        drivetrainHardware.resetSensors();

        CANSparkMax leftMasterSpark = drivetrainHardware.leftMasterSpark;
        CANSparkMax leftSlave1Spark = drivetrainHardware.leftSlave1Spark;
        CANSparkMax leftSlave2Spark = drivetrainHardware.leftSlave2Spark;

        CANSparkMax rightMasterSpark = drivetrainHardware.rightMasterSpark;
        CANSparkMax rightSlave1Spark = drivetrainHardware.rightSlave1Spark;
        CANSparkMax rightSlave2Spark = drivetrainHardware.rightSlave2Spark;

//		leftMasterSpark.restoreFactoryDefaults();
//		leftSlave1Spark.restoreFactoryDefaults();
//		leftSlave2Spark.restoreFactoryDefaults();
//		rightMasterSpark.restoreFactoryDefaults();
//		rightSlave1Spark.restoreFactoryDefaults();
//		rightSlave2Spark.restoreFactoryDefaults();


//		leftMasterSpark.enableVoltageCompensation(12);
//		leftSlave1Spark.enableVoltageCompensation(12);
//		leftSlave2Spark.enableVoltageCompensation(12);
//		rightMasterSpark.enableVoltageCompensation(12);
//		rightSlave1Spark.enableVoltageCompensation(12);
//		rightSlave2Spark.enableVoltageCompensation(12);

        drivetrainHardware.sparks.forEach(spark -> {
            CANEncoder encoder = spark.getEncoder();
            CANPIDController controller = spark.getPIDController();
            encoder.setPositionConversionFactor(DrivetrainConstants.kDriveInchesPerRotation);
            encoder.setVelocityConversionFactor(DrivetrainConstants.kDriveSpeedUnitConversion);
            controller.setOutputRange(-DrivetrainConstants.kDriveMaxClosedLoopOutput, DrivetrainConstants.kDriveMaxClosedLoopOutput);
            spark.setSmartCurrentLimit(DrivetrainConstants.kCurrentLimit);
        });

        // leftMasterSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 3);
        // rightMasterSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 3);

        //Reverse right side
        leftMasterSpark.setInverted(false);
        leftSlave1Spark.setInverted(false);
        leftSlave2Spark.setInverted(false);

        rightMasterSpark.setInverted(true);
        rightSlave1Spark.setInverted(true);
        rightSlave2Spark.setInverted(true);

//		leftMasterSpark.setOpenLoopRampRate(.2);
//		leftSlave1Spark.setOpenLoopRampRate(.2);
//		leftSlave2Spark.setOpenLoopRampRate(.2);
//
//		rightMasterSpark.setOpenLoopRampRate(.2);
//		rightSlave1Spark.setOpenLoopRampRate(.2);
//		rightSlave2Spark.setOpenLoopRampRate(.2);

        // Set slave sparks to follower mode
        leftSlave1Spark.follow(leftMasterSpark);
        leftSlave2Spark.follow(leftMasterSpark);
        rightSlave1Spark.follow(rightMasterSpark);
        rightSlave2Spark.follow(rightMasterSpark);
    }

    private void configureElevatorHardware() {

        HardwareAdapter.ElevatorHardware elevatorHardware = HardwareAdapter.getInstance().getElevator();
        CANSparkMax masterElevatorSpark = elevatorHardware.elevatorMasterSpark;
        CANSparkMax slaveElevatorSpark = elevatorHardware.elevatorSlaveSpark;

        masterElevatorSpark.restoreFactoryDefaults();
        slaveElevatorSpark.restoreFactoryDefaults();
        elevatorHardware.resetSensors();

        slaveElevatorSpark.follow(masterElevatorSpark);

        masterElevatorSpark.enableVoltageCompensation(12);
        slaveElevatorSpark.enableVoltageCompensation(12);

        masterElevatorSpark.setInverted(true); // Makes it so that upwards is positive ticks. Flips motor and encoder.

        masterElevatorSpark.setIdleMode(IdleMode.kBrake);
        slaveElevatorSpark.setIdleMode(IdleMode.kBrake);

        // TODO refactor into constants
        masterElevatorSpark.setSoftLimit(SoftLimitDirection.kForward, 0.0f);
        masterElevatorSpark.setSoftLimit(SoftLimitDirection.kReverse, ElevatorConfig.kMaxHeightInches);
        masterElevatorSpark.enableSoftLimit(SoftLimitDirection.kForward, false);
        masterElevatorSpark.enableSoftLimit(SoftLimitDirection.kReverse, false);

        // TODO re-enable when Spark fixes the smart motion with conversion factors
//        masterElevatorSpark.getEncoder().setPositionConversionFactor(1.0 / ElevatorConfig.kElevatorRotationsPerInch);
//        masterElevatorSpark.getEncoder().setVelocityConversionFactor(ElevatorConfig.kElevatorSpeedUnitConversion);

        ElevatorConfig elevatorConfig = Configs.get(ElevatorConfig.class);
        updateSparkGains(masterElevatorSpark, Gains.elevatorPosition, 0);
        updateSmartMotionGains(
                masterElevatorSpark,
                new Gains(elevatorConfig.p, elevatorConfig.i, elevatorConfig.d, elevatorConfig.f, 0, 0.0),
                elevatorConfig.a, elevatorConfig.v,
                ElevatorConfig.kElevatorInchPerSecPerRpm
        );
    }

    private void configureIntakeHardware() {

        HardwareAdapter.IntakeHardware intakeHardware = HardwareAdapter.getInstance().getIntake();
        CANSparkMax intakeMasterSpark = intakeHardware.intakeMasterSpark;
        CANSparkMax intakeSlaveSpark = intakeHardware.intakeSlaveSpark;
        WPI_TalonSRX intakeTalon = intakeHardware.intakeTalon;

        intakeMasterSpark.restoreFactoryDefaults();
        intakeSlaveSpark.restoreFactoryDefaults();
        intakeHardware.resetSensors();

        intakeSlaveSpark.follow(intakeMasterSpark);

        intakeMasterSpark.enableVoltageCompensation(12);
        intakeSlaveSpark.enableVoltageCompensation(12);

        intakeMasterSpark.setIdleMode(IdleMode.kBrake);

        // TODO fix when conversion is fixed from Spark
//        intakeMasterSpark.getEncoder().setPositionConversionFactor(IntakeConstants.kArmDegreesPerRevolution);
//        intakeMasterSpark.getEncoder().setVelocityConversionFactor(IntakeConstants.kArmDegreePerSecPerRpm);

        intakeTalon.setInverted(true);

        intakeTalon.setNeutralMode(NeutralMode.Brake);

        intakeTalon.enableVoltageCompensation(true);
        intakeTalon.configVoltageCompSaturation(14, 0);
        intakeTalon.configForwardSoftLimitEnable(false, 0);
        intakeTalon.configReverseSoftLimitEnable(false, 0);

        intakeTalon.configPeakOutputForward(1, 0);
        intakeTalon.configPeakOutputReverse(-1, 0);

        updateSparkGains(intakeMasterSpark, Gains.intakePosition, 0);
        updateSmartMotionGains(
                intakeMasterSpark,
                Gains.intakeSmartMotion,
                Gains.kVidarIntakeSmartMotionMaxAcceleration, Gains.kVidarIntakeSmartMotionMaxVelocity,
                IntakeConstants.kArmDegreePerSecPerRpm
        );
    }

    private void configureShooterHardware() {
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

    private void configurePusherHardware() {

        HardwareAdapter.getInstance().getPusher().resetSensors();

        CANSparkMax pusherSpark = HardwareAdapter.getInstance().getPusher().pusherSpark;

        pusherSpark.restoreFactoryDefaults();

        pusherSpark.enableVoltageCompensation(12);

        pusherSpark.getEncoder().setPositionConversionFactor(PusherConstants.kPusherInchesPerRotation);
        pusherSpark.getEncoder().setVelocityConversionFactor(PusherConstants.kPusherEncSpeedUnitConversion);
        pusherSpark.getPIDController().setOutputRange(-0.7, 0.7);

        pusherSpark.setSmartCurrentLimit(56);
        pusherSpark.setInverted(true);
        pusherSpark.setIdleMode(IdleMode.kBrake);


        updateSparkGains(pusherSpark, Gains.pusherPosition);
    }

    private void startUltrasonics() {
        Ultrasonic intakeUltrasonicLeft = HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft;
        Ultrasonic intakeUltrasonicRight = HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight;
        Ultrasonic pusherUltrasonic = HardwareAdapter.getInstance().getPusher().pusherUltrasonic;
//		Ultrasonic pusherSecondaryUltrasonic = HardwareAdapter.getInstance().getPusher().pusherSecondaryUltrasonic;


        intakeUltrasonicLeft.setAutomaticMode(true);
        intakeUltrasonicRight.setAutomaticMode(true);
        pusherUltrasonic.setAutomaticMode(true);
//		pusherSecondaryUltrasonic.setAutomaticMode(true);

        intakeUltrasonicLeft.setEnabled(true);
        intakeUltrasonicRight.setEnabled(true);
        pusherUltrasonic.setEnabled(true);
//		pusherSecondaryUltrasonic.setEnabled(true);
    }

    private void configureShovelHardware() {
        WPI_TalonSRX shovelTalon = HardwareAdapter.getInstance().getShovel().shovelTalon;

        shovelTalon.setNeutralMode(NeutralMode.Brake);
        shovelTalon.configOpenloopRamp(0.09, 0);
        shovelTalon.enableVoltageCompensation(true);
        shovelTalon.configVoltageCompSaturation(14, 0);
        shovelTalon.configForwardSoftLimitEnable(false, 0);
        shovelTalon.configReverseSoftLimitEnable(false, 0);
    }

    private double[] m_AccelerometerAngles = new double[3]; // Cached array to prevent more garbage

    /**
     * Takes all of the sensor data from the hardware, and unwraps it into the current {@link RobotState}.
     */
    void updateState(RobotState robotState) {
        CANSparkMax
                leftMasterSpark = HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark,
                rightMasterSpark = HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark;

        robotState.leftStickInput.update(HardwareAdapter.getInstance().getJoysticks().driveStick);
        robotState.rightStickInput.update(HardwareAdapter.getInstance().getJoysticks().turnStick);

        robotState.operatorXboxControllerInput.update(HardwareAdapter.getInstance().getJoysticks().operatorXboxController);
//		robotState.backupStickInput.update(HardwareAdapter.getInstance().getJoysticks().backupStick);

        robotState.hatchIntakeUp = !HardwareAdapter.getInstance().getShovel().upDownHFX.get();
        robotState.shovelCurrentDraw = HardwareAdapter.getInstance().getMiscellaneousHardware().pdp.getCurrent(PortConstants.kVidarShovelPDPPort);
        robotState.hasHatch = (robotState.shovelCurrentDraw > ShovelConstants.kMaxShovelCurrentDraw);

        CANEncoder elevatorEncoder = HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.getEncoder();
        // TODO fix when conversion works
        robotState.elevatorPosition = elevatorEncoder.getPosition() * ElevatorConfig.kElevatorInchPerRevolution;
        robotState.elevatorVelocity = elevatorEncoder.getVelocity() * ElevatorConfig.kElevatorInchPerSecPerRpm;

        PigeonIMU gyro = HardwareAdapter.getInstance().getDrivetrain().gyro;
        if (gyro != null) {
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

        HardwareAdapter.getInstance().getDrivetrain().gyro.getAccelerometerAngles(m_AccelerometerAngles);
        robotState.robotAcceleration = m_AccelerometerAngles[0];
        robotState.robotVelocity = robotVelocity;

        CANEncoder armEncoder = HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder();
        robotState.intakeAngle = armEncoder.getPosition() * IntakeConstants.kArmDegreesPerRevolution;
        robotState.intakeVelocity = armEncoder.getVelocity() * IntakeConstants.kArmDegreePerSecPerRpm;

        double time = Timer.getFPGATimestamp();

        if (!robotState.cancelAuto && robotState.rightStickInput.getButtonPressed(7)) {
            robotState.cancelAuto = true;
        }

        //Rotation2d gyro_angle = Rotation2d.fromRadians((right_distance - left_distance) * Constants.kTrackScrubFactor
        ///Constants.kTrackEffectiveDiameter);
        Rotation2d
                gyroAngle = Rotation2d.fromDegrees(robotState.drivePose.heading),
                gyroVelocity = Rotation2d.fromDegrees(robotState.drivePose.headingVelocity);

        RigidTransform2d odometry = robotState.generateOdometryFromSensors(
                robotState.drivePose.leftEnc - robotState.drivePose.lastLeftEnc,
                robotState.drivePose.rightEnc - robotState.drivePose.lastRightEnc,
                gyroAngle
        );

        RigidTransform2d.Delta velocity = Kinematics.forwardKinematics(
                robotState.drivePose.leftEncVelocity,
                robotState.drivePose.rightEncVelocity,
                gyroVelocity.getRadians()
        );

        robotState.addObservations(time, odometry, velocity);

        // Update pusher sensors
        robotState.pusherPosition = HardwareAdapter.getInstance().getPusher().pusherSpark.getEncoder().getPosition();
        robotState.pusherVelocity = HardwareAdapter.getInstance().getPusher().pusherSpark.getEncoder().getVelocity();

        updateUltrasonicSensors(robotState);
    }

    private void startIntakeArm() {
        Robot.getRobotState().intakeStartAngle = IntakeConstants.kMaxAngle -
                1 / IntakeConstants.kArmPotentiometerTicksPerDegree * Math.abs(HardwareAdapter.getInstance().getIntake().potentiometer.get() -
                        IntakeConstants.kMaxAngleTicks);
        HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().setPosition(
                Robot.getRobotState().intakeStartAngle / IntakeConstants.kArmDegreesPerRevolution
        );
    }

    private void updateUltrasonicSensors(RobotState robotState) {
        // HAS CARGO IN INTAKE

        // left side
        Ultrasonic mUltrasonicLeft = HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft;
        robotState.mLeftReadings.add(mUltrasonicLeft.getRangeInches());
        if (robotState.mLeftReadings.size() > 10) {
            robotState.mLeftReadings.remove(0);
        }
        // right side
        Ultrasonic mUltrasonicRight = HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight;
        robotState.mRightReadings.add(mUltrasonicRight.getRangeInches());
        if (robotState.mRightReadings.size() > 10) {
            robotState.mRightReadings.remove(0);
        }

        int leftTotal = (int) robotState.mLeftReadings.stream().filter(i -> (i < IntakeConstants.kCargoInchTolerance)).count();
        int rightTotal = (int) robotState.mRightReadings.stream().filter(i -> (i < IntakeConstants.kCargoInchTolerance)).count();
        robotState.hasCargo = (leftTotal >= IntakeConstants.kCargoCountRequired || rightTotal >= IntakeConstants.kCargoCountRequired);
        robotState.cargoDistance = Math.min(mUltrasonicLeft.getRangeInches(), mUltrasonicRight.getRangeInches());


        // HAS CARGO IN CARRIAGE

        //Cargo Distance from Pusher
        Ultrasonic mPusherUltrasonic = HardwareAdapter.getInstance().getPusher().pusherUltrasonic;
        System.out.println(mPusherUltrasonic.getRangeInches());
        robotState.mPusherReadings.add(mPusherUltrasonic.getRangeInches());
        if (robotState.mPusherReadings.size() > 10) {
            robotState.mPusherReadings.remove(0);
        }


        int pusherTotalClose = (int) robotState.mPusherReadings.stream().filter(i -> i < PusherConstants.kVidarCargoTolerance).count();

        int pusherTotalFar = (int) robotState.mPusherReadings.stream().filter(i -> i < PusherConstants.kVidarCargoToleranceFar).count();

        robotState.hasPusherCargo = (pusherTotalClose > OtherConstants.kRequiredUltrasonicCount + 1);
        robotState.hasPusherCargoFar = (pusherTotalFar > OtherConstants.kRequiredUltrasonicCount);

        robotState.cargoPusherDistance = (mPusherUltrasonic.getRangeInches());
//		System.out.println(robotState.cargoPusherDistance);
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
        updateIntake();
        updateMiscellaneousHardware();
    }

    /**
     * Updates the drivetrain Uses SparkOutput and can run off-board control loops through SRX
     */
    private void updateDrivetrain() {
        updateSparkMax(HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark, mDrive.getDriveSignal().leftMotor);
        updateSparkMax(HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark, mDrive.getDriveSignal().rightMotor);

//		SparkMaxOutput c = new SparkMaxOutput();
//		c.setPercentOutput(HardwareAdapter.getInstance().getJoysticks().driveStick.getY());
//
////		c.setPercentOutput(throttle);
//
//		updateSparkMax(HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark, c);
//		updateSparkMax(HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark, c);

//		mSignal.leftMotor.setPercentOutput(throttle);
//		mSignal.rightMotor.setPercentOutput(throttle);
    }

    /**
     * Checks if the compressor should compress and updates it accordingly
     */
    private void updateMiscellaneousHardware() {
//        HardwareAdapter.getInstance().getMiscellaneousHardware().compressor.stop();
        if (shouldCompress()) {
            HardwareAdapter.getInstance().getMiscellaneousHardware().compressor.start();
        } else {
            HardwareAdapter.getInstance().getMiscellaneousHardware().compressor.stop();
        }
        HardwareAdapter.getInstance().getJoysticks().operatorXboxController.setRumble(shouldRumble());
    }

    /**
     * Runs the compressor only when the pressure too low or the current draw is low enough
     */
    private boolean shouldCompress() {
        return RobotState.getInstance().gamePeriod != RobotState.GamePeriod.AUTO && !RobotState.getInstance().isQuickTurning;
    }

    private boolean shouldRumble() {
        boolean rumble;
        double intakeRumbleLength = mIntake.getRumbleLength();
        double shovelRumbleLength = mShovel.getRumbleLength();
        double shooterRumbleLength = mShooter.getRumbleLength();

        if (intakeRumbleLength > 0) {
            rumble = true;
            mIntake.decreaseRumbleLength();
        } else if (shovelRumbleLength > 0) {
            rumble = true;
            mShovel.decreaseRumbleLength();
        } else if (shooterRumbleLength > 0) {
            rumble = true;
            mShooter.decreaseRumbleLength();
        } else {
            rumble = false;
        }

        return rumble;
    }

    private void updateShooter() {
        HardwareAdapter.getInstance().getShooter().shooterMasterVictor.set(mShooter.getOutput());
    }

    private void updateElevator() {
        updateSparkMax(HardwareAdapter.getInstance().getElevator().elevatorMasterSpark, mElevator.getOutput());
        HardwareAdapter.getInstance().getElevator().elevatorShifter.set(mElevator.getSolenoidOutput());
    }

    private void updatePusher() {
        updateSparkMax(HardwareAdapter.getInstance().getPusher().pusherSpark, mPusher.getPusherOutput());
    }

    private void updateShovel() {
        HardwareAdapter.getInstance().getShovel().shovelTalon.set(mShovel.getPercentOutput());
        HardwareAdapter.getInstance().getShovel().upDownSolenoid.set(mShovel.getUpDownOutput() ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
    }

    private void updateFingers() {
        HardwareAdapter.getInstance().getFingers().openCloseSolenoid.set(mFingers.getOpenCloseOutput() == DoubleSolenoid.Value.kReverse ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
        HardwareAdapter.getInstance().getFingers().pusherSolenoid.set(mFingers.getExpelOutput());
    }

    private void updateIntake() {
        updateSparkMax(HardwareAdapter.getInstance().getIntake().intakeMasterSpark, mIntake.getSparkOutput());
        HardwareAdapter.getInstance().getIntake().intakeTalon.set(mIntake.getTalonOutput());
//		System.out.println(HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getAppliedOutput());
    }

    void enableBrakeMode() {
        HardwareAdapter.getInstance().getDrivetrain().sparks.forEach(spark -> spark.setIdleMode(IdleMode.kBrake));
    }

    void disableBrakeMode() {
        HardwareAdapter.getInstance().getDrivetrain().sparks.forEach(spark -> spark.setIdleMode(IdleMode.kCoast));
    }

    private void updateTalonSRX(WPI_TalonSRX talon, TalonSRXOutput output) {
        if (output.getControlMode().equals(ControlMode.Position) || output.getControlMode().equals(ControlMode.Velocity)
                || output.getControlMode().equals(ControlMode.MotionMagic)) {
            talon.config_kP(output.profile, output.gains.P, 0);
            talon.config_kI(output.profile, output.gains.I, 0);
            talon.config_kD(output.profile, output.gains.D, 0);
            talon.config_kF(output.profile, output.gains.F, 0);
            talon.config_IntegralZone(output.profile, output.gains.iZone, 0);
            talon.configClosedloopRamp(output.gains.rampRate, 0);
        }
        if (output.getControlMode().equals(ControlMode.MotionMagic)) {
            talon.configMotionAcceleration(output.accel, 0);
            talon.configMotionCruiseVelocity(output.cruiseVel, 0);
        }
        if (output.getControlMode().equals(ControlMode.Velocity)) {
            talon.configAllowableClosedloopError(output.profile, 0, 0);
        }
        if (output.getArbitraryFF() != 0.0 && output.getControlMode().equals(ControlMode.Position)) {
            talon.set(output.getControlMode(), output.getSetpoint(), DemandType.ArbitraryFeedForward, output.getArbitraryFF());
        } else {
            talon.set(output.getControlMode(), output.getSetpoint(), DemandType.Neutral, 0.0);
        }
    }

    private void updateSparkMax(CANSparkMax spark, SparkMaxOutput output) {
//        if (output.getControlType() == ControlType.kSmartMotion) System.out.println(output.getSetpoint());
        spark.getPIDController().setReference(
                output.getControlType() == ControlType.kSmartMotion
                        ? output.getSmartMotionSetpointAdjusted()
                        : output.getSetpoint(),
                output.getControlType(),
                output.getControlType() == ControlType.kSmartMotion ? 1 : 0, // TODO named constants for PID slots
                output.getArbitraryDemand(),
                output.getControlType() == ControlType.kSmartMotion // TODO make both use percent out
                        ? CANPIDController.ArbFFUnits.kPercentOut
                        : CANPIDController.ArbFFUnits.kVoltage);
    }

    private void updateSparkGains(CANSparkMax spark, Gains gains) {
        updateSparkGains(spark, gains, 0);
    }

    private void updateSmartMotionGains(CANSparkMax spark, Gains gains, double acceleration, double velocity, double velocityConversion) {
        CANPIDController controller = spark.getPIDController();
        controller.setP(gains.P * velocityConversion, 1);
        controller.setD(gains.D * velocityConversion, 1);
        controller.setI(gains.I * velocityConversion, 1);
        controller.setFF(gains.F * velocityConversion, 1);
        controller.setIZone(gains.iZone, 1); // TODO use position conversion
        controller.setIAccum(0.0);
        controller.setSmartMotionMaxAccel(acceleration / velocityConversion, 1);
        controller.setSmartMotionMaxVelocity(velocity / velocityConversion, 1);
        controller.setOutputRange(-0.7, 0.7, 1);
        controller.setSmartMotionAccelStrategy(AccelStrategy.kSCurve, 1);
        controller.setSmartMotionAllowedClosedLoopError(0.0, 1); // TODO zero?
        controller.setSmartMotionMinOutputVelocity(0.0, 1);
        spark.setClosedLoopRampRate(gains.rampRate);
    }

    private void updateSparkGains(CANSparkMax spark, Gains gains, int slotID) {
        CANPIDController controller = spark.getPIDController();
        controller.setP(gains.P, slotID);
        controller.setD(gains.D, slotID);
        controller.setI(gains.I, slotID);
        controller.setFF(gains.F, slotID);
        controller.setIZone(gains.iZone, slotID);
        controller.setIAccum(0.0);
        spark.setClosedLoopRampRate(gains.rampRate); // TODO this is not a per PID slot basis, it is global config
    }
}