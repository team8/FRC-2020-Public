package com.palyrobotics.frc2020.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.config.constants.SpinnerConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Shooter;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.util.LoopOverrunDebugger;
import com.palyrobotics.frc2020.util.SparkMaxOutput;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.LazySparkMax;
import com.revrobotics.*;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Should only be used in robot package.
 */

class HardwareUpdater {
    /**
     * A Rev Color Match object is used to register and detect known colors. This can
     * be calibrated ahead of time or during operation.
     * <p>
     * This object uses a simple euclidian distance to estimate the closest match
     * with given confidence range.
     */
    public final ColorMatch mColorMatcher = new ColorMatch();

    public static final int TIMEOUT_MS = 500;

    private Drive mDrive;
    private Spinner mSpinner;
    private Intake mIntake;
    private Shooter mShooter;
    private double[] mAccelerometerAngles = new double[3]; // Cached array to prevent more garbage
    private final LoopOverrunDebugger mLoopOverrunDebugger = new LoopOverrunDebugger("UpdateState", 0.02);
    // private double[] mAccelerometerAngles = new double[3]; // Cached array to prevent more garbage

    HardwareUpdater(Drive drive, Spinner spinner, Intake intake, Shooter shooter) {
        mDrive = drive;
        mSpinner = spinner;
        mIntake = intake;
        mShooter = shooter;
    }

    void initHardware() {
        configureHardware();
        configureIntakeHardware();
        startUltrasonics();
    }

    private void configureHardware() {
        configureDriveHardware();
        configureSpinner();
        configureShooterHardware();
        configureMiscellaneousHardware();
    }

    private void configureDriveHardware() {
        HardwareAdapter.DrivetrainHardware driveHardware = HardwareAdapter.getInstance().getDrivetrainHardware();

        for (CANSparkMax spark : driveHardware.sparks) {
            spark.restoreFactoryDefaults();
            spark.enableVoltageCompensation(DrivetrainConstants.kMaxVoltage);
            CANEncoder encoder = spark.getEncoder();
            encoder.setPositionConversionFactor(DrivetrainConstants.kDriveMetersPerRotation);
            encoder.setVelocityConversionFactor(DrivetrainConstants.kDriveMetersPerSecondPerRpm);
            CANPIDController controller = spark.getPIDController();
            controller.setOutputRange(-DrivetrainConstants.kDriveMaxClosedLoopOutput, DrivetrainConstants.kDriveMaxClosedLoopOutput);
            DriveConfig config = Configs.get(DriveConfig.class);
//            spark.setSmartCurrentLimit(config.stallCurrentLimit, config.freeCurrentLimit, config.freeRpmLimit);
            spark.setOpenLoopRampRate(config.controllerRampRate);
            spark.setClosedLoopRampRate(config.controllerRampRate);
        }

        // Invert right side
        driveHardware.leftMasterSpark.setInverted(false);
        driveHardware.leftSlave1Spark.setInverted(false);
        driveHardware.leftSlave2Spark.setInverted(false);

        driveHardware.rightMasterSpark.setInverted(true);
        driveHardware.rightSlave1Spark.setInverted(true);
        driveHardware.rightSlave2Spark.setInverted(true);

        driveHardware.leftSlave1Spark.follow(driveHardware.leftMasterSpark);
        driveHardware.leftSlave2Spark.follow(driveHardware.leftMasterSpark);
        driveHardware.rightSlave1Spark.follow(driveHardware.rightMasterSpark);
        driveHardware.rightSlave2Spark.follow(driveHardware.rightMasterSpark);

        resetDriveSensors();
    }

    private void configureSpinner() {
        mColorMatcher.addColorMatch(SpinnerConstants.kCyanCPTarget);
        mColorMatcher.addColorMatch(SpinnerConstants.kGreenCPTarget);
        mColorMatcher.addColorMatch(SpinnerConstants.kRedCPTarget);
        mColorMatcher.addColorMatch(SpinnerConstants.kYellowCPTarget);
    }

    private void configureIntakeHardware() {
        HardwareAdapter.IntakeHardware intakeHardware = HardwareAdapter.getInstance().getIntakeHardware();

        intakeHardware.intakeVictor.setInverted(false);
    }

    private void configureShooterHardware() {
        LazySparkMax masterSpark = HardwareAdapter.getInstance().getShooterHardware().masterSpark;
        LazySparkMax slaveSpark = HardwareAdapter.getInstance().getShooterHardware().slaveSpark;

        masterSpark.setInverted(false);
        slaveSpark.setInverted(true);

        masterSpark.restoreFactoryDefaults();
        slaveSpark.restoreFactoryDefaults();

        masterSpark.enableVoltageCompensation(12.0);
        masterSpark.enableVoltageCompensation(12.0);

        masterSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward,false);
        masterSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse,false);
        slaveSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kForward,false);
        slaveSpark.enableSoftLimit(CANSparkMax.SoftLimitDirection.kReverse,false);

        slaveSpark.follow(masterSpark);

    }

    private void configureMiscellaneousHardware() {
        // UsbCamera fisheyeCam = HardwareAdapter.getInstance().getMiscellaneousHardware().fisheyeCam;
        // fisheyeCam.setResolution(640, 360); // Original is 1920 x 1080
    }

    public void resetDriveSensors() {
        HardwareAdapter.DrivetrainHardware driveHardware = HardwareAdapter.getInstance().getDrivetrainHardware();
        driveHardware.gyro.setYaw(0, TIMEOUT_MS);
        driveHardware.gyro.setFusedHeading(0, TIMEOUT_MS);
        driveHardware.gyro.setAccumZAngle(0, TIMEOUT_MS);
        driveHardware.sparks.forEach(spark -> spark.getEncoder().setPosition(0.0));
        System.out.println("Drive Sensors Reset");
    }

    // TODO: ultrasonics
    private void startUltrasonics() {
//         Ultrasonic
//                 intakeUltrasonicLeft = HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft,
//                 intakeUltrasonicRight = HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight,
//                 pusherUltrasonic = HardwareAdapter.getInstance().getPusher().pusherUltrasonic;
// 		Ultrasonic pusherSecondaryUltrasonic = HardwareAdapter.getInstance().getPusher().pusherSecondaryUltrasonic;
//
//
//         intakeUltrasonicLeft.setAutomaticMode(true);
//         intakeUltrasonicRight.setAutomaticMode(true);
//         pusherUltrasonic.setAutomaticMode(true);
//		pusherSecondaryUltrasonic.setAutomaticMode(true);
//
//         intakeUltrasonicLeft.setEnabled(true);
//         intakeUltrasonicRight.setEnabled(true);
//         pusherUltrasonic.setEnabled(true);
// 		pusherSecondaryUltrasonic.setEnabled(true);
    }

    /**
     * Takes all of the sensor data from the hardware, and unwraps it into the current {@link RobotState}.
     */
    void updateState(RobotState robotState) {
        mLoopOverrunDebugger.reset();

        HardwareAdapter.DrivetrainHardware drivetrain = HardwareAdapter.getInstance().getDrivetrainHardware();

        robotState.leftDriveVelocity = drivetrain.leftMasterEncoder.getVelocity() / 60.0;
        robotState.rightDriveVelocity = drivetrain.rightMasterEncoder.getVelocity() / 60.0;
        robotState.leftDrivePosition = drivetrain.leftMasterEncoder.getPosition();
        robotState.rightDrivePosition = drivetrain.rightMasterEncoder.getPosition();

        //updating color sensor data
        robotState.detectedRGBVals = HardwareAdapter.getInstance().getMiscellaneousHardware().mColorSensor.getColor();
        robotState.closestColorRGB = mColorMatcher.matchClosestColor(robotState.detectedRGBVals);
        if (robotState.closestColorRGB.color == SpinnerConstants.kCyanCPTarget) {
            robotState.closestColorString = "Cyan";
        } else if (robotState.closestColorRGB.color == SpinnerConstants.kYellowCPTarget) {
            robotState.closestColorString = "Yellow";
        } else if (robotState.closestColorRGB.color == SpinnerConstants.kGreenCPTarget) {
            robotState.closestColorString = "Green";
        } else if (robotState.closestColorRGB.color == SpinnerConstants.kRedCPTarget) {
            robotState.closestColorString = "Red";
        }
        robotState.closestColorConfidence = robotState.closestColorRGB.confidence;

        //For testing purposes
        // System.out.println(robotState.closestColorString + " with confidence level of " + (robotState.closestColorConfidence * 100));
        // System.out.println(robotState.detectedRGBVals.red + ", " + robotState.detectedRGBVals.green + ", " + robotState.detectedRGBVals.blue);

        robotState.gameData = DriverStation.getInstance().getGameSpecificMessage();
        // if (robotState.gameData.length() > 0) {
        //     System.out.printf("Game data has been found, color is: %s%n", robotState.gameData);
        // }

        mLoopOverrunDebugger.addPoint("Basic");

        robotState.updateOdometry(drivetrain.gyro.getFusedHeading(), robotState.leftDrivePosition, robotState.rightDrivePosition);

        mLoopOverrunDebugger.addPoint("Odometry");

        updateUltrasonicSensors(robotState);

        mLoopOverrunDebugger.finish();
    }

    private void updateUltrasonicSensors(RobotState robotState) {
        //TODO: ultrasonics
    }

    /**
     * Updates the hardware to run with output values of subsystems
     */
    void updateHardware() {
        updateDrivetrain();
        updateSpinner();
        updateIntake();
        updateShooter();
        updateMiscellaneousHardware();
    }

    /**
     * Updates the drivetrain Uses SparkOutput and can run off-board control loops through SRX
     */
    private void updateDrivetrain() {
        updateSparkMax(HardwareAdapter.getInstance().getDrivetrainHardware().leftMasterSpark, mDrive.getDriveSignal().leftOutput);
        updateSparkMax(HardwareAdapter.getInstance().getDrivetrainHardware().rightMasterSpark, mDrive.getDriveSignal().rightOutput);
//        CSVWriter.addData("leftActualPower", HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark.getAppliedOutput());
//        CSVWriter.addData("rightActualPower", HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark.getAppliedOutput());
//        System.out.println("HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark = " + HardwareAdapter.getInstance().getDrivetrain().leftMasterSpark.getAppliedOutput());
//        System.out.println("HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark = " + HardwareAdapter.getInstance().getDrivetrain().rightMasterSpark.getAppliedOutput());
    }

    private void updateSpinner() {
        HardwareAdapter.getInstance().getSpinnerHardware().spinnerTalon.set(ControlMode.PercentOutput, mSpinner.getOutput());
    }

    /**
     * Updates the intake
     */
    private void updateIntake() {
        HardwareAdapter.getInstance().getIntakeHardware().intakeVictor.set(mIntake.getOutput());
    }

    private void updateShooter() {
        HardwareAdapter mHardwareAdapter = HardwareAdapter.getInstance();
        updateSparkMax(mHardwareAdapter.getShooterHardware().slaveSpark, mShooter.getOutput());
    }
    /**
     * Checks if the compressor should compress and updates it accordingly
     */
    private void updateMiscellaneousHardware() {
        Compressor compressor = HardwareAdapter.getInstance().getMiscellaneousHardware().compressor;
        if (shouldCompress()) {
            compressor.start();
        } else {
            compressor.stop();
        }
        HardwareAdapter.getInstance().getJoysticks().operatorXboxController.setRumble(shouldRumble());
    }

    /**
     * Runs the compressor only when the pressure too low or the current draw is low enough
     */
    private boolean shouldCompress() {
        return false;
    }

    // TODO: Update this
    private boolean shouldRumble() {
        return false;
    }

    void setDriveIdleMode(IdleMode idleMode) {
        HardwareAdapter.getInstance().getDrivetrainHardware().sparks.forEach(spark -> spark.setIdleMode(idleMode));
    }

    // private void updateTalonSRX(WPI_TalonSRX talon, TalonSRXOutput output) {
    //     if (output.getControlMode().equals(ControlMode.Position) || output.getControlMode().equals(ControlMode.Velocity)
    //             || output.getControlMode().equals(ControlMode.MotionMagic)) {
    //         talon.config_kP(output.profile, output.gains.p, 0);
    //         talon.config_kI(output.profile, output.gains.i, 0);
    //         talon.config_kD(output.profile, output.gains.d, 0);
    //         talon.config_kF(output.profile, output.gains.f, 0);
    //         talon.config_IntegralZone(output.profile, (int) Math.round(output.gains.iZone), 0);
    //         talon.configClosedloopRamp(output.gains.rampRate, 0);
    //     }
    //     if (output.getControlMode().equals(ControlMode.MotionMagic)) {
    //         talon.configMotionAcceleration(output.acceleration, 0);
    //         talon.configMotionCruiseVelocity(output.cruiseVelocity, 0);
    //     }
    //     if (output.getControlMode().equals(ControlMode.Velocity)) {
    //         talon.configAllowableClosedloopError(output.profile, 0, 0);
    //     }
    //     if (output.getArbitraryFF() != 0.0 && output.getControlMode().equals(ControlMode.Position)) {
    //         talon.set(output.getControlMode(), output.getSetPoint(), DemandType.ArbitraryFeedForward, output.getArbitraryFF());
    //     } else {
    //         talon.set(output.getControlMode(), output.getSetPoint(), DemandType.Neutral, 0.0);
    //     }
    // }

    private void updateSparkMax(LazySparkMax spark, SparkMaxOutput output) {
        ControlType controlType = output.getControlType();
        if (!Configs.get(RobotConfig.class).disableOutput) {
            spark.set(controlType, output.getReference(), output.getArbitraryDemand(), output.getGains());
//            System.out.printf("%s,%s%n", output.getControlType(), output.getReference());
        }
    }
}