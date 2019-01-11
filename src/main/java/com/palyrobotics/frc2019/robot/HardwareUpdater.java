package com.palyrobotics.frc2019.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Arm;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Intake;
import com.palyrobotics.frc2019.util.ClimberSignal;
import com.palyrobotics.frc2019.util.LEDColor;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.logger.Logger;
import com.palyrobotics.frc2019.util.trajectory.Kinematics;
import com.palyrobotics.frc2019.util.trajectory.RigidTransform2d;
import com.palyrobotics.frc2019.util.trajectory.Rotation2d;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

import java.util.*;
import java.util.logging.Level;

/**
 * Should only be used in robot package.
 */

class HardwareUpdater {

	//Subsystem references
	private Drive mDrive;
	private Arm mArm;
	private Intake mIntake;

	private double lastVelocity = 0;
	private double maxA = 0;
	private double maxV = 0;

	/**
	 * Hardware Updater for Forseti
	 */
	protected HardwareUpdater(Drive drive, Arm arm, Intake intake) {
		this.mDrive = drive;
		this.mArm = arm;
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

		//Disable arm talons 
		HardwareAdapter.getInstance().getArm().armMasterTalon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getArm().armSlaveVictor.set(ControlMode.Disabled, 0);

		//Disable intake talons
		HardwareAdapter.getInstance().getIntake().masterTalon.set(ControlMode.Disabled, 0);
		HardwareAdapter.getInstance().getIntake().slaveTalon.set(ControlMode.Disabled, 0);
	}

	void configureHardware() {
		configureDriveHardware();
		configureArmHardware();
		configureIntakeHardware();
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

		disableBrakeMode();

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

	void configureArmHardware() {
		WPI_TalonSRX masterTalon = HardwareAdapter.getInstance().getArm().armMasterTalon;
		WPI_VictorSPX slaveVictor = HardwareAdapter.getInstance().getArm().armSlaveVictor;

		masterTalon.setInverted(true);
		slaveVictor.setInverted(false);

		slaveVictor.follow(masterTalon);

		masterTalon.enableVoltageCompensation(true);
		slaveVictor.enableVoltageCompensation(true);

		masterTalon.configVoltageCompSaturation(14, 0);
		slaveVictor.configVoltageCompSaturation(14, 0);

		masterTalon.configReverseLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon.getDeviceID(), 0);
//		masterTalon.configForwardLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, HardwareAdapter.getInstance().getDrivetrain().rightMasterTalon.getDeviceID(), 0);

		masterTalon.overrideLimitSwitchesEnable(true);
		slaveVictor.overrideLimitSwitchesEnable(true);

		masterTalon.configPeakOutputForward(1, 0);
		masterTalon.configPeakOutputReverse(-1, 0);
		slaveVictor.configPeakOutputForward(1, 0);
		slaveVictor.configPeakOutputReverse(-1, 0);

		masterTalon.configClosedloopRamp(0.4, 0);
		masterTalon.configOpenloopRamp(0.4, 0);

		masterTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		masterTalon.setSensorPhase(true);

		//Zero encoders
		masterTalon.setSelectedSensorPosition(0, 0, 0);
	}

	void configureIntakeHardware() {

		WPI_VictorSPX masterTalon = HardwareAdapter.getInstance().getIntake().masterTalon;
		WPI_VictorSPX slaveTalon = HardwareAdapter.getInstance().getIntake().slaveTalon;

		Ultrasonic ultrasonic1 = HardwareAdapter.getInstance().getIntake().ultrasonic1;
		Ultrasonic ultrasonic2 = HardwareAdapter.getInstance().getIntake().ultrasonic2;

		masterTalon.setNeutralMode(NeutralMode.Brake);
		slaveTalon.setNeutralMode(NeutralMode.Brake);

		masterTalon.configOpenloopRamp(0.09, 0);
		slaveTalon.configOpenloopRamp(0.09, 0);

		masterTalon.enableVoltageCompensation(true);
		slaveTalon.enableVoltageCompensation(true);

		masterTalon.configVoltageCompSaturation(14, 0);
		slaveTalon.configVoltageCompSaturation(14, 0);

		//Disables forwards and reverse soft limits
		masterTalon.configForwardSoftLimitEnable(false, 0);
		masterTalon.configReverseSoftLimitEnable(false, 0);
		slaveTalon.configForwardSoftLimitEnable(false, 0);
		slaveTalon.configReverseSoftLimitEnable(false, 0);

		//Reverse right side
		if (Constants.kRobotName == Constants.RobotName.FORSETI) {
			masterTalon.setInverted(true);
		}
		else {
			masterTalon.setInverted(false);
		}
		slaveTalon.setInverted(false);

		//Set slave talons to follower mode
        slaveTalon.follow(masterTalon);

        ultrasonic1.setAutomaticMode(true);
		ultrasonic1.setEnabled(true);
		ultrasonic2.setAutomaticMode(true);
		ultrasonic2.setAutomaticMode(true);

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

		// left side
		Ultrasonic mUltrasonicLeft = HardwareAdapter.getInstance().getIntake().ultrasonic1;
		robotState.mLeftReadings.add(mUltrasonicLeft.getRangeInches());
//		System.out.println(mUltrasonicLeft.getRangeInches());
		if(robotState.mLeftReadings.size() > 10) {
			robotState.mLeftReadings.remove(0);
		}

		int leftTotal = 0;
		for (int i = 0; i < robotState.mLeftReadings.size(); i++) {
			if (robotState.mLeftReadings.get(i) < Constants.kIntakeCubeInchTolerance) {
				leftTotal += 1;
			}
		}

		// right side
		Ultrasonic mUltrasonicRight = HardwareAdapter.getInstance().getIntake().ultrasonic2;
		robotState.mRightReadings.add(mUltrasonicRight.getRangeInches());
//		System.out.println(mUltrasonicRight.getRangeInches());
		if(robotState.mRightReadings.size() > 10) {
			robotState.mRightReadings.remove(0);
		}

		int rightTotal = 0;
		for (int i = 0; i < robotState.mRightReadings.size(); i++) {
			if (robotState.mRightReadings.get(i) < Constants.kIntakeCubeInchTolerance) {
				rightTotal += 1;
			}
		}

		if (leftTotal > Constants.kRequiredUltrasonicCount && rightTotal > Constants.kRequiredUltrasonicCount) {
			robotState.hasCube = true;
		}
		else {
			robotState.hasCube = false;
		}

        robotState.cubeDistance = (mUltrasonicRight.getRangeInches() + mUltrasonicLeft.getRangeInches())/2;

//		System.out.println("Left: " + mUltrasonicLeft.getRangeInches());
//		System.out.println("Right: " + mUltrasonicRight.getRangeInches());
//		System.out.println(robotState.hasCube);


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

//		System.out.println(odometry.getTranslation());
		//System.out.println("Odometry = " + odometry.getTranslation().getX());
//		System.out.println("Velocity = " + velocity.dx);
//		System.out.println("Gyro angle = " + robotState.drivePose.heading);
//		System.out.println("Latest field to vehicle = " + robotState.getLatestFieldToVehicle().toString());
//		System.out.println("Encoder estimate = " + left_distance);

		double cv = (robotState.drivePose.leftEncVelocity + robotState.drivePose.rightEncVelocity)/2 * 1/Constants.kDriveSpeedUnitConversion;



//        //Update compressor pressure
//        robotState.compressorPressure = HardwareAdapter.getInstance().getMiscellaneousHardware().compressorSensor.getVoltage() * Constants.kForsetiCompressorVoltageToPSI; //TODO: Implement the constant!
//

        PowerDistributionPanel pdp = HardwareAdapter.getInstance().getMiscellaneousHardware().pdp;
        robotState.shovelCurrentDraw = pdp.getTotalCurrent() - pdp.getCurrent(Constants.kShovelID);
		if (robotState.shovelCurrentDraw > Constants.kMaxShovelCurrentDraw) {
			robotState.hasHatch = true;
		} else {
			robotState.hasHatch = false;
		}

		//Update arm sensors
		robotState.armPosition = HardwareAdapter.getInstance().getArm().armMasterTalon.getSelectedSensorPosition(0);
		robotState.armVelocity = HardwareAdapter.getInstance().getArm().armMasterTalon.getSelectedSensorVelocity(0);
		robotState.armAngle = HardwareAdapter.getInstance().getArm().armPot.get(); 
		StickyFaults armStickyFaults = new StickyFaults();
		HardwareAdapter.getInstance().getArm().armMasterTalon.clearStickyFaults(0);
		HardwareAdapter.getInstance().getArm().armMasterTalon.getStickyFaults(armStickyFaults);
		robotState.hasArmStickyFaults = false;
	}

	/**
	 * Updates the hardware to run with output values of subsystems
	 */
	void updateHardware() {
		updateDrivetrain();
		updateArm();
		updateIntake();
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
//        double currentDraw = RobotState.getInstance().totalCurrentDraw;
//        double pressure = RobotState.getInstance().compressorPressure;
//        return currentDraw * pressure < Constants.kForsetiPressureCurrentProductThreshold; //TODO: Implement this!
    	return !(RobotState.getInstance().gamePeriod == RobotState.GamePeriod.AUTO);
    }

	/**
	 * Updates the arm
	 */
	private void updateArm() {

		if(mArm.getIsAtTop()) {
			TalonSRXOutput armHoldOutput = new TalonSRXOutput();
			armHoldOutput.setPercentOutput(Constants.kArmHoldVoltage);
			updateTalonSRX(HardwareAdapter.getInstance().getArm().armMasterTalon, armHoldOutput);
		} else {
			updateTalonSRX(HardwareAdapter.getInstance().getArm().armMasterTalon, mArm.getOutput());
		}

	}


	/**
	 * Updates the intake
	 */
	private void updateIntake() {
		HardwareAdapter.getInstance().getIntake().masterTalon.set(mIntake.getTalonOutput().getSetpoint());
		HardwareAdapter.getInstance().getIntake().slaveTalon.set(mIntake.getTalonOutput().getSetpoint());
		HardwareAdapter.getInstance().getIntake().inOutSolenoid.set(mIntake.getOpenCloseOutput() ? Value.kReverse : Value.kForward);
		HardwareAdapter.getInstance().getIntake().LED.set(LEDColor.getValue(LEDColor.getColor()));
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
		}
		else {
			talon.set(output.getControlMode(), output.getSetpoint(), DemandType.Neutral, 0.0);
		}
	}
}