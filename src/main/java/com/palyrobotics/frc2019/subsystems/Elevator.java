package com.palyrobotics.frc2019.subsystems;


import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.ElevatorConfig;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.configv2.Configs;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Elevator extends Subsystem {

    private static Elevator sInstance = new Elevator();

    public static Elevator getInstance() {
        return sInstance;
    }

    private ElevatorConfig mConfig = Configs.get(ElevatorConfig.class);

    public enum ElevatorState {
        MANUAL_POSITIONING,
        CUSTOM_POSITIONING,
        PERCENT_OUTPUT,
        IDLE
    }

    private ElevatorState mElevatorState;

    private Double mElevatorWantedPosition;

    private RobotState mRobotState;

    private SparkMaxOutput mOutput;

    private long mLastTimeWhenInClosedLoopMs;

    private Elevator() {
        super("elevator");
    }

    @Override
    public void reset() {
        mElevatorState = ElevatorState.IDLE;
        mElevatorWantedPosition = null;
        mOutput = SparkMaxOutput.getIdle();
    }

    /**
     * Calibration is checked and the variable for the state machine is set after processing the wanted elevator state. State machine used for movement and
     * clearing {@link #mElevatorWantedPosition} only.
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
            case MANUAL_POSITIONING:
                mOutput.setPercentOutput(mConfig.manualMaxPercentOut * (OtherConstants.operatorXBoxController
                        ? mRobotState.operatorXboxControllerInput.getRightY()
                        : mRobotState.operatorJoystickInput.getY()));
                break;
            case CUSTOM_POSITIONING:
                long currentTimeMs = System.currentTimeMillis();
                boolean inClosedLoopZone = mRobotState.elevatorPosition >= mConfig.closedLoopZoneHeight,
                        wantedPositionInClosedLoopZone = mElevatorWantedPosition >= mConfig.closedLoopZoneHeight,
                        useClosedLoopOutOfRange = currentTimeMs - mLastTimeWhenInClosedLoopMs < mConfig.outOfClosedLoopZoneIdleDelayMs;
                if (inClosedLoopZone) mLastTimeWhenInClosedLoopMs = currentTimeMs;
                if (inClosedLoopZone || wantedPositionInClosedLoopZone || useClosedLoopOutOfRange) {
                    mOutput.setTargetPositionSmartMotion(mElevatorWantedPosition, mConfig.feedForward);
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

        CSVWriter.addData("elevatorAppliedOutput", HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.getAppliedOutput());
        CSVWriter.addData("elevatorPositionInch", mRobotState.elevatorPosition);
        CSVWriter.addData("elevatorVelInchPerSec", mRobotState.elevatorVelocity);
        CSVWriter.addData("elevatorWantedPos", mElevatorWantedPosition);
        CSVWriter.addData("elevatorSetPointInch", mOutput.getReference());
    }

    /**
     * Process wanted elevator state and joystick inputs into mElevatorState for the state machine. Sets {@link #mElevatorWantedPosition} for use in the state
     * machine. Does not clear it. At the end, always check if any custom positioning has finished, and if so, set the state to hold. <br>
     * <br>
     *
     * <b>Teleop joystick movement overrides everything else!</b> <br>
     * <br>
     * <p>
     * Behavior for desired states:
     * <ul>
     * <li>{@link ElevatorState#MANUAL_POSITIONING}: Sets the state to manual.</li>
     * <li>{@link ElevatorState#CUSTOM_POSITIONING}: Sets the desired custom position and state to custom positioning. If not calibrated, set to calibrate instead.</li>
     * <li>{@link ElevatorState#IDLE}: Sets to idle.</li>
     * </ul>
     *
     * @param commands the commands used to get the wanted state
     */
    private void handleElevatorState(Commands commands) {
        switch (commands.wantedElevatorState) {
            case CUSTOM_POSITIONING:
                mElevatorWantedPosition = commands.robotSetPoints.elevatorPositionSetpoint;
                mElevatorState = ElevatorState.CUSTOM_POSITIONING;
                break;
            default:
                mElevatorWantedPosition = null;
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
                && Math.abs(mElevatorWantedPosition - mRobotState.elevatorPosition) < mConfig.acceptablePositionError
                && Math.abs(mRobotState.elevatorVelocity) < mConfig.acceptableVelocityError;
    }

    public SparkMaxOutput getOutput() {
        return mOutput;
    }

    public DoubleSolenoid.Value getSolenoidOutput() {
        return DoubleSolenoid.Value.kForward;
    }
}