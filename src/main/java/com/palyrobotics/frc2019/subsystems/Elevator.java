package com.palyrobotics.frc2019.subsystems;


import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.subsystem.ElevatorConfig;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.config.Configs;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;

public class Elevator extends Subsystem {

    private static Elevator sInstance = new Elevator();

    public static Elevator getInstance() {
        return sInstance;
    }

    private ElevatorConfig mConfig = Configs.get(ElevatorConfig.class);

    public enum ElevatorState {
        MANUAL_VELOCITY,
        CUSTOM_POSITIONING,
        PERCENT_OUTPUT,
        IDLE
    }

    private ElevatorState mElevatorState;

    private Double mWantedPosition, mWantedVelocity;

    private RobotState mRobotState;

    private SparkMaxOutput mOutput;

    private double mLastTimeWhenInClosedLoopMs;

    private Elevator() {
        super("elevator");
    }

    @Override
    public void reset() {
        mElevatorState = ElevatorState.IDLE;
        mWantedPosition = null;
        mWantedVelocity = null;
        mOutput = new SparkMaxOutput();
    }

    /**
     * Calibration is checked and the variable for the state machine is set after processing the wanted elevator state. State machine used for movement and
     * clearing {@link #mWantedPosition} only.
     *
     * @param commands   used to obtain wanted elevator state
     * @param robotState used to obtain joystick input and sensor readings
     */
    @Override
    public void update(Commands commands, RobotState robotState) {
        mRobotState = robotState;

        handleElevatorState(commands);

        // Execute update loop based on the current state
        // Does not switch between states, only performs actions
        switch (mElevatorState) {
            case MANUAL_VELOCITY:
                mOutput.setTargetSmartVelocity(mWantedVelocity, mConfig.feedForward, mConfig.gains);
//                CSVWriter.addData("elevatorWantedVel", mRobotState.elevatorAppliedOutput);
                break;
            case CUSTOM_POSITIONING:
                double currentTime = Timer.getFPGATimestamp();
                boolean inClosedLoopZone = mRobotState.elevatorPosition >= mConfig.closedLoopZoneHeight,
                        wantedPositionInClosedLoopZone = mWantedPosition >= mConfig.closedLoopZoneHeight,
                        useClosedLoopOutOfRange = currentTime - mLastTimeWhenInClosedLoopMs < mConfig.outOfClosedLoopZoneIdleDelayMs;
                if (inClosedLoopZone) mLastTimeWhenInClosedLoopMs = currentTime;
                if (inClosedLoopZone || wantedPositionInClosedLoopZone || useClosedLoopOutOfRange) {
                    mOutput.setTargetPositionSmartMotion(mWantedPosition, mConfig.feedForward, mConfig.gains);
                } else {
                    mOutput.setIdle();
                }
                break;
            case IDLE:
                mOutput.setIdle();
                break;
            case PERCENT_OUTPUT:
                mOutput.setPercentOutput(commands.customElevatorPercentOutput);
                break;
        }

//        CSVWriter.addData("elevatorAppliedOut", mRobotState.elevatorAppliedOutput);
//        CSVWriter.addData("elevatorPositionInch", mRobotState.elevatorPosition);
//        CSVWriter.addData("elevatorVelInchPerSec", mRobotState.elevatorVelocity);
//        CSVWriter.addData("elevatorWantedPos", mWantedPosition);
//        CSVWriter.addData("elevatorSetPointInch", mOutput.getReference());
    }

    /**
     * Process wanted elevator state and joystick inputs into mElevatorState for the state machine. Sets {@link #mWantedPosition} for use in the state
     * machine. Does not clear it. At the end, always check if any custom positioning has finished, and if so, set the state to hold. <br>
     * <br>
     *
     * <b>Teleop joystick movement overrides everything else!</b> <br>
     * <br>
     * <p>
     * Behavior for desired states:
     * <ul>
     * <li>{@link ElevatorState#MANUAL_VELOCITY}: Sets the state to manual.</li>
     * <li>{@link ElevatorState#CUSTOM_POSITIONING}: Sets the desired custom position and state to custom positioning. If not calibrated, set to calibrate instead.</li>
     * <li>{@link ElevatorState#IDLE}: Sets to idle.</li>
     * </ul>
     *
     * @param commands the commands used to get the wanted state
     */
    private void handleElevatorState(Commands commands) {
        switch (commands.wantedElevatorState) {
            case CUSTOM_POSITIONING:
                mWantedPosition = commands.robotSetPoints.elevatorPositionSetPoint;
                mElevatorState = ElevatorState.CUSTOM_POSITIONING;
                break;
            case MANUAL_VELOCITY:
                mWantedVelocity = commands.customElevatorVelocity;
                mElevatorState = ElevatorState.MANUAL_VELOCITY;
                break;
            default:
                mWantedPosition = null;
                mElevatorState = commands.wantedElevatorState;
                break;
        }
    }

    /**
     * If the elevator is on target. Only for {@link ElevatorState#CUSTOM_POSITIONING}.
     *
     * @return If {@link #mElevatorState} is not {@link ElevatorState#CUSTOM_POSITIONING},
     * or whether it's within position and velocity tolerances otherwise
     */
    public boolean elevatorOnTarget() {
        return mElevatorState == ElevatorState.CUSTOM_POSITIONING
                && Math.abs(mWantedPosition - mRobotState.elevatorPosition) < mConfig.acceptablePositionError
                && Math.abs(mRobotState.elevatorVelocity) < mConfig.acceptableVelocityError;
    }

    public SparkMaxOutput getOutput() {
        return mOutput;
    }

    public DoubleSolenoid.Value getSolenoidOutput() {
        return DoubleSolenoid.Value.kForward;
    }
}