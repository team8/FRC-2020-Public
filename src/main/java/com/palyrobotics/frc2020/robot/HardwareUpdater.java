package com.palyrobotics.frc2020.robot;

import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2020.config.constants.OtherConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.util.LoopOverrunDebugger;
import com.palyrobotics.frc2020.util.SparkMaxOutput;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.LazySparkMax;
import com.revrobotics.*;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.ControlType;
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

    private Drive mDrive;
    private double[] mAccelerometerAngles = new double[3]; // Cached array to prevent more garbage

    HardwareUpdater(Drive drive) {
        mDrive = drive;
    }

    void initHardware() {
        configureHardware();
    }

    private void configureHardware() {
        configureDriveHardware();
        configureMiscellaneousHardware();
    }

    private void configureDriveHardware() {
        HardwareAdapter.DrivetrainHardware driveHardware = HardwareAdapter.getInstance().getDrivetrain();

        for (CANSparkMax spark : driveHardware.sparks) {
            spark.restoreFactoryDefaults();
            spark.enableVoltageCompensation(12.0);
            CANEncoder encoder = spark.getEncoder();
            encoder.setPositionConversionFactor(DrivetrainConstants.kDriveMetersPerRotation);
            encoder.setVelocityConversionFactor(DrivetrainConstants.kDriveMetersPerSecondPerRpm);
            CANPIDController controller = spark.getPIDController();
            controller.setOutputRange(-DrivetrainConstants.kDriveMaxClosedLoopOutput, DrivetrainConstants.kDriveMaxClosedLoopOutput);
            DriveConfig config = Configs.get(DriveConfig.class);
            spark.setSmartCurrentLimit(config.stallCurrentLimit, config.freeCurrentLimit, config.freeRpmLimit);
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

        // Set slave sparks to follower mode
        driveHardware.leftSlave1Spark.follow(driveHardware.leftMasterSpark);
        driveHardware.leftSlave2Spark.follow(driveHardware.leftMasterSpark);
        driveHardware.rightSlave1Spark.follow(driveHardware.rightMasterSpark);
        driveHardware.rightSlave2Spark.follow(driveHardware.rightMasterSpark);

        resetDriveSensors();
    }

    private void configureMiscellaneousHardware() {
        mColorMatcher.addColorMatch(OtherConstants.kCyanCPTarget);
        mColorMatcher.addColorMatch(OtherConstants.kGreenCPTarget);
        mColorMatcher.addColorMatch(OtherConstants.kRedCPTarget);
        mColorMatcher.addColorMatch(OtherConstants.kYellowCPTarget);

        // UsbCamera fisheyeCam = HardwareAdapter.getInstance().getMiscellaneousHardware().fisheyeCam;
        // fisheyeCam.setResolution(640, 360); // Original is 1920 x 1080
    }

    public void resetDriveSensors() {
        HardwareAdapter.DrivetrainHardware driveHardware = HardwareAdapter.getInstance().getDrivetrain();
        driveHardware.gyro.setYaw(0, 500);
        driveHardware.gyro.setFusedHeading(0, 500);
        driveHardware.gyro.setAccumZAngle(0, 500);
        driveHardware.sparks.forEach(spark -> spark.getEncoder().setPosition(0.0));
    }

    /**
     * Takes all of the sensor data from the hardware, and unwraps it into the current {@link RobotState}.
     */
    void updateState(RobotState robotState) {
        LoopOverrunDebugger loopOverrunDebugger = new LoopOverrunDebugger("UpdateState", 0.02);

        HardwareAdapter.DrivetrainHardware drivetrain = HardwareAdapter.getInstance().getDrivetrain();

        robotState.leftDriveVelocity = drivetrain.leftMasterEncoder.getVelocity() / 60.0;
        robotState.rightDriveVelocity = drivetrain.rightMasterEncoder.getVelocity() / 60.0;
        robotState.leftDrivePosition = drivetrain.leftMasterEncoder.getPosition();
        robotState.rightDrivePosition = drivetrain.rightMasterEncoder.getPosition();

        //updating color sensor data
        robotState.detectedRGBVals = HardwareAdapter.getInstance().getMiscellaneousHardware().mColorSensor.getColor();
        robotState.closestColorRGB = mColorMatcher.matchClosestColor(robotState.detectedRGBVals);
        if (robotState.closestColorRGB.color == OtherConstants.kCyanCPTarget) {
            robotState.closestColorString = "Cyan";
        } else if (robotState.closestColorRGB.color == OtherConstants.kYellowCPTarget) {
            robotState.closestColorString = "Yellow";
        } else if (robotState.closestColorRGB.color == OtherConstants.kGreenCPTarget) {
            robotState.closestColorString = "Green";
        } else if (robotState.closestColorRGB.color == OtherConstants.kRedCPTarget) {
            robotState.closestColorString = "Red";
        }
        robotState.closestColorConfidence = robotState.closestColorRGB.confidence;

        //For testing purposes
        System.out.println(robotState.closestColorString + " with confidence level of " + (robotState.closestColorConfidence * 100));
        System.out.println(robotState.detectedRGBVals.red + ", " + robotState.detectedRGBVals.green + ", " + robotState.detectedRGBVals.blue);


//        double robotVelocity = (robotState.drivePose.leftEncoderVelocity + robotState.drivePose.rightEncoderVelocity) / 2;

//        drivetrain.gyro.getAccelerometerAngles(mAccelerometerAngles);
//        robotState.robotAcceleration = mAccelerometerAngles[0];
//        robotState.robotVelocity = robotVelocity;


        robotState.gameData = DriverStation.getInstance().getGameSpecificMessage();
        if (robotState.gameData.length() > 0) {
            System.out.println("Game data has been found, color is: " + robotState.gameData);
        }

        loopOverrunDebugger.addPoint("Basic");

        robotState.updateOdometry(drivetrain.gyro.getFusedHeading(), robotState.leftDrivePosition, robotState.rightDrivePosition);

        loopOverrunDebugger.addPoint("Odometry");

        updateUltrasonicSensors(robotState);

        loopOverrunDebugger.addPoint("Ultrasonics");

        loopOverrunDebugger.finish();
    }

    private void updateUltrasonicSensors(RobotState robotState) {
        //TODO: ultrasonics
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
        return false;
    }

    // TODO: Update this
    private boolean shouldRumble() {
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