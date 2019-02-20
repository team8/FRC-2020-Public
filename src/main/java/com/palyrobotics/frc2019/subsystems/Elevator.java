package com.palyrobotics.frc2019.subsystems;


import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.ElevatorConstants;
import com.palyrobotics.frc2019.config.Constants.OtherConstants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.revrobotics.ControlType;
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
        HOLD, //Keeping the elevator position fixed
        MANUAL_POSITIONING, //Moving the elevator with the joystick
        CUSTOM_POSITIONING, //Moving the elevator with a control loop
        INACTIVE, //Used when the climber is being used
        IDLE //Not moving
    }

    public enum ClimberState {
        HOLD, //Keeping the climber position fixed
        ON_MANUAL,
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

    private double kElevatorBottomPosition = ElevatorConstants.kBottomPositionInches;
    private double kElevatorTopPosition = ElevatorConstants.kTopPositionInches;

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
    private SparkMaxOutput mOutput = new SparkMaxOutput();
    private DoubleSolenoid.Value mSolenoidOutput = DoubleSolenoid.Value.kForward;
    private boolean mHolderSolenoidOutput = false;

    /**
     * Constructor for Elevator, defaults state to calibrating.
     *
     * @param name
     *            the name of the elevator
     */
    protected Elevator(String name) {
        super(name);
        mElevatorState = ElevatorState.IDLE;
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
            mHolderSolenoidOutput = commands.holderOutput;

            mClimberState = ClimberState.INACTIVE;
            mSolenoidOutput = DoubleSolenoid.Value.kReverse;

            handleElevatorState(commands);
            checkTopBottom(mRobotState);

            //Execute update loop based on the current state
            //Does not switch between states, only performs actions
            switch (mElevatorState) {
                case HOLD:
                    commands.elevatorMoving = false;
                    //If at the bottom, supply no power
                    if (isAtBottom) {
                        mOutput.setPercentOutput(0.0);
                    } else {
                        //Control loop to hold position otherwise
                        mOutput.setTargetPosition(mElevatorWantedPosition.get(), ElevatorConstants.kHoldVoltage, Gains.elevatorPosition);
                    }
                    break;
                case MANUAL_POSITIONING:
                    //Clear any existing wanted positions
                    if (mElevatorWantedPosition.isPresent()) {
                        mElevatorWantedPosition = Optional.empty();
                    }

                    commands.elevatorMoving = false;

                    if (OtherConstants.operatorXBoxController) {
                        mOutput.setPercentOutput(ElevatorConstants.kUncalibratedManualPower * mRobotState.operatorXboxControllerInput.getRightY());
                    } else {
                        mOutput.setPercentOutput(ElevatorConstants.kUncalibratedManualPower * mRobotState.operatorJoystickInput.getY());
                    }

                    break;
                case CUSTOM_POSITIONING:
                    commands.elevatorMoving = true;
                    //Control loop

                    mOutput.setTargetPosition(mElevatorWantedPosition.get(), ElevatorConstants.kHoldVoltage, Gains.elevatorPosition);

                    break;
                case IDLE:
                    commands.elevatorMoving = false;
                    //Clear any existing wanted positions
                    if (mElevatorWantedPosition.isPresent()) {
                        mElevatorWantedPosition = Optional.empty();
                    }

                    mOutput.setPercentOutput(0.0);
                    break;
                case INACTIVE:
                    commands.elevatorMoving = false;
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

                    mOutput.setTargetPosition(mClimberWantedPosition.get());
                    mOutput.setGains(Gains.climberHold);
                    break;
                case ON_MANUAL:

                    if(mClimberWantedPosition.isPresent()) {
                        mClimberWantedPosition = Optional.empty();
                    }

                    mOutput.setPercentOutput(ElevatorConstants.kManualOutputPercentOutput);

                    break;
                case CUSTOM_POSITIONING:
                    mOutput.setTargetPosition(mClimberWantedPosition.get());
                    mOutput.setGains(Gains.climberPosition);

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

        System.out.println("Elevator: ");
        System.out.println("applied output " + HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.getAppliedOutput());

        mWriter.addData("elevatorPosition", mRobotState.elevatorPosition);
        mWriter.addData("elevatorPositionInches", mRobotState.elevatorPosition / ElevatorConstants.kElevatorRotationsPerInch);
        mWriter.addData("elevatorVelocity", mRobotState.elevatorVelocity);
        mWriter.addData("elevatorVelocityInchPerSec", mRobotState.elevatorVelocity * ElevatorConstants.kElevatorSpeedUnitConversion);
        mElevatorWantedPosition.ifPresent(elevatorWantedPosition -> mWriter.addData("elevatorWantedPosition", elevatorWantedPosition));
        mWriter.addData("elevatorSetpoint", mOutput.getSetpoint());

        mWriter.addData("climberPosition", mRobotState.elevatorPosition);
        mWriter.addData("climberPositionInches", mRobotState.elevatorPosition / ElevatorConstants.kClimberRotationsPerInch);
        mWriter.addData("elevatorVelocity", mRobotState.elevatorVelocity);
        mWriter.addData("elevatorVelocityInchPerSec", mRobotState.elevatorVelocity * ElevatorConstants.kClimberSpeedUnitConversion);
        mClimberWantedPosition.ifPresent(climberWantedPosition -> mWriter.addData("climberWantedPosition", climberWantedPosition));
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
        if(commands.wantedElevatorState == ElevatorState.HOLD) {
            //Set the wanted elevator position if not already set, or if switching from a
            //different state
            if(!mElevatorWantedPosition.isPresent() || mElevatorState != commands.wantedElevatorState) {
                mElevatorWantedPosition = Optional.of(mRobotState.elevatorPosition/ElevatorConstants.kElevatorRotationsPerInch);
            }
            mElevatorState = commands.wantedElevatorState;
        } else if(commands.wantedElevatorState == ElevatorState.CUSTOM_POSITIONING) {
            //Assume bottom position is the bottom
            if(!mElevatorWantedPosition.equals(Optional.of(commands.robotSetpoints.elevatorPositionSetpoint.get()))) {
                mElevatorWantedPosition = Optional.of(commands.robotSetpoints.elevatorPositionSetpoint.get());
                if(mElevatorWantedPosition.get() >= mRobotState.elevatorPosition/ElevatorConstants.kElevatorRotationsPerInch) {
                    movingDown = false;
                } else {
                    movingDown = true;
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
                mClimberWantedPosition = Optional.of(mRobotState.elevatorPosition/ElevatorConstants.kClimberRotationsPerInch);
            }
            mClimberState = commands.wantedClimberState;
        } else if(commands.wantedClimberState == ClimberState.CUSTOM_POSITIONING) {
            if(!mClimberWantedPosition.equals(Optional.of(commands.robotSetpoints.climberPositionSetpoint.get()))) {
                mClimberWantedPosition = Optional.of(commands.robotSetpoints.climberPositionSetpoint.get());
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
        if((mOutput.getControlType() == ControlType.kDutyCycle || mOutput.getControlType() == ControlType.kVelocity) && mOutput.getSetpoint() > ElevatorConstants.kHoldVoltage) {
            return false;
        } else if(mOutput.getControlType() == ControlType.kPosition) {
            if(mOutput.getSetpoint() > kElevatorTopPosition) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether or not the elevator has topped/bottomed out.
     *
     * @param state the robot state, used to obtain encoder values
     */
    private void checkTopBottom(RobotState state) {
        if(state.elevatorPosition/ElevatorConstants.kElevatorRotationsPerInch > kElevatorTopPosition) {
            isAtTop = true;
        } else {
            isAtTop = false;
        }
        if(state.elevatorPosition/ElevatorConstants.kElevatorRotationsPerInch < kElevatorBottomPosition) {
            isAtBottom = true;
        } else {
            isAtBottom = false;
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        mElevatorWantedPosition = Optional.empty();
    }

    public SparkMaxOutput getOutput() {
        return mOutput;
    }

    public DoubleSolenoid.Value getSolenoidOutput() {
        return mSolenoidOutput;
    }

    public boolean getHolderSolenoidOutput() {
        return mHolderSolenoidOutput;
    }

    public Optional<Double> getElevatorWantedPosition() {
        return mElevatorWantedPosition;
    }

    public boolean getIsAtTop() {
        return isAtTop;
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
        System.out.println("Elevator wanted position: " + mElevatorWantedPosition.get());
        System.out.println("Elevator position: " + mRobotState.elevatorPosition/ElevatorConstants.kElevatorRotationsPerInch);
        System.out.println("Elevator velocity: " + mRobotState.elevatorVelocity*ElevatorConstants.kElevatorSpeedUnitConversion);
        System.out.println("");
        return (Math.abs(mElevatorWantedPosition.get() - mRobotState.elevatorPosition/ElevatorConstants.kElevatorRotationsPerInch) < ElevatorConstants.kAcceptablePositionError)
                && (Math.abs(mRobotState.elevatorVelocity*ElevatorConstants.kElevatorSpeedUnitConversion) < ElevatorConstants.kAcceptableVelocityError);
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

        return (Math.abs(mClimberWantedPosition.get() - mRobotState.elevatorPosition/ElevatorConstants.kClimberRotationsPerInch) < ElevatorConstants.kClimberAcceptablePositionError)
                && (Math.abs(mRobotState.elevatorVelocity*ElevatorConstants.kClimberSpeedUnitConversion) < ElevatorConstants.kClimberAcceptableVelocityError);
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
}