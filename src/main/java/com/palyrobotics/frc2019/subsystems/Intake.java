package com.palyrobotics.frc2018.subsystems;

import com.palyrobotics.frc2018.config.Commands;
import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.robot.HardwareAdapter;
import com.palyrobotics.frc2018.util.LEDColor;
import com.palyrobotics.frc2018.util.TalonSRXOutput;
import com.palyrobotics.frc2018.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2018.util.logger.LeveledString;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * @author Justin and Jason and Prashanti
 */
public class Intake extends Subsystem {
	public static Intake instance = new Intake();

	public static Intake getInstance() {
		return instance;
	}
	
	public static void resetInstance() { instance = new Intake(); }
	
	private TalonSRXOutput mTalonOutput = new TalonSRXOutput();
	private boolean inOut = false;
	private DoubleSolenoid.Value mUpDownOutput = DoubleSolenoid.Value.kForward;

	public enum WheelState {
		INTAKING, IDLE, EXPELLING, VAULT_EXPELLING, AUTO1, AUTO2
	}


	public enum OpenCloseState {
		OPEN, CLOSED, NEUTRAL
	}

	private WheelState mWheelState = WheelState.IDLE;
	private OpenCloseState mOpenCloseState = OpenCloseState.CLOSED;

	private CSVWriter mWriter = CSVWriter.getInstance();


	protected Intake() {
		super("Intake");
	}

	@Override
	public void start() {
		mWheelState = WheelState.IDLE;
		mOpenCloseState = OpenCloseState.OPEN;
	}

	@Override
	public void stop() {
		mWheelState = WheelState.IDLE;
		mOpenCloseState = OpenCloseState.CLOSED;
	}

	@Override
	public void update(Commands commands, RobotState robotState) {
		mWheelState = commands.wantedIntakingState;
		mOpenCloseState = commands.wantedIntakeOpenCloseState;


		switch(mWheelState) {
			case INTAKING:
				if(commands.customIntakeSpeed) {
					mTalonOutput.setPercentOutput(robotState.operatorXboxControllerInput.leftTrigger);
				} else {
					mTalonOutput.setPercentOutput(Constants.kIntakingMotorVelocity);
				}
				break;
			case IDLE:
				mTalonOutput.setPercentOutput(0);
				break;
			case EXPELLING:
				if(commands.customIntakeSpeed) {
					mTalonOutput.setPercentOutput(-robotState.operatorXboxControllerInput.rightTrigger);
				} else {
					mTalonOutput.setPercentOutput(Constants.kExpellingMotorVelocity);
				}
				break;
			case VAULT_EXPELLING:
				mTalonOutput.setPercentOutput(Constants.kVaultExpellingMotorVelocity);
				break;
			case AUTO1:
				mTalonOutput.setPercentOutput(Constants.kAuto1MotorVelocity);
				break;
			case AUTO2:
				mTalonOutput.setPercentOutput(Constants.kAuto2MotorVelocity);
				break;
		}


		switch(mOpenCloseState) {
			case OPEN:
				inOut = true;
				break;
			case CLOSED:
				inOut = false;
				break;
		}


		if (robotState.hasCube) {
			LEDColor.setColor(LEDColor.Color.GREEN);
		}
		else if (robotState.hasCube == false && mWheelState == WheelState.INTAKING) {
			LEDColor.setColor(LEDColor.Color.ORANGE);
		}
		else if (!robotState.hasCube /*&& intake is down*/) {
			LEDColor.setColor(LEDColor.Color.BLUE);
		} else {
			LEDColor.setColor(LEDColor.Color.ORANGE);
		}

//		mWriter.addData("intakeSetpoint", mTalonOutput.getSetpoint());
//		mWriter.addData("intakeCurrentDraw", HardwareAdapter.getInstance().getIntake().masterTalon.getOutputCurrent()
//				+ HardwareAdapter.getInstance().getIntake().slaveTalon.getOutputCurrent());
	}

	public WheelState getWheelState() {
		return mWheelState;
	}

	public OpenCloseState getOpenCloseState() {
		return mOpenCloseState;
	}

	public TalonSRXOutput getTalonOutput() {
		return mTalonOutput;
	}

	public boolean getOpenCloseOutput() {
		return inOut;
	}

	public DoubleSolenoid.Value getUpDownOutput() {
		return mUpDownOutput;
	}

	@Override
	public String getStatus() {
		return "Intake State: " + mWheelState + "\nOutput Control Mode: " + mTalonOutput.getControlMode() + "\nTalon Output: " + mTalonOutput.getSetpoint()
				+ "\n" + "\nOpen Close Output: " + inOut + "\n" + "\nUp Down Output: " + mUpDownOutput + "\n";
	}
}
