package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.util.LEDColor;
import com.palyrobotics.frc2019.util.SparkMaxOutput;
import com.palyrobotics.frc2019.util.TalonSRXOutput;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2019.util.logger.LeveledString;
import edu.wpi.first.wpilibj.DoubleSolenoid;

import java.util.Optional;

/**
 * @author Justin and Jason
 */
public class Intake extends Subsystem {
    public static Intake instance = new Intake();

    public static Intake getInstance() {
        return instance;
    }

    public static void resetInstance() { instance = new Intake(); }

    private SparkMaxOutput mSparkOutput = new SparkMaxOutput();
    private double mVictorOutput;

    private boolean movingDown = false;
    private double arb_ff = 0;

    private Optional<Double> mIntakeWantedPosition = Optional.empty();
    public enum WheelState {
        INTAKING,
        IDLE,
        SLOW_EXPELLING,
        FAST_EXPELLING
    }

    public enum UpDownState {
        HOLD, //Keeping arm position fixed
        UP,
        CLIMBING,
        MANUAL_POSITIONING, //Moving elevator with joystick
        CUSTOM_POSITIONING, //Moving elevator with a control loop
        IDLE
    }

    private WheelState mWheelState = WheelState.IDLE;
    private UpDownState mUpDownState = UpDownState.UP;

    protected Intake() {
        super("Intake");
        mWheelState = WheelState.IDLE;
        mUpDownState = UpDownState.UP;
    }

    @Override
    public void start() {
        mWheelState = WheelState.IDLE;
        mUpDownState = UpDownState.UP;
    }

    @Override
    public void stop() {
        mWheelState = WheelState.IDLE;
        mUpDownState = UpDownState.UP;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mWheelState = commands.wantedIntakingState;
        mUpDownState = commands.wantedIntakeUpDownState;
        commands.hasCargo = robotState.hasCargo;
        arb_ff = Constants.kIntakeArbitraryFeedForward * Math.cos(robotState.intakePosition);

        switch(mWheelState) {
            case INTAKING:
                if(commands.customIntakeSpeed) {
                    mVictorOutput = robotState.operatorXboxControllerInput.leftTrigger;
                } else {
                    mVictorOutput = Constants.kIntakeSlowIntakingVelocity;
                }
            case IDLE:
                mVictorOutput = 0;
                break;
            case SLOW_EXPELLING:
                mVictorOutput = Constants.kIntakeSlowExpellingVelocity;
                break;
            case FAST_EXPELLING:
                mVictorOutput = Constants.kIntakeFastExpellingVelocity;
                break;
        }

        switch(mUpDownState) {
            case HOLD:
                mSparkOutput.setGains(Gains.intakeHold);
                mSparkOutput.setTargetPosition(mIntakeWantedPosition.get());
                break;
            case UP:
                mSparkOutput.setGains(Gains.intakeUp);
                mSparkOutput.setTargetPosition(mIntakeWantedPosition.get(), -arb_ff);
                break;
            case CLIMBING:
                mSparkOutput.setGains(Gains.intakeClimbing);
                mSparkOutput.setTargetPosition(mIntakeWantedPosition.get(), arb_ff);
                //subtract component of gravity
                break;
            case MANUAL_POSITIONING:
                mSparkOutput.setPercentOutput(0); //TODO: Fix this based on what control method wanted
                break;
            case CUSTOM_POSITIONING:
                if(!mIntakeWantedPosition.equals(Optional.of(commands.robotSetpoints.intakePositionSetpoint.get() * Constants.kIntakeTicksPerInch))) {
                    mIntakeWantedPosition = Optional.of(commands.robotSetpoints.intakePositionSetpoint.get() * Constants.kIntakeTicksPerInch);
                    if(mIntakeWantedPosition.get() >= robotState.intakePosition) {
                        movingDown = false;
                    } else {
                        movingDown = true;
                    }
                }

                if (movingDown) {
                    mSparkOutput.setGains(Gains.intakeDownwards);
                    mSparkOutput.setTargetPosition(mIntakeWantedPosition.get(), arb_ff);
                } else {
                    mSparkOutput.setGains(Gains.intakePosition);
                    mSparkOutput.setTargetPosition(mIntakeWantedPosition.get(), arb_ff);
                }
                break;
            case IDLE:
                if(mIntakeWantedPosition.isPresent()) {
                    mIntakeWantedPosition = Optional.empty();
                }
                mSparkOutput.setPercentOutput(0.0);
                break;
        }

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


    @Override
    public String getStatus() {
        return "Intake State: " + mWheelState + "\nOutput Control Mode: " + mSparkOutput.getControlType() + "\nTalon Output: "
                + mSparkOutput.getSetpoint() + "\nUp Down Output: " + mUpDownState;
    }
}