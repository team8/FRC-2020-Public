package com.palyrobotics.frc2019.config;

public class Constants {
	public enum RobotName {
        VIDAR
	}

	public enum DriverName {
		BRYAN
	}

	public enum OperatorName {
		GRIFFIN
	}

	public enum FieldName {
		//we goin to cmp bois <- for sure :send-it:
		TEAM_8, TEAM_254, SVR, SVR_PRACTICE, GNR, GNR_PRACTICE, DMR, DMR_PRACTICE, CMP, CMP_PRACTICE
	}

	//Initialization constants
	public static final RobotName kRobotName = RobotName.VIDAR;
	public static final DriverName kDriverName = DriverName.BRYAN;
	public static final OperatorName kOperatorName = OperatorName.GRIFFIN;
	public static final FieldName kFieldName = FieldName.TEAM_8;

	// Time Constants (these might exist elsewhere but whatever)
	public static final double updatesPerSecond = 50;
	public static final double deltaTime = 1/ updatesPerSecond;


	/**
	 * Cheesy Drive Constants Set by DriverProfiles
	 */
	//Deadband for joysticks
	public static double kDeadband;
	public static double kMaxAccelRate;
	//Threshold for quickturn sensitivity change
	public static double kQuickTurnSensitivityThreshold;
	//Sensitivities for how fast non-quickturn turning is
	public static double kDriveSensitivity;
	//Sensitivities for quickturn
	public static double kQuickTurnSensitivity;
	public static double kPreciseQuickTurnSensitivity;
	//The rate at which the QuickStopAccumulator will decrease
	public static double kQuickStopAccumulatorDecreaseRate;
	//The value at which the QuickStopAccumulator will stop decreasing
	public static double kQuickStopAccumulatorDecreaseThreshold;
	public static double kNegativeInertiaScalar;
	//How much the QuickStopAccumulator is affected by the wheel
	//(1-alpha) is how much the QuickStopAccumulator is affected by the previous QuickStopAccumulator
	//Range: (0, 1)
	public static double kAlpha;
	public static double kCyclesUntilStop;

    /**
     * Physical field constants
     */
    public static double kUpperPlatformLength =  48.0;
    public static double kLevel1Width = 150.0;
    public static double kLevel2Width = 40.0;
    public static double kLevel3Width = 48.0;
    public static double kLowerPlatformLength = 48.0;
    public static double kCargoLineGap = 21.5;

    /**
     * Elevator Constants
     */
    public static final double kNominalUpwardsOutput = 0.1;
    public static final double kElevatorTopBottomDifferenceInches = 0.0;
    public static final double kElevatorHFXAcceptableError = 0.01;
    public static final double kElevatorBottomPositionInches = 0;
    public static final double kElevatorHoldVoltage = 0.11;

    public static double kElevatorUncalibratedManualPower;
    public static double kElevatorClosedLoopManualControlPositionSensitivity = 500;//250;
    public static double kElevatorConstantDownPower;

	/**
	 * Ultrasonics
	 */
	public static final int kLeftUltrasonicPing = 0;
	public static final int kLeftUltrasonicEcho = 1;
	public static final int kRightUltrasonicPing = 2;
	public static final int kRightUltrasonicEcho = 3;
	public static final double kIntakeCargoInchTolerance = 3;
    public static final double kIntakeCloseRoutineCloseNow = 5.8;
    public static final int kRequiredUltrasonicCount = 6;
    public static final int kVidarPusherRightUltrasonicPing = 0;
    public static final int kVidarPusherRightUltrasonicEcho = 0;
	public static final int kVidarPusherLeftUltrasonicPing = 0;
	public static final int kVidarPusherLeftUltrasonicEcho = 0;

	/*
	 * Control loop constants for both robots
	 */
	public static final double kTurnInPlacePower = .5; //for bang bang
	public static final double kCalibratePower = -0.28;
	public static final double kDriveMaxClosedLoopOutput = 1.0;

	/**
	 * Unit conversions for Talons
	 */
	public static final double kDriveTicksPerInch = 4096 / (6.25 * Math.PI);
	public static final double kArmPotentiometerTicksPerDegree = 4096 / (360 * 10);

	// Using the NEO built in Encoder, so we must account for reduction.  ~= 42 / 360 * 118
	public static final double kArmEncoderTicksPerDegree = 42 / (360) * 68/14 * 38/18 * 36/14 * 54/12;

	public static final double kPusherTicksPerInch = 42 / (1 * Math.PI); // todo: change the 1 to the actual sprocket size
    public static final double kElevatorTicksPerInch = 42 / (2.00 * Math.PI) * (50/12)*(52/26)*(28/44);
    public static final double kClimberTicksPerInch = 0;
	public static final double kIntakeTicksPerInch = 4096 / (6.25 * Math.PI); //TODO: CHANGE THIS
	public static final double kDriveInchesPerDegree = 0.99 * 21.5 / 90;
	public static final double kDriveSpeedUnitConversion = 4096 / (6.25 * Math.PI * 10);

	public static final double kIntakeIntakingPosition = 10; // degrees relatively to the plane of the field.
	public static final double kIntakeHoldingPosition = 65; // same relative angle as above
	public static final double kIntakeHandoffPosition = 90; // place where the drop to the elevator occurs


	/**
	 * Physical robot Constants
	 */
	public static final double kRobotWidthInches = 34.0;
	public static final double kRobotLengthInches = 39.0;
	public static final double kCenterOfRotationOffsetFromFrontInches = 13.0;
	public static final double kNullZoneAllowableBack = 5;

	/**
	 * Tolerances
	 */
	public static final double kAcceptableDrivePositionError = 15;
	public static final double kAcceptableDriveVelocityError = 5;
	public static final double kAcceptableShortDrivePositionError = 1;
	public static final double kAcceptableShortDriveVelocityError = 3;
    public static final double kAcceptablePusherPositionError = 0;
	public static final double kAcceptableTurnAngleError = 5;
	public static final double kAcceptableGyroZeroError = 3;
	public static final double kAcceptableEncoderZeroError = 50;

    public static final double kElevatorAcceptablePositionError = 40;
    public static final double kElevatorAcceptableVelocityError = 0.01;


	public static final double kIntakeAcceptableAngularError = 3;
	public static final double kIntakeAngularVelocityError = .05;

	public static final double kClimberAcceptablePositionError = 0;
    public static final double kClimberAcceptableVelocityError = 0;

	//Intake
	public static final double kIntakingMotorVelocity = .4;
	public static final double kIntakeFastIntakingVelocity = 0.8;
	public static final double kIntakeDroppingVelocity = -0.225;

	public static final double kIntakeMaxAngle = 120;
	public static final double kIntakeMaxAngleTicks = 2000;

	public static final int kIntakeVictorID = 0;
	public static final int kIntakeMasterDeviceID = 0;
	public static final int kIntakeSlaveDeviceID = 0;

	public static final double kIntakeGravityFF = 0;
	public static final double kIntakeAccelComp = 0;


// shooter
	public static final double kExpellingMotorVelocity = 0;


	/**
	 * Shovel
	 */
	public static final int kShovelID = 0;
	public static final int kShovelUpDownSolenoid = 0;
	public static final int kShovelMotorVelocity = 0;
	public static final int kShovelExpellingMotorVelocity = 0;
	public static final int kShovelSmallExpelMotorVelocity = 0;
	public static final int kShovelPDPPort = 0;

	public static final int kMaxShovelCurrentDraw = 0;
	public static final int kShovelHFXPort = 1;

	/**
	 * Pusher
	 */
	//TODO: Add values
	public static final int kVidarPusherRequiredUltrasonicCount = 0;
	public static final double kVidarPusherDistanceIn = 0;
	public static final double kVidarPusherDistanceMiddle = 0;
	public static final double kVidarPusherDistanceOut = 0;
	public static final double kVidarPusherCargoTolerance = 0;

	public static final double kVidarPusherPositionkP = 0;
	public static final double kVidarPusherPositionkI = 0;
	public static final double kVidarPusherPositionkD = 0;

    /**
     * AutoPlacer
     */
    public static final int kAutoPlacerSolenoidID = 0;

	public static boolean operatorXBoxController = true;

	/*
	 * !!! End of editable Constants! !!!
	 **********************************************************************************
	 */

	/*
	 * ************************************ Vidar ELECTRONIC CONSTANTS ************************************
	 */

	//DRIVETRAIN
	//PDP slots for drivetrain 0, 1, 2, 3, 12, 13
	public static final int kVidarLeftDriveMasterDeviceID = 0;
	public static final int kVidarLeftDriveSlave1DeviceID = 1;
	public static final int kVidarLeftDriveSlave2DeviceID = 2;

	public static final int kVidarRightDriveMasterDeviceID = 15;
	public static final int kVidarRightDriveSlave1DeviceID = 14;
	public static final int kVidarRightDriveSlave2DeviceID = 13;

	//ELEVATOR
    public static final int kVidarElevatorMasterSparkID = 0;
    public static final int kVidarElevatorSlaveSparkID = 0;

    public static final int kVidarElevatorDoubleSolenoidForwardsID = 0;
    public static final int kVidarElevatorDoubleSolenoidReverseID = 0;

    public static final int kElevatorHFXPort = 1;

	//INTAKE
	public static final int kVidarIntakeMasterDeviceID = 3;
	public static final int kVidarIntakeSlaveDeviceID = 4;

	//SHOOTER
	public static final int kVidarShooterMasterVictorDeviceID = 0;
	public static final int kVidarShooterSlaveVictorDeviceID = 1;

	//PUSHER
	public static final int kVidarPusherVictorID = 0;
	public static final int kVidarPusherPotID = 0;

	//FINGERS
	public static final int kVidarOpenCloseSolenoidForwardID = 0;
	public static final int kVidarOpenCloseSolenoidReverseID = 1;
	public static final int kVidarExpelSolenoidForwardID = 2;
	public static final int kVidarExpelSolenoidReverseID = 3;

	//PCM 0
	public static final int kVidarIntakeUpDownSolenoidForwardID = 2;
	public static final int kVidarIntakeUpDownSolenoidReverseID = 5;

	//PCM 1
	public static final int kInOutSolenoidA = 0;
	public static final int kInOutSolenoidB = 1;

	public static final int kGyroPort = 7;

	//!!! Physical constants
	public static final double kSquareCubeLength = 13.0;

	//!!! Loop rate of normal Looper
	public static final double kNormalLoopsDt = 0.02;

	//Adaptive Pure Pursuit Controller

	// Preprocessing constants
	// Distance between the intermediate points inserted in the drive path; set to 0 to turn off insertion
	public static final double kInsertionSpacingInches = 6.0;
	// Smoothing constants
	public static final double kSmoothingWeight = 0.90; // 1 = max smoothness
	public static final double kSmoothingWeightData = 1 - kSmoothingWeight;
	public static final double kSmoothingTolerance = 0.001; // When the change in path falls below the threshold it is basically converged
	public static final double kSmoothingMaxIters = 10000;
	public static final double kTurnVelocityReduction = 2; // A constant for reducing following speed during turns; 1 for slowest turning, 5 for fastest
	public static final double kDriveWheelDiameterInches = 6.25;
	public static final double kTrackLengthInches = 8.2;
	public static final double kTrackWidthInches = 25.4;
	public static final double kTrackEffectiveDiameter = (kTrackWidthInches * kTrackWidthInches + kTrackLengthInches * kTrackLengthInches) / kTrackWidthInches;
	public static final double kTrackScrubFactor = .9;
	public static final double kPathFollowingLookahead = 35.0;
	public static final double kPathFollowingMaxAccel = 120;
	public static final double kPathFollowingMaxVel = 100.0;
	//public static final double kPathFollowingMaxVel = 5 * kPathFollowingMaxAccel;
	public static final double kPathFollowingTolerance = 0.20;

	@Override
	public String toString() {
		return "kQuickStopAccumulatorDecreaseRate " + kQuickStopAccumulatorDecreaseRate + "kQuickStopAccumulatorDecreaseThreshold "
				+ kQuickStopAccumulatorDecreaseThreshold + "kNegativeInertiaScalar " + kNegativeInertiaScalar + "kAlpha " + kAlpha + "kDriveTicksPerInch "
				+ kDriveTicksPerInch + "kDriveInchesPerDegree" + kDriveInchesPerDegree + "kDriveSpeedUnitConversion " + kDriveSpeedUnitConversion;
	}
}