package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.IntakeConfig;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.configv2.Configs;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;

public class Intake extends Subsystem {

    public static Intake instance = new Intake();

    public static Intake getInstance() {
        return instance;
    }

    private IntakeConfig mConfig = Configs.get(IntakeConfig.class);

    private SparkMaxOutput mOutput = SparkMaxOutput.getIdle();
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
        MANUAL,
        CUSTOM_ANGLE,
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
        super("intake");
    }

    @Override
    public void reset() {
        mMacroState = IntakeMacroState.IDLE;
        mOutput = SparkMaxOutput.getIdle();
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
        double arbitraryDemand = mConfig.gravityFF * Math.cos(Math.toRadians(robotState.intakeAngle - mConfig.angleOffset))
                + mConfig.accelerationCompensation * robotState.robotAcceleration * Math.sin(Math.toRadians(robotState.intakeAngle - mConfig.angleOffset))
                + mConfig.centripetalCoefficient * robotState.drivePose.headingVelocity * robotState.drivePose.headingVelocity * Math.sin(Math.toRadians(robotState.intakeAngle - mConfig.angleOffset));

        switch (mMacroState) {
            case STOWED:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = mConfig.maxAngle - ((1.0 + 2.0)/2.0);
                break;
            case GROUND_INTAKING:
                mWheelState = WheelState.INTAKING;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = mConfig.intakeAngle;
                break;
            case LIFTING:
                mWheelState = WheelState.SLOW;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = mConfig.handOffAngle;
                break;
            case DROPPING:
                mWheelState = WheelState.DROPPING;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = mConfig.handOffAngle;
                // todo: add some sort of timeout so this doesn't finish immediately
                break;
            case HOLDING_MID:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = mConfig.holdAngle;
                break;
            case HOLDING_ROCKET:
            case TUCK:
                mWheelState = WheelState.SLOW;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = mConfig.rocketExpelAngle;
                break;
            case INTAKING_ROCKET:
                mWheelState = WheelState.MEDIUM;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = mConfig.rocketExpelAngle;
                break;
            case EXPELLING_ROCKET:
                mWheelState = WheelState.EXPELLING;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = mConfig.rocketExpelAngle;
                break;
            case CLIMBING:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = mConfig.climbAngle;
                break;
            case DOWN:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_ANGLE;
                mIntakeWantedAngle = mConfig.intakeAngle;
                break;
            case HOLDING:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.ZERO_VELOCITY;
                break;
            default:
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
                    mTalonOutput = mConfig.motorVelocity;
                }
                break;
            case DROPPING:
                mTalonOutput = mConfig.droppingVelocity;
                break;
            case EXPELLING:
                mTalonOutput = mConfig.expellingVelocity;
                break;
            case SLOW:
                mTalonOutput = mConfig.verySlowly;
                break;
            case MEDIUM:
                mTalonOutput = mConfig.medium;
                break;
            default:
            case IDLE:
                mTalonOutput = 0;
                break;
        }

//        System.out.println(mMacroState);

        switch (mUpDownState) {
            case MANUAL:
                mOutput.setIdle(); //TODO: Fix this based on what control method wanted
                break;
            case CUSTOM_ANGLE:
//                boolean
//                        inClosedLoopZone = mRobotState.intakeAngle >= IntakeConstants.kLowestAngle && mRobotState.intakeAngle <= IntakeConstants.kHighestAngle,
//                        wantedAngleInClosedLoopZone = mIntakeWantedAngle >= IntakeConstants.kLowestAngle && mIntakeWantedAngle <= IntakeConstants.kHighestAngle;
//                if (inClosedLoopZone || wantedAngleInClosedLoopZone) {
//                    mSparkOutput.setTargetPositionSmartMotion(mIntakeWantedAngle, IntakeConstants.kArmDegreesPerRevolution, arbitraryDemand);
//                } else {
//                    mSparkOutput.setIdle();
//                }
                mOutput.setTargetPositionSmartMotion(mIntakeWantedAngle, arbitraryDemand);
                break;
            case ZERO_VELOCITY:
                mOutput.setTargetSmartVelocity(0.0, arbitraryDemand);
            default:
            case IDLE:
                mIntakeWantedAngle = null;
                mOutput.setIdle();
                break;
        }

        if (!cachedCargoState && robotState.hasCargo) {
            mRumbleLength = 0.75;
        } else if (mRumbleLength <= 0) {
            mRumbleLength = -1;
        }

        cachedCargoState = robotState.hasCargo;

        CSVWriter.addData("intakeAngle", mRobotState.intakeAngle);
        CSVWriter.addData("intakeOutput", HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getAppliedOutput());
        if (mIntakeWantedAngle != null) CSVWriter.addData("intakeWantedAngle", mIntakeWantedAngle);
        CSVWriter.addData("intakeTargetAngle", mOutput.getReference());
    }

    public double getRumbleLength() {
        return mRumbleLength;
    }

    public void decreaseRumbleLength() {
        mRumbleLength -= OtherConstants.deltaTime;
    }

    public SparkMaxOutput getSparkOutput() {
        return mOutput;
    }

    public double getTalonOutput() {
        return mTalonOutput;
    }

    private boolean intakeOnTarget() {
        return mIntakeWantedAngle != null
                && (Math.abs(mIntakeWantedAngle - mRobotState.intakeAngle) < mConfig.acceptableAngularError)
                && (Math.abs(mRobotState.intakeVelocity) < mConfig.angularVelocityError);
    }

    @Override
    public String getStatus() {
        return String.format("Intake State: %s%nOutput Control Mode: %s%nSpark Output: %.2f%nUp Down Output: %s", mWheelState, mOutput.getControlType(), mOutput.getReference(), mUpDownState);
    }
}