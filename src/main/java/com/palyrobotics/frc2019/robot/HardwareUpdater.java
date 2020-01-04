package com.palyrobotics.frc2019.robot;

import com.palyrobotics.frc2019.config.RobotConfig;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.subsystem.DriveConfig;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.util.LoopOverrunDebugger;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.config.Configs;
import com.palyrobotics.frc2019.util.control.LazySparkMax;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.ControlType;
import edu.wpi.cscore.UsbCamera;

/**
 * Should only be used in robot package.
 */

class HardwareUpdater {

    private Drive mDrive;
    private double[] mAccelerometerAngles = new double[3]; // Cached array to prevent more garbage

    HardwareUpdater(Drive drive) {
        mDrive = drive;
    }

    void initHardware() {
        configureHardware();
        startUltrasonics();
    }

    private void configureHardware() {
        configureDriveHardware();
        configureMiscellaneousHardware();
    }

    private void configureDriveHardware() {
        HardwareAdapter.DrivetrainHardware driveHardware = HardwareAdapter.getInstance().getDrivetrain();

        for (CANSparkMax spark : driveHardware.sparks) {
            spark.restoreFactoryDefaults();
            spark.enableVoltageCompensation(11.0);
            spark.setSecondaryCurrentLimit(120);
            CANEncoder encoder = spark.getEncoder();
            encoder.setPositionConversionFactor(DrivetrainConstants.kDriveMetersPerRotation);
            encoder.setVelocityConversionFactor(DrivetrainConstants.kDriveMetersPerSecondPerRpm);
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
        // TODO: dt sensor reset
        // driveHardware.resetSensors();

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

    private void configureMiscellaneousHardware() {
        UsbCamera fisheyeCam = HardwareAdapter.getInstance().getMiscellaneousHardware().fisheyeCam;
        fisheyeCam.setResolution(640, 360); // Original is 1920 x 1080
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
        LoopOverrunDebugger loopOverrunDebugger = new LoopOverrunDebugger("UpdateState", 0.02);

        HardwareAdapter.DrivetrainHardware drivetrain = HardwareAdapter.getInstance().getDrivetrain();

        robotState.leftDriveVelocity = drivetrain.leftMasterSpark.getEncoder().getVelocity();
        robotState.rightDriveVelocity = drivetrain.rightMasterSpark.getEncoder().getVelocity();


//        double robotVelocity = (robotState.drivePose.leftEncoderVelocity + robotState.drivePose.rightEncoderVelocity) / 2;

//        drivetrain.gyro.getAccelerometerAngles(mAccelerometerAngles);
//        robotState.robotAcceleration = mAccelerometerAngles[0];
//        robotState.robotVelocity = robotVelocity;


        loopOverrunDebugger.addPoint("Basic");

        // TODO: gyro
        // robotState.updateOdometry(drivetrain.gyro.getFusedHeading(), robotState.leftDriveVelocity, robotState.rightDriveVelocity);

        loopOverrunDebugger.addPoint("Odometry");

        updateUltrasonicSensors(robotState);

        loopOverrunDebugger.addPoint("Ultrasonics");

        loopOverrunDebugger.finish();
    }

    private void updateUltrasonicSensors(RobotState robotState) {
        //TODO: ultrasonics

        // Ultrasonic ultrasonicLeft = HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft;
        // robotState.leftIntakeReadings.addFirst(ultrasonicLeft.getRangeInches());
        // Ultrasonic ultrasonicRight = HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight;
        // robotState.rightIntakeReadings.addFirst(ultrasonicRight.getRangeInches());
        //
        // IntakeConfig intakeConfig = Configs.get(IntakeConfig.class);
        // robotState.hasIntakeCargo = hasCargoFromReadings(robotState.leftIntakeReadings, intakeConfig.cargoInchTolerance, intakeConfig.cargoCountRequired)
        //         || hasCargoFromReadings(robotState.rightIntakeReadings, intakeConfig.cargoInchTolerance, intakeConfig.cargoCountRequired);
        //
        // robotState.cargoDistance = Math.min(ultrasonicLeft.getRangeInches(), ultrasonicRight.getRangeInches());
        //
        // /* Test for cargo in carriage */
        //
        // // Cargo Distance from Pusher
        // Ultrasonic pusherUltrasonic = HardwareAdapter.getInstance().getPusher().pusherUltrasonic;
        // robotState.pusherReadings.addFirst(pusherUltrasonic.getRangeInches());
        //
        // boolean lastHasPusherCargoFar = robotState.hasPusherCargoFar;
        // PusherConfig pusherConfig = Configs.get(PusherConfig.class);
        // robotState.hasPusherCargo = hasCargoFromReadings(robotState.pusherReadings, pusherConfig.cargoTolerance, OtherConstants.kRequiredUltrasonicCount + 1);
        // robotState.hasPusherCargoFar = hasCargoFromReadings(robotState.pusherReadings, pusherConfig.cargoToleranceFar, OtherConstants.kRequiredUltrasonicCount);
        //
        // if (lastHasPusherCargoFar != robotState.hasPusherCargoFar) {
        //     int properPipeline = robotState.hasIntakeCargo ? OtherConstants.kLimelightCargoPipeline : OtherConstants.kLimelightHatchPipeline;
        //     Limelight.getInstance().setPipeline(properPipeline);
        // }
        //
        // robotState.cargoPusherDistance = pusherUltrasonic.getRangeInches();
    }

    /**
     * Updates the hardware to run with output values of subsystems
     */
    void updateHardware() {
        updateDrivetrain();
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

    // TODO: Update this
    private boolean shouldRumble() {
        // boolean rumble;
        // double
        //         intakeRumbleLength = mIntake.getRumbleLength(),
        //         shooterRumbleLength = mShooter.getRumbleLength();
        // if (intakeRumbleLength > 0) {
        //     rumble = true;
        //     mIntake.decreaseRumbleLength();
        // } else if (shooterRumbleLength > 0) {
        //     rumble = true;
        //     mShooter.decreaseRumbleLength();
        // } else {
        //     rumble = false;
        // }
        // return rumble;
        return false;
    }

    void setDriveIdleMode(IdleMode idleMode) {
        HardwareAdapter.getInstance().getDrivetrain().sparks.forEach(spark -> spark.setIdleMode(idleMode));
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
        if (!Configs.get(RobotConfig.class).disableSparkOutput) {
            spark.set(controlType, output.getReference(), output.getArbitraryDemand(), output.getGains());
//            System.out.printf("%s,%s%n", output.getControlType(), output.getReference());
        }
    }
}