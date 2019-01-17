package com.palyrobotics.frc2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import java.util.Optional;

public class Elevator extends Subsystem {
    private static Elevator instance = new Elevator("Elevator");

    public static Elevator getInstance() {
        return instance;
    }

    public static void resetInstance() {
        instance = new Elevator("Elevator");
    }

    public enum ElevatorState {
        CALIBRATING, //Moving to the bottom to trigger HFX
        HOLD, //Keeping the elevator position fixed
        MANUAL_POSITIONING, //Moving the elevator with the joystick
        CUSTOM_POSITIONING, //Moving the elevator with a control loop
        INACTIVE, //Used when the climber is being used
        IDLE //Not moving
    }

    public enum ClimberState {
        HOLD, //Keeping the climber position fixed
        MANUAL_POSITIONING, //Moving the climber with the joystick
        CUSTOM_POSITIONING, //Moving the climber with a control loop
        INACTIVE, //Used when the elevator is being used
        IDLE //Not moving
    }

    public enum GearboxState {
        ELEVATOR,
        CLIMBER
    }

    //The variable used in the state machine
    private ElevatorState mElevatorState;
    private ClimberState mClimberState;
    private GearboxState mGearboxState;

    //Values for the bottom/top positions to be calibrated
    private Optional<Double> kElevatorBottomPosition = Optional.empty();
    private Optional<Double> kElevatorTopPosition = Optional.empty();

    //Variables used to check if elevator is at the top or bottom position
    private boolean isAtTop = false;
    private boolean isAtBottom = true;

    private boolean movingDown = false;

    //Used for specifying where to hold/move to
    private Optional<Double> mElevatorWantedPosition = Optional.empty();
    private Optional<Double> mClimberWantedPosition = Optional.empty();

    //Used to store the robot state for use in methods other than update()
    private RobotState mRobotState;

    //The subsystem output
    private TalonSRXOutput mOutput = new TalonSRXOutput();
    private DoubleSolenoid.Value mSolenoidOutput;

    private CSVWriter mWriter = CSVWriter.getInstance();


    /**
     * Constructor for Elevator, defaults state to calibrating.
     *
     * @param name
     *            the name of the elevator
     */
    protected Elevator(String name) {
        super(name);
        mElevatorState = ElevatorState.CALIBRATING;
        mClimberState = ClimberState.INACTIVE;
        mGearboxState = GearboxState.ELEVATOR;
    }

    /**
     * Calibration is checked and the variable for the state machine is set after processing the wanted elevator state. State machine used for movement and
     * clearing {@link Elevator#mElevatorWantedPosition} only.
     *
     * @param commands
     *            used to obtain wanted elevator state
     * @param robotState
     *            used to obtain joystick input and sensor readings
     */
    @Override
    public void update(Commands commands, RobotState robotState) {
        //Update for use in handleElevatorState()
        mRobotState = robotState;

        if(mGearboxState == GearboxState.ELEVATOR) {
            mClimberState = ClimberState.INACTIVE;
            mSolenoidOutput = DoubleSolenoid.Value.kReverse;
            //Checks calibration if not calibrated and not in custom/hold state (checks in manual, idle, calibrating)
            //Exception: in hold but bottomed out, so applying 0 power anyway
            if (mElevatorState != ElevatorState.CUSTOM_POSITIONING && (mElevatorState != ElevatorState.HOLD
                    || (mElevatorState == ElevatorState.HOLD && mRobotState.elevatorHFX)) && !isCalibrated()) {
                checkCalibration();
            }

            handleElevatorState(commands);
            checkTopBottom(mRobotState);

            //Execute update loop based on the current state
            //Does not switch between states, only performs actions
            switch (mElevatorState) {
                //Actual calibration logic is not done in the state machine
                case CALIBRATING:
                    mOutput.setPercentOutput(Constants.kCalibratePower);
                    break;
                case HOLD:
                    //If at the bottom, supply no power
                    if (isAtBottom) {
                        mOutput.setPercentOutput(0.0);
                    } else {
                        //Control loop to hold position otherwise
                        mOutput.setPosition(mElevatorWantedPosition.get(), Gains.elevatorHold);
                    }
                    break;
                case MANUAL_POSITIONING:

                    //Clear any existing wanted positions
                    if (mElevatorWantedPosition.isPresent()) {
                        mElevatorWantedPosition = Optional.empty();
                    }

                    //If calibrated, run limiting code for top & bottom

                    //if not calibrated, limit speed
                    if (Constants.operatorXBoxController) {
                        mOutput.setPercentOutput(Constants.kElevatorUncalibratedManualPower * mRobotState.operatorXboxControllerInput.getRightY());
                    } else {
                        mOutput.setPercentOutput(Constants.kElevatorUncalibratedManualPower * mRobotState.operatorJoystickInput.getY());
                    }

                    break;
                case CUSTOM_POSITIONING:
                    //Control loop
                    if (movingDown) {
                        mOutput.setPosition(mElevatorWantedPosition.get(), Gains.elevatorDownwardsPosition);
                    } else {
                        mOutput.setPosition(mElevatorWantedPosition.get(), Gains.elevatorPosition);
                    }

                    break;
                case IDLE:
                    //Clear any existing wanted positions
                    if (mElevatorWantedPosition.isPresent()) {
                        mElevatorWantedPosition = Optional.empty();
                    }

                    mOutput.setPercentOutput(0.0);
                    break;
                case INACTIVE:
                    if(mElevatorWantedPosition.isPresent()) {
                        mElevatorWantedPosition = Optional.empty();
                    }
                default:
                    break;
            }
        } else { // Climber
            mElevatorState = ElevatorState.INACTIVE;
            mSolenoidOutput = DoubleSolenoid.Value.kForward;

            handleClimberState(commands);

            switch(mClimberState) {
                case HOLD:

                    mOutput.setPosition(mClimberWantedPosition.get(), Gains.climberHold);
                    break;
                case MANUAL_POSITIONING:

                    if(mClimberWantedPosition.isPresent()) {
                        mClimberWantedPosition = Optional.empty();
                    }

                    mOutput.setPercentOutput(mRobotState.operatorXboxControllerInput.getRightY());

                    break;
                case CUSTOM_POSITIONING:

                    mOutput.setPosition(mClimberWantedPosition.get(), Gains.climberPosition);

                    break;
                case IDLE:

                    if(mClimberWantedPosition.isPresent()) {
                        mClimberWantedPosition = Optional.empty();
                    }
                    mOutput.setPercentOutput(0);

                    break;
                case INACTIVE:

                    if(mClimberWantedPosition.isPresent()) {
                        mClimberWantedPosition = Optional.empty();
                    }

                    break;
                default:
                    break;
            }
        }
        mElevatorWantedPosition.ifPresent(aDouble -> mWriter.addData("elevatorWantedPosition", aDouble));
        mWriter.addData("elevatorSetpoint", mOutput.getSetpoint());
        mWriter.addData("elevatorPosition", mRobotState.elevatorPosition);
        mWriter.addData("elevatorVelocity", mRobotState.elevatorVelocity);
        mClimberWantedPosition.ifPresent(aDouble -> mWriter.addData("climberWantedPosition", aDouble));
        mWriter.addData("climberSetpoint", mOutput.getSetpoint());
    }

    /**
     * Process wanted elevator state and joystick inputs into mElevatorState for the state machine. Sets {@link Elevator#mElevatorWantedPosition} for use in the state
     * machine. Does not clear it. At the end, always check if any custom positioning has finished, and if so, set the state to hold. <br>
     * <br>
     *
     * <b>Teleop joystick movement overrides everything else!</b> <br>
     * <br>
     *
     * Behavior for desired states:
     * <ul>
     * <li>{@link ElevatorState#CALIBRATING}: Sets the state to calibrate. If already calibrated, ignores the request and holds instead.</li>
     * <li>{@link ElevatorState#HOLD}: Sets the desired holding position and state to hold.</li>
     * <li>{@link ElevatorState#MANUAL_POSITIONING}: Sets the state to manual.</li>
     * <li>{@link ElevatorState#CUSTOM_POSITIONING}: Sets the desired custom position and state to custom positioning. If not calibrated, set to calibrate instead.</li>
     * <li>{@link ElevatorState#IDLE}: Sets to idle.</li>
     * </ul>
     *
     *
     *
     * @param commands
     *            the commands used to get the wanted state
     */
    private void handleElevatorState(Commands commands) {
        if(commands.wantedElevatorState == ElevatorState.CALIBRATING) {
            if(!isCalibrated()) {
                mElevatorState = ElevatorState.CALIBRATING;
            }  else {
                commands.wantedElevatorState = ElevatorState.HOLD;
            }
        } else if(commands.wantedElevatorState == ElevatorState.HOLD) {
            //Set the wanted elevator position if not already set, or if switching from a
            //different state
            if(!mElevatorWantedPosition.isPresent() || mElevatorState != commands.wantedElevatorState) {
                mElevatorWantedPosition = Optional.of(mRobotState.elevatorPosition);
            }
            mElevatorState = commands.wantedElevatorState;
        } else if(commands.wantedElevatorState == ElevatorState.CUSTOM_POSITIONING) {
            //If calibrated
            if(isCalibrated()) {
                //Set the setpoint
                //If the desired custom positioning setpoint is different than what currently
                //exists, replace it
                if(!mElevatorWantedPosition.equals(Optional.of(kElevatorBottomPosition.get() + commands.robotSetpoints.elevatorPositionSetpoint.get() * Constants.kElevatorTicksPerInch))) {
                    mElevatorWantedPosition = Optional.of(kElevatorBottomPosition.get() + commands.robotSetpoints.elevatorPositionSetpoint.get() * Constants.kElevatorTicksPerInch);
                    if(mElevatorWantedPosition.get() >= mRobotState.elevatorPosition) {
                        movingDown = false;
                    } else {
                        movingDown = true;
                    }
                }
            } else {
                //Assume bottom position is the bottom
                if(!mElevatorWantedPosition.equals(Optional.of(commands.robotSetpoints.elevatorPositionSetpoint.get() * Constants.kElevatorTicksPerInch))) {
                    mElevatorWantedPosition = Optional.of(commands.robotSetpoints.elevatorPositionSetpoint.get() * Constants.kElevatorTicksPerInch);
                    if(mElevatorWantedPosition.get() >= mRobotState.elevatorPosition) {
                        movingDown = false;
                    } else {
                        movingDown = true;
                    }
                }
            }
            mElevatorState = ElevatorState.CUSTOM_POSITIONING;
        } else {
            //For idle/manual positioning, just set it
            mElevatorState = commands.wantedElevatorState;
        }

        //If custom positioning is finished, hold it
        if(elevatorOnTarget()) {
            //Hold it next cycle
            commands.wantedElevatorState = ElevatorState.HOLD;
        }
    }

    private void handleClimberState(Commands commands) {
        if(commands.wantedClimberState == ClimberState.HOLD) {
            //Set the wanted climberposition if not already set, or if switching from a
            //different state
            if(!mClimberWantedPosition.isPresent() || mClimberState != commands.wantedClimberState) {
                mClimberWantedPosition = Optional.of(mRobotState.elevatorPosition);
            }
            mClimberState = commands.wantedClimberState;
        } else if(commands.wantedClimberState == ClimberState.CUSTOM_POSITIONING) {
            if(!mClimberWantedPosition.equals(Optional.of(commands.robotSetpoints.climberPositionSetpoint.get() * Constants.kClimberTicksPerInch))) {
                mClimberWantedPosition = Optional.of(commands.robotSetpoints.climberPositionSetpoint.get() * Constants.kClimberTicksPerInch);
            }
            mClimberState = ClimberState.CUSTOM_POSITIONING;
        } else {
            //For idle/manual positioning, just set it
            mClimberState = commands.wantedClimberState;
        }

        //If custom positioning is finished, hold it
        if(climberOnTarget()) {
            //Hold it next cycle
            commands.wantedClimberState = ClimberState.HOLD;
        }
    }

    public boolean movingUpwards() {
        //
        if((mOutput.getControlMode() == ControlMode.PercentOutput || mOutput.getControlMode() == ControlMode.Velocity) && mOutput.getSetpoint() > Constants.kElevatorHoldVoltage) {
            return true;
        } else if(mOutput.getControlMode() == ControlMode.MotionMagic || mOutput.getControlMode() == ControlMode.Position) {

            //Check calibration. If not calibrated, assume the worst and return true.
            if(isCalibrated()) {
                //If the desired setpoint is above the top position, assume it's moving up
                if(mOutput.getSetpoint() > getElevatorTopPosition().get()) {
                    return true;
                }
            } else return true;
        } else if(mOutput.getControlMode() == ControlMode.Current) {
            if(mOutput.getSetpoint() == 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Checks whether or not the elevator has topped/bottomed out.
     * Uses both HFX and encoders as redundant checks.
     *
     * @param state the robot state, used to obtain encoder values
     */
    private void checkTopBottom(RobotState state) {
        if(isCalibrated() && state.elevatorPosition > kElevatorTopPosition.get()) {
            isAtTop = true;
        } else {
            isAtTop = false;
        }
        if(state.elevatorHFX || (isCalibrated() && state.elevatorPosition < kElevatorBottomPosition.get())) {
            isAtBottom = true;
        } else {
            isAtBottom = false;
        }
    }
    /**
     * Calibrates the bottom or top position values depending on which HFX is triggered. If the other position is not already set, set that as well.
     */
    private void checkCalibration() {

        if(!isCalibrated()) {
            if(mRobotState.elevatorHFX) {
                kElevatorBottomPosition = Optional.of(mRobotState.elevatorPosition);
                if(!kElevatorTopPosition.isPresent()) {
                    kElevatorTopPosition = Optional.of(mRobotState.elevatorPosition + Constants.kElevatorTopBottomDifferenceInches * Constants.kElevatorTicksPerInch);
                }
            }
        } else {
            if((kElevatorBottomPosition.get() - mRobotState.elevatorPosition) > Constants.kElevatorHFXAcceptableError ||
                    (mRobotState.elevatorPosition - kElevatorTopPosition.get()) > Constants.kElevatorHFXAcceptableError) {
                kElevatorTopPosition = Optional.empty();
                kElevatorBottomPosition = Optional.empty();
            }
        }
    }

    public boolean isCalibrated() {
        return (kElevatorTopPosition.isPresent() && kElevatorBottomPosition.isPresent());
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        mElevatorWantedPosition = Optional.empty();
    }

    public TalonSRXOutput getOutput() {
        return mOutput;
    }

    public DoubleSolenoid.Value getSolenoidOutput() {
        return mSolenoidOutput;
    }

    public Optional<Double> getElevatorBottomPosition() {
        return kElevatorBottomPosition;
    }

    public Optional<Double> getElevatorTopPosition() {
        return kElevatorTopPosition;
    }

    public Optional<Double> getElevatorWantedPosition() {
        return mElevatorWantedPosition;
    }

    public boolean tryingToBottom() {
        if(mElevatorWantedPosition.isPresent()) {
            if(isCalibrated()) {
                return mElevatorWantedPosition.get().equals(kElevatorBottomPosition.get());
            } else return mElevatorWantedPosition.get().equals(0);
        } else return false;
    }

    public boolean nearBottom() {
        if(isCalibrated()) {
            if(Math.abs(mRobotState.elevatorPosition - getElevatorBottomPosition().get()) < 3000) {
                return true;
            } else return false;
        } else return false;
    }

    public boolean getIsAtTop() {
        return isAtTop;
    }

    public boolean getIsAtBottom() {
        return isAtBottom;
    }

    /**
     * If the elevator is on target. Only for {@link ElevatorState#CUSTOM_POSITIONING}.
     *
     * @return
     *         <p>
     *         false if {@link Elevator#mElevatorState} is not {@link ElevatorState#CUSTOM_POSITIONING}, or whether it's within position and velocity tolerances
     *         otherwise
     *         </p>
     */
    public boolean elevatorOnTarget() {
        if(mElevatorState != ElevatorState.CUSTOM_POSITIONING) {
            return false;
        }

        return (Math.abs(mElevatorWantedPosition.get() - mRobotState.elevatorPosition) < Constants.kElevatorAcceptablePositionError)
                && (Math.abs(mRobotState.elevatorVelocity) < Constants.kElevatorAcceptableVelocityError);
    }

    /**
     * If the climber is on target. Only for {@link ClimberState#CUSTOM_POSITIONING}.
     *
     * @return
     *         <p>
     *         false if {@link Elevator#mClimberState} is not {@link ClimberState#CUSTOM_POSITIONING}, or whether it's within position and velocity tolerances
     *         otherwise
     *         </p>
     */
    public boolean climberOnTarget() {
        if(mClimberState != ClimberState.CUSTOM_POSITIONING) {
            return false;
        }

        return (Math.abs(mClimberWantedPosition.get() - mRobotState.elevatorPosition) < Constants.kClimberAcceptablePositionError)
                && (Math.abs(mRobotState.elevatorVelocity) < Constants.kClimberAcceptableVelocityError);
    }

    public ElevatorState getElevatorState() {
        return mElevatorState;
    }

    public ClimberState getClimberState() {
        return mClimberState;
    }

    public GearboxState getmGearboxState() {
        return mGearboxState;
    }

    public void setBottomPosition(Optional<Double> value) {
        kElevatorBottomPosition = value;
    }

    public void setTopPosition(Optional<Double> value) {
        kElevatorTopPosition = value;
    }
}