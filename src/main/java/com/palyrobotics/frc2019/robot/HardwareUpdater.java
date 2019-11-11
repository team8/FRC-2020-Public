package com.palyrobotics.frc2019.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.palyrobotics.frc2019.config.RobotConfig;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.constants.OtherConstants;
import com.palyrobotics.frc2019.config.subsystem.DriveConfig;
import com.palyrobotics.frc2019.config.subsystem.ElevatorConfig;
import com.palyrobotics.frc2019.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2019.config.subsystem.PusherConfig;
import com.palyrobotics.frc2019.subsystems.*;
import com.palyrobotics.frc2019.util.LoopOverrunDebugger;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.config.Configs;
import com.palyrobotics.frc2019.util.control.LazySparkMax;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2019.vision.Limelight;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.ControlType;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CircularBuffer;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * Should only be used in robot package.
 */

class HardwareUpdater {

    private Drive mDrive;
    private Intake mIntake;
    private Elevator mElevator;
    private Shooter mShooter;
    private Pusher mPusher;
    private Fingers mFingers;

    HardwareUpdater(Drive drive, Elevator elevator, Shooter shooter, Pusher pusher, Fingers fingers, Intake intake) {
        mDrive = drive;
        mElevator = elevator;
        mShooter = shooter;
        mPusher = pusher;
        mFingers = fingers;
        mIntake = intake;
    }

    void initHardware() {
        configureHardware();
        startUltrasonics();
    }

    private void configureHardware() {
        configureDriveHardware();
        configureElevatorHardware();
        configureIntakeHardware();
        configureShooterHardware();
        configurePusherHardware();
        configureMiscellaneousHardware();
        HardwareAdapter.getInstance().getIntake().calibrateIntakeEncoderWithPotentiometer();
    }

    private void configureDriveHardware() {
        HardwareAdapter.DrivetrainHardware driveHardware = HardwareAdapter.getInstance().getDrivetrain();

        for (CANSparkMax spark : driveHardware.sparks) {
            spark.restoreFactoryDefaults();
            spark.enableVoltageCompensation(11.0);
            spark.setSecondaryCurrentLimit(120);
            CANEncoder encoder = spark.getEncoder();
            encoder.setPositionConversionFactor(DrivetrainConstants.kDriveInchesPerRotation);
            encoder.setVelocityConversionFactor(DrivetrainConstants.kDriveSpeedUnitConversion);
            CANPIDController controller = spark.getPIDController();
            controller.setOutputRange(-DrivetrainConstants.kDriveMaxClosedLoopOutput, DrivetrainConstants.kDriveMaxClosedLoopOutput);
        }

        Configs.listen(DriveConfig.class, config -> {
            for (LazySparkMax spark : driveHardware.sparks) {
                spark.setSmartCurrentLimit(config.stallCurrentLimit, config.freeCurrentLimit, config.freeRpmLimit);
                spark.setOpenLoopRampRate(config.controllerRampRate);
                spark.setClosedLoopRampRate(config.controllerRampRate);
            }
        });

        driveHardware.resetSensors();

        // Invert right side
        driveHardware.leftMasterSpark.setInverted(false);
        driveHardware.leftSlave1Spark.setInverted(false);
        driveHardware.leftSlave2Spark.setInverted(false);

        driveHardware.rightMasterSpark.setInverted(true);
        driveHardware.rightSlave1Spark.setInverted(true);
        driveHardware.rightSlave2Spark.setInverted(true);

        // Set slave sparks to follower mode
        driveHardware.leftSlave1Spark.follow(driveHardware.leftMasterSpark);
        driveHardware.leftSlave2Spark.follow(driveHardware.leftMasterSpark);
        driveHardware.rightSlave1Spark.follow(driveHardware.rightMasterSpark);
        driveHardware.rightSlave2Spark.follow(driveHardware.rightMasterSpark);
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

        masterElevatorSpark.setClosedLoopRampRate(0.1);

        // TODO refactor into constants
        masterElevatorSpark.setSoftLimit(SoftLimitDirection.kForward, 0.0f);
        masterElevatorSpark.setSoftLimit(SoftLimitDirection.kReverse, ElevatorConfig.kMaxHeightInches);
        masterElevatorSpark.enableSoftLimit(SoftLimitDirection.kForward, false);
        masterElevatorSpark.enableSoftLimit(SoftLimitDirection.kReverse, false);

        masterElevatorSpark.getEncoder().setPositionConversionFactor(ElevatorConfig.kElevatorInchesPerRevolution);
        masterElevatorSpark.getEncoder().setVelocityConversionFactor(ElevatorConfig.kElevatorInchesPerMinutePerRpm);
    }

    private void configureIntakeHardware() {
        HardwareAdapter.IntakeHardware intakeHardware = HardwareAdapter.getInstance().getIntake();
        CANSparkMax
                intakeMasterSpark = intakeHardware.intakeMasterSpark,
                intakeSlaveSpark = intakeHardware.intakeSlaveSpark;
        WPI_TalonSRX intakeTalon = intakeHardware.intakeTalon;

        intakeMasterSpark.restoreFactoryDefaults();
        intakeSlaveSpark.restoreFactoryDefaults();

        intakeSlaveSpark.follow(intakeMasterSpark);

        intakeMasterSpark.enableVoltageCompensation(12.0);
        intakeSlaveSpark.enableVoltageCompensation(12.0);

        intakeMasterSpark.getEncoder().setPositionConversionFactor(IntakeConfig.kArmDegreesPerRevolution);
        intakeMasterSpark.getEncoder().setVelocityConversionFactor(IntakeConfig.kArmDegreesPerMinutePerRpm);

        intakeMasterSpark.setClosedLoopRampRate(0.05);

        intakeTalon.setInverted(true);

        intakeTalon.setNeutralMode(NeutralMode.Brake);

        intakeTalon.enableVoltageCompensation(true);
        intakeTalon.configVoltageCompSaturation(14, 0);
        intakeTalon.configForwardSoftLimitEnable(false, 0);
        intakeTalon.configReverseSoftLimitEnable(false, 0);

        intakeTalon.configPeakOutputForward(1.0, 0);
        intakeTalon.configPeakOutputReverse(-1.0, 0);
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

        pusherSpark.setClosedLoopRampRate(0.05);

        pusherSpark.getEncoder().setPositionConversionFactor(PusherConfig.kPusherInchesPerRotation);
        pusherSpark.getEncoder().setVelocityConversionFactor(PusherConfig.kPusherEncSpeedUnitConversion);
        pusherSpark.getPIDController().setOutputRange(-0.9, 0.9);

        pusherSpark.setSmartCurrentLimit(80);
        pusherSpark.setInverted(true);
        pusherSpark.setIdleMode(IdleMode.kBrake);
    }

    private void configureMiscellaneousHardware() {
        UsbCamera fisheyeCam = HardwareAdapter.getInstance().getMiscellaneousHardware().fisheyeCam;
        fisheyeCam.setResolution(640,360); // Original is 1920 x 1080
    }

    private void startUltrasonics() {
        Ultrasonic
                intakeUltrasonicLeft = HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft,
                intakeUltrasonicRight = HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight,
                pusherUltrasonic = HardwareAdapter.getInstance().getPusher().pusherUltrasonic;
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

    private double[] mAccelerometerAngles = new double[3]; // Cached array to prevent more garbage

    /**
     * Takes all of the sensor data from the hardware, and unwraps it into the current {@link RobotState}.
     */
    void updateState(RobotState robotState) {
        LoopOverrunDebugger loopOverrunDebugger = new LoopOverrunDebugger("UpdateState", 0.02);

        HardwareAdapter.DrivetrainHardware drivetrain = HardwareAdapter.getInstance().getDrivetrain();
        LazySparkMax elevatorSpark = HardwareAdapter.getInstance().getElevator().elevatorMasterSpark;

        robotState.leftDriveVelocity = drivetrain.leftMasterSpark.getEncoder().getVelocity();
        robotState.rightDriveVelocity = drivetrain.rightMasterSpark.getEncoder().getVelocity();

        CANEncoder elevatorEncoder = elevatorSpark.getEncoder();
        robotState.elevatorPosition = elevatorEncoder.getPosition();
        robotState.elevatorVelocity = elevatorEncoder.getVelocity();
//        robotState.elevatorAppliedOutput = elevatorSpark.getAppliedOutput();

//        PigeonIMU gyro = drivetrain.gyro;
//        robotState.drivePose.lastHeading = robotState.drivePose.heading;
//        robotState.drivePose.heading = gyro.getFusedHeading();
//        robotState.drivePose.headingVelocity = (robotState.drivePose.heading - robotState.drivePose.lastHeading) / DrivetrainConstants.kNormalLoopsDt;

//        robotState.drivePose.lastLeftEncoderPosition = robotState.drivePose.leftEncoderPosition;
//        robotState.drivePose.leftEncoderPosition = drivetrain.leftMasterSpark.getEncoder().getPosition();
//        robotState.drivePose.leftEncoderVelocity = drivetrain.leftMasterSpark.getEncoder().getVelocity();
//        robotState.drivePose.lastRightEncoderPosition = robotState.drivePose.rightEncoderPosition;
//        robotState.drivePose.rightEncoderPosition = drivetrain.rightMasterSpark.getEncoder().getPosition();
//        robotState.drivePose.rightEncoderVelocity = drivetrain.rightMasterSpark.getEncoder().getVelocity();

//        double robotVelocity = (robotState.drivePose.leftEncoderVelocity + robotState.drivePose.rightEncoderVelocity) / 2;

//        drivetrain.gyro.getAccelerometerAngles(mAccelerometerAngles);
//        robotState.robotAcceleration = mAccelerometerAngles[0];
//        robotState.robotVelocity = robotVelocity;

        LazySparkMax intakeSpark = HardwareAdapter.getInstance().getIntake().intakeMasterSpark;
        CANEncoder armEncoder = intakeSpark.getEncoder();
        robotState.intakeAngle = armEncoder.getPosition();
        robotState.intakeVelocity = armEncoder.getVelocity();
//        robotState.intakeAppliedOutput = intakeSpark.getAppliedOutput();

        LazySparkMax pusherSpark = HardwareAdapter.getInstance().getPusher().pusherSpark;
        CANEncoder pusherEncoder = pusherSpark.getEncoder();
        robotState.pusherPosition = pusherEncoder.getPosition();
        robotState.pusherVelocity = pusherEncoder.getVelocity();
//        robotState.pusherAppliedOutput = pusherSpark.getAppliedOutput();

        loopOverrunDebugger.addPoint("Basic");

//        double time = Timer.getFPGATimestamp();
//
//        Rotation2d
//                gyroAngle = Rotation2d.fromDegrees(robotState.drivePose.heading),
//                gyroVelocity = Rotation2d.fromDegrees(robotState.drivePose.headingVelocity);
//        RigidTransform2d odometry = robotState.generateOdometryFromSensors(
//                robotState.drivePose.leftEncoderPosition - robotState.drivePose.lastLeftEncoderPosition,
//                robotState.drivePose.rightEncoderPosition - robotState.drivePose.lastRightEncoderPosition,
//                gyroAngle
//        );
//        RigidTransform2d.Delta velocity = Kinematics.forwardKinematics(
//                robotState.drivePose.leftEncoderVelocity,
//                robotState.drivePose.rightEncoderVelocity,
//                gyroVelocity.getRadians()
//        );
//
//        robotState.addObservations(time, odometry, velocity);

        loopOverrunDebugger.addPoint("Odometry");

        updateUltrasonicSensors(robotState);

        loopOverrunDebugger.addPoint("Ultrasonics");

        loopOverrunDebugger.finish();
    }

    private boolean hasCargoFromReadings(CircularBuffer readings, double tolerance, int requiredCount) {
        int count = 0;
        for (int i = 0; i < RobotState.kUltrasonicBufferSize; i++) {
            if (readings.get(i) <= tolerance) count++;
        }
        return count >= requiredCount;
    }

    private void updateUltrasonicSensors(RobotState robotState) {
        /* Test for cargo in intake */

        Ultrasonic ultrasonicLeft = HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft;
        robotState.leftIntakeReadings.addFirst(ultrasonicLeft.getRangeInches());
        Ultrasonic ultrasonicRight = HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight;
        robotState.rightIntakeReadings.addFirst(ultrasonicRight.getRangeInches());

        IntakeConfig intakeConfig = Configs.get(IntakeConfig.class);
        robotState.hasIntakeCargo = hasCargoFromReadings(robotState.leftIntakeReadings, intakeConfig.cargoInchTolerance, intakeConfig.cargoCountRequired)
                || hasCargoFromReadings(robotState.rightIntakeReadings, intakeConfig.cargoInchTolerance, intakeConfig.cargoCountRequired);

        robotState.cargoDistance = Math.min(ultrasonicLeft.getRangeInches(), ultrasonicRight.getRangeInches());

        /* Test for cargo in carriage */

        // Cargo Distance from Pusher
        Ultrasonic pusherUltrasonic = HardwareAdapter.getInstance().getPusher().pusherUltrasonic;
        robotState.pusherReadings.addFirst(pusherUltrasonic.getRangeInches());

        boolean lastHasPusherCargoFar = robotState.hasPusherCargoFar;
        PusherConfig pusherConfig = Configs.get(PusherConfig.class);
        robotState.hasPusherCargo = hasCargoFromReadings(robotState.pusherReadings, pusherConfig.cargoTolerance, OtherConstants.kRequiredUltrasonicCount + 1);
        robotState.hasPusherCargoFar = hasCargoFromReadings(robotState.pusherReadings, pusherConfig.cargoToleranceFar, OtherConstants.kRequiredUltrasonicCount);

        if (lastHasPusherCargoFar != robotState.hasPusherCargoFar) {
            int properPipeline = robotState.hasIntakeCargo ? OtherConstants.kLimelightCargoPipeline : OtherConstants.kLimelightHatchPipeline;
            Limelight.getInstance().setPipeline(properPipeline);
        }

        robotState.cargoPusherDistance = pusherUltrasonic.getRangeInches();
    }

    /**
     * Updates the hardware to run with output values of subsystems
     */
    void updateHardware() {
        updateDrivetrain();
        updateElevator();
        updateShooter();
        updatePusher();
        updateFingers();
        updateIntake();
        updateMiscellaneousHardware();
    }

    /**
     * Updates the drivetrain Uses SparkOutput and can run off-board control loops through SRX
     */
    private void updateDrivetrain() {
        updateSparkMax(HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark, mDrive.getDriveSignal().leftOutput);
        updateSparkMax(HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark, mDrive.getDriveSignal().rightOutput);
//        CSVWriter.addData("leftActualPower", HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark.getAppliedOutput());
//        CSVWriter.addData("rightActualPower", HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark.getAppliedOutput());
//        System.out.println("HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark = " + HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark.getAppliedOutput());
//        System.out.println("HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark = " + HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark.getAppliedOutput());
    }

    /**
     * Checks if the compressor should compress and updates it accordingly
     */
    private void updateMiscellaneousHardware() {
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
        double
                intakeRumbleLength = mIntake.getRumbleLength(),
                shooterRumbleLength = mShooter.getRumbleLength();
        if (intakeRumbleLength > 0) {
            rumble = true;
            mIntake.decreaseRumbleLength();
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

    private void updateFingers() {
        HardwareAdapter.getInstance().getFingers().openCloseSolenoid.set(mFingers.getOpenCloseOutput());
        HardwareAdapter.getInstance().getFingers().pusherSolenoid.set(mFingers.getExpelOutput());
    }

    private void updateIntake() {
        updateSparkMax(HardwareAdapter.getInstance().getIntake().intakeMasterSpark, mIntake.getSparkOutput());
        HardwareAdapter.getInstance().getIntake().intakeTalon.set(mIntake.getTalonOutput());
    }

    void setDriveIdleMode(IdleMode idleMode) {
        HardwareAdapter.getInstance().getDrivetrain().sparks.forEach(spark -> spark.setIdleMode(idleMode));
    }

    void setElevatorIdleMode(IdleMode idleMode) {
        HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.setIdleMode(idleMode);
        HardwareAdapter.getInstance().getElevator().elevatorSlaveSpark.setIdleMode(idleMode);
    }

    void setArmIdleMode(IdleMode idleMode) {
        HardwareAdapter.getInstance().getIntake().intakeMasterSpark.setIdleMode(idleMode);
        HardwareAdapter.getInstance().getIntake().intakeSlaveSpark.setIdleMode(idleMode);
    }

//    private void updateTalonSRX(WPI_TalonSRX talon, TalonSRXOutput output) {
//        if (output.getControlMode().equals(ControlMode.Position) || output.getControlMode().equals(ControlMode.Velocity)
//                || output.getControlMode().equals(ControlMode.MotionMagic)) {
//            talon.config_kP(output.profile, output.gains.p, 0);
//            talon.config_kI(output.profile, output.gains.i, 0);
//            talon.config_kD(output.profile, output.gains.d, 0);
//            talon.config_kF(output.profile, output.gains.f, 0);
//            talon.config_IntegralZone(output.profile, (int) Math.round(output.gains.iZone), 0);
//            talon.configClosedloopRamp(output.gains.rampRate, 0);
//        }
//        if (output.getControlMode().equals(ControlMode.MotionMagic)) {
//            talon.configMotionAcceleration(output.acceleration, 0);
//            talon.configMotionCruiseVelocity(output.cruiseVelocity, 0);
//        }
//        if (output.getControlMode().equals(ControlMode.Velocity)) {
//            talon.configAllowableClosedloopError(output.profile, 0, 0);
//        }
//        if (output.getArbitraryFF() != 0.0 && output.getControlMode().equals(ControlMode.Position)) {
//            talon.set(output.getControlMode(), output.getSetPoint(), DemandType.ArbitraryFeedForward, output.getArbitraryFF());
//        } else {
//            talon.set(output.getControlMode(), output.getSetPoint(), DemandType.Neutral, 0.0);
//        }
//    }

    private void updateSparkMax(LazySparkMax spark, SparkMaxOutput output) {
        ControlType controlType = output.getControlType();
        if (!Configs.get(RobotConfig.class).disableSparkOutput) {
            spark.set(controlType, output.getReference(), output.getArbitraryDemand(), output.getGains());
//            System.out.printf("%s,%s%n", output.getControlType(), output.getReference());
        }
    }
}