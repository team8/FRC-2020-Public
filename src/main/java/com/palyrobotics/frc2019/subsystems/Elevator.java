package com.palyrobotics.frc2019.subsystems;


import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.ElevatorConfig;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.configv2.Configs;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class Elevator extends Subsystem {

    private static Elevator instance = new Elevator("Elevator");

    public static Elevator getInstance() {
        return instance;
    }

    private ElevatorConfig mConfig = Configs.get(ElevatorConfig.class);

    public enum ElevatorState {
        MANUAL_POSITIONING, //Moving the elevator with the joystick
        CUSTOM_POSITIONING, //Moving the elevator with a control loop
        PERCENT_OUTPUT,
        IDLE //Not moving
    }

    //The variable used in the state machine
    private ElevatorState mElevatorState;

    //Used for specifying where to hold/move to
    private double mElevatorWantedPosition;

    //Used to store the robot state for use in methods other than update()
    private RobotState mRobotState;

    private SparkMaxOutput mOutput = new SparkMaxOutput(ControlType.kSmartMotion);
    private boolean mHolderSolenoidOutput = false;

    /**
     * Constructor for Elevator, defaults state to idle.
     *
     * @param name the name of the elevator
     */
    protected Elevator(String name) {
        super(name);
        mElevatorState = ElevatorState.IDLE;
    }

    /**
     * Calibration is checked and the variable for the state machine is set after processing the wanted elevator state. State machine used for movement and
     * clearing {@link Elevator#mElevatorWantedPosition} only.
     *
     * @param commands   used to obtain wanted elevator state
     * @param robotState used to obtain joystick input and sensor readings
     */
    @Override
    public void update(Commands commands, RobotState robotState) {
        //Update for use in handleElevatorState()
        mRobotState = robotState;
        mHolderSolenoidOutput = commands.holderOutput;

        handleElevatorState(commands);

        //Execute update loop based on the current state
        //Does not switch between states, only performs actions
        switch (mElevatorState) {
            case MANUAL_POSITIONING:
                //Clear any existing wanted positions
                if (OtherConstants.operatorXBoxController) {
                    mOutput.setPercentOutput(mConfig.manualMaxPercentOut * mRobotState.operatorXboxControllerInput.getRightY());
                } else {
                    mOutput.setPercentOutput(mConfig.manualMaxPercentOut * mRobotState.operatorJoystickInput.getY());
                }
                break;
            case CUSTOM_POSITIONING:
                //Control loop
                mOutput.setTargetPositionSmartMotion(mElevatorWantedPosition, ElevatorConfig.kElevatorInchPerRevolution, mConfig.ff);
                break;
            case IDLE:
                //Clear any existing wanted positions
                mOutput.setPercentOutput(0.0);
                break;
            case PERCENT_OUTPUT:
                mOutput.setPercentOutput(commands.customElevatorPercentOutput);
                break;
        }

        CSVWriter.addData("elevatorAppliedOutput", HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.getAppliedOutput());
        CSVWriter.addData("elevatorPositionInch", mRobotState.elevatorPosition);
        CSVWriter.addData("elevatorVelInchPerSec", mRobotState.elevatorVelocity);
        CSVWriter.addData("elevatorWantedPos", mElevatorWantedPosition);
        CSVWriter.addData("elevatorSetpointInch", mOutput.getSetpoint());
    }

    /**
     * Process wanted elevator state and joystick inputs into mElevatorState for the state machine. Sets {@link Elevator#mElevatorWantedPosition} for use in the state
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
                mElevatorWantedPosition = commands.robotSetpoints.elevatorPositionSetpoint.orElseThrow();
                mElevatorState = ElevatorState.CUSTOM_POSITIONING;
                break;
            default:
                //For idle/manual positioning, just set it
                mElevatorState = commands.wantedElevatorState;
                break;
        }
    }

    @Override
    public void start() {
        resetWantedPosition();
    }

    @Override
    public void stop() {
        resetWantedPosition();
    }

    public SparkMaxOutput getOutput() {
        return mOutput;
    }

    public DoubleSolenoid.Value getSolenoidOutput() {
        return DoubleSolenoid.Value.kForward;
    }

    public boolean getHolderSolenoidOutput() {
        return mHolderSolenoidOutput;
    }

    /**
     * If the elevator is on target. Only for {@link ElevatorState#CUSTOM_POSITIONING}.
     *
     * @return If {@link Elevator#mElevatorState} is not {@link ElevatorState#CUSTOM_POSITIONING},
     * or whether it's within position and velocity tolerances otherwise
     */
    public boolean elevatorOnTarget() {
        return mElevatorState == ElevatorState.CUSTOM_POSITIONING
                && Math.abs(mElevatorWantedPosition - mRobotState.elevatorPosition) < mConfig.acceptablePositionError
                && Math.abs(mRobotState.elevatorVelocity) < mConfig.acceptableVelocityError;
    }

    public void resetWantedPosition() {
        mElevatorWantedPosition = 0.0;
    }
}