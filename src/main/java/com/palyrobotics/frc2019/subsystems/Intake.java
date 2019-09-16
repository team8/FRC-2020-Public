package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.IntakeConstants;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import com.revrobotics.ControlType;

import java.util.Optional;

public class Intake extends Subsystem {
    public static Intake instance = new Intake();

    public static Intake getInstance() {
        return instance;
    }

    public static void resetInstance() {
        instance = new Intake();
    }

    private SparkMaxOutput mSparkOutput = new SparkMaxOutput(ControlType.kSmartMotion);
    private double mTalonOutput;
    private double mRumbleLength;

    private boolean movingDown = false;

    private Optional<Double> mIntakeWantedPosition = Optional.empty();
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
        MANUAL_POSITIONING, //Moving elevator with joystick
        CUSTOM_POSITIONING, //Moving elevator with a control loop
        IDLE
    }

    public enum IntakeMacroState {
        STOWED, // stowed at the start of the match
        GROUND_INTAKING, // Getting the cargo off the ground
        LIFTING, // lifting the cargo into the intake
        DROPPING, // dropping the cargo into the intake
        HOLDING_MID, // moving the arm to the mid hold position and keeping it there
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

    private double lastIntakeQueueTime = 0;
    private final double requiredMSCancel = 100;

    private double lastDropQueueTme = 0;
    private final double requiredMSDrop = 150;

    protected Intake() {
        super("Intake");
        this.mMacroState = IntakeMacroState.IDLE;
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

    public static long ticks = 0;

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

        if (commands.wantedIntakeState == IntakeMacroState.HOLDING_MID && this.mMacroState == IntakeMacroState.GROUND_INTAKING) {
            // note: this needs to be nested so that the if/else can be exited
            if (this.lastIntakeQueueTime + this.requiredMSCancel < System.currentTimeMillis()) {
                // move the intake back up from the ground
                this.mMacroState = IntakeMacroState.HOLDING_MID;
            }
        } else if (this.mMacroState == IntakeMacroState.GROUND_INTAKING && robotState.hasCargo) {
            this.mMacroState = IntakeMacroState.LIFTING;
            commands.wantedIntakeState = IntakeMacroState.LIFTING;
        } else if (commands.wantedIntakeState == IntakeMacroState.GROUND_INTAKING && this.mMacroState != IntakeMacroState.LIFTING) {
            this.mMacroState = IntakeMacroState.GROUND_INTAKING;
            this.lastIntakeQueueTime = System.currentTimeMillis();
        } else if (this.mMacroState == IntakeMacroState.LIFTING && intakeOnTarget()) {
            this.mMacroState = IntakeMacroState.DROPPING;
            commands.wantedIntakeState = IntakeMacroState.DROPPING;
            lastDropQueueTme = System.currentTimeMillis();
        } else if (commands.wantedIntakeState == IntakeMacroState.DROPPING && robotState.hasPusherCargo) {
            this.mMacroState = IntakeMacroState.HOLDING_MID;
            commands.wantedIntakeState = IntakeMacroState.HOLDING_MID; // reset it
        } else if (commands.wantedIntakeState == IntakeMacroState.HOLDING && (this.mMacroState != IntakeMacroState.HOLDING || mIntakeWantedPosition.isEmpty())) {
            this.mMacroState = commands.wantedIntakeState;
            mIntakeWantedPosition = Optional.of(robotState.intakeAngle); // setpoint is current position
//            Logger.getInstance().logRobotThread(Level.INFO, "setting wanted intake pos to " + robotState.intakeAngle);
        } else if (this.mMacroState != IntakeMacroState.DROPPING && !(this.mMacroState == IntakeMacroState.GROUND_INTAKING && commands.wantedIntakeState == IntakeMacroState.HOLDING_ROCKET)) {
            this.mMacroState = commands.wantedIntakeState;
        }

//        System.out.println(this.mMacroState);
//        System.out.println(commands.wantedIntakeState);
//        System.out.println(robotState.intakeAngle);

        commands.hasCargo = robotState.hasCargo;

        // FEED FORWARD MODEL:
        // 1. Compensate for gravity on the CM.
        // 2. Compensate for robot acceleration.  Derivation is similar to that for an inverted pendulum,
        // and can be found on slack.
        // 3. Compensate for centripetal acceleration on the arm.
        double arbitraryDemand = IntakeConstants.kGravityFF * Math.cos(Math.toRadians(robotState.intakeAngle - IntakeConstants.kAngleOffset))
                + IntakeConstants.kAccelComp * robotState.robotAcceleration * Math.sin(Math.toRadians(robotState.intakeAngle - IntakeConstants.kAngleOffset))
                + IntakeConstants.kCentripetalCoeff * robotState.drivePose.headingVelocity * robotState.drivePose.headingVelocity * Math.sin(Math.toRadians(robotState.intakeAngle - IntakeConstants.kAngleOffset));

        switch (mMacroState) {
            case STOWED:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(IntakeConstants.kMaxAngle);
                break;
            case GROUND_INTAKING:
                mWheelState = WheelState.INTAKING;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(IntakeConstants.kIntakingPosition);
                break;
            case LIFTING:
                mWheelState = WheelState.SLOW;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(IntakeConstants.kHandoffPosition);
                break;
            case DROPPING:
                mWheelState = WheelState.DROPPING;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(IntakeConstants.kHandoffPosition);
                // todo: add some sort of timeout so this doesn't finish immediately
                break;
            case HOLDING_MID:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(IntakeConstants.kHoldingPosition);
                break;
            case HOLDING_ROCKET:
            case TUCK:
                mWheelState = WheelState.SLOW;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(IntakeConstants.kRocketExpelPosition);
                break;
            case INTAKING_ROCKET:
                mWheelState = WheelState.MEDIUM;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(IntakeConstants.kRocketExpelPosition);
                break;
            case EXPELLING_ROCKET:
                mWheelState = WheelState.EXPELLING;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(IntakeConstants.kRocketExpelPosition);
                break;
            case CLIMBING:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(IntakeConstants.kClimbPosition);
                break;
            case DOWN:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(IntakeConstants.kIntakingPosition);
                break;
            case HOLDING:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
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
            case MANUAL_POSITIONING:
                mSparkOutput.setPercentOutput(0); //TODO: Fix this based on what control method wanted
                break;
            case CUSTOM_POSITIONING:
//                System.out.printf("%f, %f%n", mIntakeWantedPosition.orElseThrow(), HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getPosition() * IntakeConstants.kArmDegreesPerRevolution);
                mSparkOutput.setTargetPositionSmartMotion(mIntakeWantedPosition.orElseThrow(), IntakeConstants.kArmDegreesPerRevolution, arbitraryDemand);
                break;
            case IDLE:
                if (mIntakeWantedPosition.isPresent()) {
                    mIntakeWantedPosition = Optional.empty();
                }
                mSparkOutput.setPercentOutput(0.0);
                break;
        }

        if (!cachedCargoState && robotState.hasCargo) {
            mRumbleLength = 0.75;
        } else if (mRumbleLength <= 0) {
            mRumbleLength = -1;
        }

        cachedCargoState = robotState.hasCargo;

        CSVWriter.addData("intakeAngle", mRobotState.intakeAngle);
        mIntakeWantedPosition.ifPresent(intakeWantedPosition -> CSVWriter.addData("intakeWantedPosition", intakeWantedPosition));
        CSVWriter.addData("intakeSparkSetpoint", mSparkOutput.getSetpoint());
    }

    public double getRumbleLength() {
        return mRumbleLength;
    }

    public void decreaseRumbleLength() {
        mRumbleLength -= OtherConstants.deltaTime;
    }

    public WheelState getWheelState() {
        return mWheelState;
    }

    public UpDownState getUpDownState() {
        return mUpDownState;
    }

    public Optional<Double> getIntakeWantedPosition() {
        return mIntakeWantedPosition;
    }

    public SparkMaxOutput getSparkOutput() {
        return mSparkOutput;
    }

    public double getTalonOutput() {
        return mTalonOutput;
    }

    public boolean intakeOnTarget() {
        return mIntakeWantedPosition.filter(wantedAngle ->
                (Math.abs(wantedAngle - mRobotState.intakeAngle) < IntakeConstants.kAcceptableAngularError) && (Math.abs(mRobotState.intakeVelocity) < IntakeConstants.kAngularVelocityError)).isPresent();
    }

    @Override
    public String getStatus() {
        return String.format("Intake State: %s\nOutput Control Mode: %s\nSpark Output: %.2f\nUp Down Output: %s", mWheelState, mSparkOutput.getControlType(), mSparkOutput.getSetpoint(), mUpDownState);
    }
}