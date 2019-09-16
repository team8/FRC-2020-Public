package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.IntakeConstants;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import com.revrobotics.ControlType;

public class Intake extends Subsystem {

    public static Intake instance = new Intake();

    public static Intake getInstance() {
        return instance;
    }

    private SparkMaxOutput mSparkOutput = new SparkMaxOutput(ControlType.kSmartMotion);
    private double mTalonOutput, mRumbleLength;

    private Double mIntakeWantedAngle;
    private RobotState mRobotState;

    private boolean cachedCargoState;

    private enum WheelState {
        INTAKING,
        IDLE,
        EXPELLING,
        SLOW,
        MEDIUM,
        DROPPING
    }

    private enum UpDownState {
        CLIMBING,
        MANUAL, // Moving elevator with joystick
        CUSTOM_ANGLE, // Moving elevator with a control loop
        ZERO_VELOCITY,
        IDLE
    }

    public enum IntakeMacroState {
        STOWED, // Stowed at the start of the match
        GROUND_INTAKING, // Getting the cargo off the ground
        LIFTING, // Lifting the cargo into the intake
        DROPPING, // Dropping the cargo into the intake
        HOLDING_MID, // Moving the arm to the mid hold position and keeping it there
        DOWN,
        TUCK,
        HOLDING_ROCKET,
        INTAKING_ROCKET,
        EXPELLING_ROCKET,
        EXPELLING_CARGO,
        CLIMBING,
        HOLDING,
        IDLE
    }

    private WheelState mWheelState;
    private UpDownState mUpDownState;
    private IntakeMacroState mMacroState;

    private final static double requiredMSCancel = 100;
    private double mLastIntakeQueueTime = 0;

    protected Intake() {
        super("Intake");
        mMacroState = IntakeMacroState.IDLE;
    }

    @Override
    public void start() {
        mWheelState = WheelState.IDLE;
        mUpDownState = UpDownState.IDLE;
        mMacroState = IntakeMacroState.IDLE;
    }

    @Override
    public void stop() {
        mWheelState = WheelState.IDLE;
        mUpDownState = UpDownState.IDLE;
        mMacroState = IntakeMacroState.IDLE;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mRobotState = robotState;

        // The intake macro state has eight possible states.  Any state can be transferred to automatically or manually,
        // but some states need to set auxiliary variables, such as the queue times.

        // if (commands.wantedIntakeState == IntakeMacroState.CLIMBING && mMacroState != IntakeMacroState.CLIMBING) {
        //     HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getPIDController().setOutputRange(-1.0,1.0);
        //     HardwareAdapter.getInstance().getIntake().intakeSlaveSpark.getPIDController().setOutputRange(-1.0,1.0);
        // }
        // else if (commands.wantedIntakeState != IntakeMacroState.CLIMBING && mMacroState == IntakeMacroState.CLIMBING) {
        //     HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getPIDController().setOutputRange(-.75,.75);
        //     HardwareAdapter.getInstance().getIntake().intakeSlaveSpark.getPIDController().setOutputRange(-.75,.75);
        // }

        if (commands.wantedIntakeState == IntakeMacroState.HOLDING_MID && mMacroState == IntakeMacroState.GROUND_INTAKING) {
            // note: this needs to be nested so that the if/else can be exited
            if (mLastIntakeQueueTime + requiredMSCancel < System.currentTimeMillis()) {
                // move the intake back up from the ground
                mMacroState = IntakeMacroState.HOLDING_MID;
            }
        } else if (mMacroState == IntakeMacroState.GROUND_INTAKING && robotState.hasCargo) {
            mMacroState = IntakeMacroState.LIFTING;
            commands.wantedIntakeState = IntakeMacroState.LIFTING;
        } else if (commands.wantedIntakeState == IntakeMacroState.GROUND_INTAKING && mMacroState != IntakeMacroState.LIFTING) {
            mMacroState = IntakeMacroState.GROUND_INTAKING;
            mLastIntakeQueueTime = System.currentTimeMillis();
        } else if (mMacroState == IntakeMacroState.LIFTING && intakeOnTarget()) {
            mMacroState = IntakeMacroState.DROPPING;
            commands.wantedIntakeState = IntakeMacroState.DROPPING;
        } else if (commands.wantedIntakeState == IntakeMacroState.DROPPING && robotState.hasPusherCargo) {
            mMacroState = IntakeMacroState.HOLDING_MID;
            commands.wantedIntakeState = IntakeMacroState.HOLDING_MID; // reset it
        } else if (commands.wantedIntakeState == IntakeMacroState.HOLDING
                && (mMacroState != IntakeMacroState.HOLDING || mIntakeWantedAngle == null)) {
            mMacroState = commands.wantedIntakeState;
            mIntakeWantedAngle = robotState.intakeAngle; // Set to current position, hold it
//            Logger.getInstance().logRobotThread(Level.INFO, "setting wanted intake pos to " + robotState.intakeAngle);
        } else if (mMacroState != IntakeMacroState.DROPPING
                && !(mMacroState == IntakeMacroState.GROUND_INTAKING && commands.wantedIntakeState == IntakeMacroState.HOLDING_ROCKET)) {
            mMacroState = commands.wantedIntakeState;
        }

//        System.out.println(mMacroState);
//        System.out.println(commands.wantedIntakeState);
//        System.out.println(robotState.intakeAngle);

        commands.hasCargo = robotState.hasCargo;

        // FEED FORWARD MODEL:
        // 1. Compensate for gravity on the CM.
        // 2. Compensate for robot acceleration.  Derivation is similar to that for an inverted pendulum,
        // and can be found on slack.
        // 3. Compensate for centripetal acceleration on the arm.
        double arbitraryDemand = IntakeConstants.kGravityFF * Math.cos(Math.toRadians(robotState.intakeAngle - IntakeConstants.kAngleOffset))
                + IntakeConstants.kAccelerationCompensation * robotState.robotAcceleration * Math.sin(Math.toRadians(robotState.intakeAngle - IntakeConstants.kAngleOffset))
                + IntakeConstants.kCentripetalCoefficient * robotState.drivePose.headingVelocity * robotState.drivePose.headingVelocity * Math.sin(Math.toRadians(robotState.intakeAngle - IntakeConstants.kAngleOffset));

        switch (mMacroState) {
            case STOWED:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = IntakeConstants.kMaxAngle;
                break;
            case GROUND_INTAKING:
                mWheelState = WheelState.INTAKING;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = IntakeConstants.kIntakeAngle;
                break;
            case LIFTING:
                mWheelState = WheelState.SLOW;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = IntakeConstants.kHandOffAngle;
                break;
            case DROPPING:
                mWheelState = WheelState.DROPPING;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = IntakeConstants.kHandOffAngle;
                // todo: add some sort of timeout so this doesn't finish immediately
                break;
            case HOLDING_MID:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = IntakeConstants.kHoldAngle;
                break;
            case HOLDING_ROCKET:
            case TUCK:
                mWheelState = WheelState.SLOW;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = IntakeConstants.kRocketExpelAngle;
                break;
            case INTAKING_ROCKET:
                mWheelState = WheelState.MEDIUM;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = IntakeConstants.kRocketExpelAngle;
                break;
            case EXPELLING_ROCKET:
                mWheelState = WheelState.EXPELLING;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = IntakeConstants.kRocketExpelAngle;
                break;
            case CLIMBING:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = IntakeConstants.kClimbAngle;
                break;
            case DOWN:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = IntakeConstants.kIntakeAngle;
                break;
            case HOLDING:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.ZERO_VELOCITY;
                break;
            case IDLE:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.IDLE;
                break;
        }

        switch (mWheelState) {
            case INTAKING:
                if (commands.customIntakeSpeed) {
                    mTalonOutput = robotState.operatorXboxControllerInput.leftTrigger;
                } else {
                    mTalonOutput = IntakeConstants.kMotorVelocity;
                }
                break;
            case IDLE:
                mTalonOutput = 0;
                break;
            case DROPPING:
                mTalonOutput = IntakeConstants.kDroppingVelocity;
                break;
            case EXPELLING:
                mTalonOutput = IntakeConstants.kExpellingVelocity;
                break;
            case SLOW:
                mTalonOutput = IntakeConstants.kVerySlowly;
                break;
            case MEDIUM:
                mTalonOutput = IntakeConstants.kMedium;
                break;
        }

//        System.out.println(mMacroState);

        switch (mUpDownState) {
            case MANUAL:
                mSparkOutput.setIdle(); //TODO: Fix this based on what control method wanted
                break;
            case CUSTOM_ANGLE:
                boolean
                        inClosedLoopZone = mRobotState.intakeAngle >= IntakeConstants.kLowestAngle && mRobotState.intakeAngle <= IntakeConstants.kHighestAngle,
                        wantedAngleInClosedLoopZone = mIntakeWantedAngle >= IntakeConstants.kLowestAngle && mIntakeWantedAngle <= IntakeConstants.kHighestAngle;
                if (inClosedLoopZone || wantedAngleInClosedLoopZone) {
                    mSparkOutput.setTargetPositionSmartMotion(mIntakeWantedAngle, IntakeConstants.kArmDegreesPerRevolution, arbitraryDemand);
                } else {
                    mSparkOutput.setIdle();
                }
                break;
            case ZERO_VELOCITY:
                mSparkOutput.setTargetVelocity(0.0);
            case IDLE:
                mIntakeWantedAngle = null;
                mSparkOutput.setIdle();
                break;
        }

        if (!cachedCargoState && robotState.hasCargo) {
            mRumbleLength = 0.75;
        } else if (mRumbleLength <= 0) {
            mRumbleLength = -1;
        }

        cachedCargoState = robotState.hasCargo;

        CSVWriter.addData("intakeAngle", mRobotState.intakeAngle);
        if (mIntakeWantedAngle != null) CSVWriter.addData("intakeWantedPosition", mIntakeWantedAngle);
        CSVWriter.addData("intakeSparkSetpoint", mSparkOutput.getReference());
    }

    public double getRumbleLength() {
        return mRumbleLength;
    }

    public void decreaseRumbleLength() {
        mRumbleLength -= OtherConstants.deltaTime;
    }

    public SparkMaxOutput getSparkOutput() {
        return mSparkOutput;
    }

    public double getTalonOutput() {
        return mTalonOutput;
    }

    private boolean intakeOnTarget() {
        return mIntakeWantedAngle != null
                && (Math.abs(mIntakeWantedAngle - mRobotState.intakeAngle) < IntakeConstants.kAcceptableAngularError)
                && (Math.abs(mRobotState.intakeVelocity) < IntakeConstants.kAngularVelocityError);
    }

    @Override
    public String getStatus() {
        return String.format("Intake State: %s\nOutput Control Mode: %s\nSpark Output: %.2f\nUp Down Output: %s", mWheelState, mSparkOutput.getControlType(), mSparkOutput.getReference(), mUpDownState);
    }
}