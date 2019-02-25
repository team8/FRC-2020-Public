package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.IntakeConstants;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.util.SparkMaxOutput;

import java.util.Optional;

public class Intake extends Subsystem {
    public static Intake instance = new Intake();

    public static Intake getInstance() {
        return instance;
    }

    public static void resetInstance() { instance = new Intake(); }

    private SparkMaxOutput mSparkOutput = new SparkMaxOutput();
    private double mVictorOutput;
    private double mRumbleLength;

    private boolean movingDown = false;

    private Optional<Double> mIntakeWantedPosition = Optional.empty();
    private RobotState mRobotState;

    private boolean cachedCargoState;

    private enum WheelState {
        INTAKING,
        IDLE,
        EXPELLING,
        DROPPING
    }

    private enum UpDownState {
        HOLD, //Keeping arm position fixed
        CLIMBING,
        MANUAL_POSITIONING, //Moving elevator with joystick
        CUSTOM_POSITIONING, //Moving elevator with a control loop
        IDLE
    }

    public enum IntakeMacroState {
        STOWED, // stowed at the start of the match
        GROUND_INTAKING, // Getting the cargo off the ground
        LIFTING, // lifting the cargo into the intake
        DROPPING, // dropping the cargo into the intkae
        HOLDING_MID, // moving the arm to the mid hold position and keeping it there
        HOLDING_ROCKET,
        EXPELLING_ROCKET,
        EXPELLING_CARGO,
        CLIMBING,
        IDLE
    }

    private WheelState mWheelState;
    private UpDownState mUpDownState;
    private IntakeMacroState mMacroState;

    private double lastIntakeQueueTime = 0;
    private final double requiredMSCancel = 150;

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
        mMacroState = IntakeMacroState.STOWED;
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

//        System.out.println(commands.wantedIntakeState);

        // The intake macro state has eight possible states.  Any state can be transferred to automatically or manually,
        // but some states need to set auxiliary variables, such as the queue times.

        if (commands.wantedIntakeState == IntakeMacroState.HOLDING_MID && this.mMacroState == IntakeMacroState.GROUND_INTAKING) {
            // note: this needs to be nested so that the if/else can be exited
            if (this.lastIntakeQueueTime + this.requiredMSCancel < System.currentTimeMillis()) {
                // move the intake back up from the ground
                this.mMacroState = IntakeMacroState.HOLDING_MID;
            }
        }
        else if (this.mMacroState == IntakeMacroState.GROUND_INTAKING && robotState.hasCargo) {
            this.mMacroState = IntakeMacroState.LIFTING;
            commands.wantedIntakeState = IntakeMacroState.LIFTING;
        }
        else if (commands.wantedIntakeState == IntakeMacroState.GROUND_INTAKING && this.mMacroState != IntakeMacroState.LIFTING) {
            this.mMacroState = IntakeMacroState.GROUND_INTAKING;
            this.lastIntakeQueueTime = System.currentTimeMillis();
        }
        else if (this.mMacroState == IntakeMacroState.LIFTING && intakeOnTarget()) {
            this.mMacroState = IntakeMacroState.DROPPING;
            commands.wantedIntakeState = IntakeMacroState.DROPPING;
            lastDropQueueTme = System.currentTimeMillis();
        }
        else if (commands.wantedIntakeState == IntakeMacroState.DROPPING && robotState.hasPusherCargo) {
            this.mMacroState = IntakeMacroState.HOLDING_MID;
            commands.wantedIntakeState = IntakeMacroState.HOLDING_MID; // reset it
        }
        else if (this.mMacroState != IntakeMacroState.LIFTING && this.mMacroState != IntakeMacroState.DROPPING){
            this.mMacroState = commands.wantedIntakeState;
        }

//        System.out.println(mMacroState);

        if (intakeOnTarget()) {
//            System.out.println("On Target");
            this.mUpDownState = UpDownState.HOLD;
        }

        commands.hasCargo = robotState.hasCargo;

        // FEED FORWARD MODEL:
        // 1. Compensate for gravity on the CM.
        // 2. Compensate for robot acceleration.  Derivation is similar to that for an inverted pendulum,
        // and can be found on slack.
        // 3. Compensate for centripetal acceleration on the arm.
        double arb_ff = IntakeConstants.kGravityFF * Math.cos(Math.toRadians(robotState.intakeAngle))
                + IntakeConstants.kAccelComp * robotState.robotAccel * Math.sin(Math.toRadians(robotState.intakeAngle))
                + IntakeConstants.kCentripetalCoeff * robotState.drivePose.headingVelocity * robotState.drivePose.headingVelocity *
                Math.sin(Math.toRadians(robotState.intakeAngle));

        switch (mMacroState) {
            case STOWED:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(convertIntakeSetpoint(IntakeConstants.kMaxAngle));
                break;
            case GROUND_INTAKING:
                mWheelState = WheelState.INTAKING;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(convertIntakeSetpoint(IntakeConstants.kIntakingPosition));
                break;
            case LIFTING:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(convertIntakeSetpoint(IntakeConstants.kHandoffPosition));
                break;
            case DROPPING:
                mWheelState = WheelState.DROPPING;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(convertIntakeSetpoint(IntakeConstants.kHandoffPosition));
                // todo: add some sort of timeout so this doesn't finish immediately
                break;
            case HOLDING_MID:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(convertIntakeSetpoint(IntakeConstants.kHoldingPosition));
                break;
            case HOLDING_ROCKET:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(convertIntakeSetpoint(IntakeConstants.kRocketExpelPosition));
                break;
            case EXPELLING_ROCKET:
                mWheelState = WheelState.EXPELLING;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                mIntakeWantedPosition = Optional.of(convertIntakeSetpoint(IntakeConstants.kRocketExpelPosition));
                break;
            case CLIMBING:
                mWheelState = WheelState.IDLE;
                mUpDownState = UpDownState.CUSTOM_POSITIONING;
                HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getPIDController().setOutputRange(-1.0,1.0);
                HardwareAdapter.getInstance().getIntake().intakeSlaveSpark.getPIDController().setOutputRange(-1.0,1.0);
                mIntakeWantedPosition = Optional.of(convertIntakeSetpoint(IntakeConstants.kClimbPosition));
        }

//        System.out.println(this.mMacroState);
//        System.out.println(this.mIntakeWantedPosition.isPresent() ? this.mIntakeWantedPosition.get() : -100);


        switch(mWheelState) {
            case INTAKING:
                if(commands.customIntakeSpeed) {
                    mVictorOutput = robotState.operatorXboxControllerInput.leftTrigger;
                } else {
                    mVictorOutput = IntakeConstants.kMotorVelocity;
                }
                break;
            case IDLE:
                mVictorOutput = 0;
                break;
            case DROPPING:
                mVictorOutput = IntakeConstants.kDroppingVelocity;
                break;
            case EXPELLING:
                mVictorOutput = IntakeConstants.kExpellingVelocity;
        }

        switch(mUpDownState) {
            case HOLD:
                mSparkOutput.setGains(Gains.intakeHold);
                mSparkOutput.setTargetPosition(mIntakeWantedPosition.get());
                break;
            case CLIMBING:
                mSparkOutput.setGains(Gains.intakePosition);
                mSparkOutput.setTargetPosition(mIntakeWantedPosition.get());
                //subtract component of gravity
                break;
            case MANUAL_POSITIONING:
                mSparkOutput.setPercentOutput(0); //TODO: Fix this based on what control method wanted
                break;
            case CUSTOM_POSITIONING:
                mSparkOutput.setGains(Gains.intakePosition);
                mSparkOutput.setTargetPosition(mIntakeWantedPosition.get(), arb_ff, Gains.intakePosition);
                System.out.println(mIntakeWantedPosition.get());
                break;
            case IDLE:
                if(mIntakeWantedPosition.isPresent()) {
                    mIntakeWantedPosition = Optional.empty();
                }
                mSparkOutput.setPercentOutput(0.0);
                break;
        }

        if (intakeOnTarget() && mIntakeWantedPosition.isPresent() &&
                mIntakeWantedPosition.get() == IntakeConstants.kMaxAngle) {
            mSparkOutput.setPercentOutput(0.0);
        }

//        System.out.println("Intake: ");
//        System.out.println(mIntakeWantedPosition.get());
//        System.out.println(HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getPosition());

        if(!cachedCargoState && robotState.hasCargo) {
            mRumbleLength = 0.25;
        } else if(mRumbleLength <= 0) {
            mRumbleLength = -1;
        }

        cachedCargoState = robotState.hasCargo;

//        System.out.println("Angle: " + mRobotState.intakeAngle);
//        System.out.println(HardwareAdapter.getInstance().getIntake().potentiometer.get());
//        System.out.println(HardwareAdapter.getInstance().getIntake().potentiometer.get() / IntakeConstants.kArmPotentiometerTicksPerDegree);
        mWriter.addData("intakeAngle", mRobotState.intakeAngle);
        mIntakeWantedPosition.ifPresent(intakeWantedPosition -> mWriter.addData("intakeWantedPosition", intakeWantedPosition));
        mWriter.addData("intakeSparkSetpoint", mSparkOutput.getSetpoint());
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

    public Optional<Double> getIntakeWantedPosition() { return mIntakeWantedPosition; }

    public SparkMaxOutput getSparkOutput() {
        return mSparkOutput;
    }

    public double getVictorOutput() { return mVictorOutput; }

    public boolean intakeOnTarget() {
        if (!mIntakeWantedPosition.isPresent()) {
            return false;
        }

//        System.out.println("Angle Error: " + (mIntakeWantedPosition.get() - HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getPosition()));
//        System.out.println(mIntakeWantedPosition.get());
//        System.out.println("Vel Error: " + Math.abs(mRobotState.elevatorVelocity));
        return (Math.abs(mIntakeWantedPosition.get() - HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getPosition()) < IntakeConstants.kAcceptableAngularError)
                && (Math.abs(mRobotState.intakeVelocity) < IntakeConstants.kAngularVelocityError);
    }

    public double convertIntakeSetpoint(double targetAngle) {
        return -(mRobotState.intakeStartAngle - targetAngle);
    }

    @Override
    public String getStatus() {
        return "Intake State: " + mWheelState + "\nOutput Control Mode: " + mSparkOutput.getControlType() + "\nSpark Output: "
                + mSparkOutput.getSetpoint() + "\nUp Down Output: " + mUpDownState;
    }
}