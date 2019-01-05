package com.palyrobotics.frc2019.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.Gains;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.util.TalonSRXOutput;

public class Arm extends Subsystem {
	private static Arm instance = new Arm("Arm");

	public static Arm getInstance() {
		return instance;
	}

	public static void resetInstance() {
		instance = new Arm("Arm");
	}

	public enum ArmState {
		HOLD, //Keeping the arm position fixeds
		MANUAL_POSITIONING, //Moving the arm with the joystick
		CUSTOM_POSITIONING, //Moving the arm with a control loop
		IDLE //Not moving
	}

	//The variable used in the state machine
	private ArmState mState = ArmState.MANUAL_POSITIONING;

	//Variables used to check if arm is at the top or bottom position
	private boolean isAtFront= true; 
	private boolean isAtBack = false; 
	private boolean isAtTop = false;

	//Used for specifying where to hold/move to
	public double mEncoderWantedPosition = 0.0;
	public double mPotentiometerWantedPosition = 0.0;

	//Used to store the robot state for use in methods other than update()
	private RobotState mRobotState;

	//The subsystem output
	private TalonSRXOutput mOutput = new TalonSRXOutput();

	//Sensor functionality fields
	private boolean isEncoderFunctional = true;
	private boolean isPotentiometerFunctional = false;

	/**
	 * Constructor for Arm, defaults state to idle.
	 *
	 * @param name
	 *            the name of the arm
	 */
	protected Arm(String name) {
		super(name);
		mState = ArmState.IDLE;
	}

	/**
	 * Calibration is checked and the variable for the state machine is set after processing the wanted Arm state. State machine used for movement and
	 * clearing only.
	 *
	 * @param commands
	 *            used to obtain wanted Arm state
	 * @param robotState
	 *            used to obtain joystick input and sensor readings
	 */
	@Override
	public void update(Commands commands, RobotState robotState) {
		//Update for use in handleState()
		mRobotState = robotState;

		mState = commands.wantedArmState;

		if(isEncoderFunctional){
			if (onTargetEncoderPositioning()){
				commands.wantedArmState = ArmState.HOLD;
			}
		} else if (isPotentiometerFunctional){
			if (onTargetPotentiometerPositioning()){
				commands.wantedArmState = ArmState.HOLD; // wack. smack.
			}
		}
		checkFrontBack(mRobotState);

		//Execute update loop based on the current state
		//Does not switch between states, only performs actions
		switch(mState) {
			case HOLD:
				if(mState != commands.wantedArmState) {
					if (isAtFront || isAtBack) {
						mOutput.setPercentOutput(0.0);
					} else {
						if (isEncoderFunctional) {
							mEncoderWantedPosition = mRobotState.armEncoder;
							mOutput.setPosition(mEncoderWantedPosition, Gains.armHold);
						} else if (isPotentiometerFunctional) {
							mEncoderWantedPosition = mRobotState.armPotentiometer;
							mOutput.setPosition(mPotentiometerWantedPosition, Gains.armHold);
						}
					}
				}
				break;
			case MANUAL_POSITIONING:

				double armAngle = mRobotState.armAngle;

				if (commands.disableArmScaling) {
					if (Constants.operatorXBoxController) {
						mOutput.setPercentOutput(Constants.kArmUncalibratedManualPower * mRobotState.operatorXboxControllerInput.getRightY());
					} else {
						mOutput.setPercentOutput(Constants.kArmUncalibratedManualPower * mRobotState.operatorJoystickInput.getY());
					}
				}
				break;
			case CUSTOM_POSITIONING:
				//Control loop
				if(isEncoderFunctional) {
					mOutput.setPosition(mEncoderWantedPosition, Gains.armPosition);
				} else if(isPotentiometerFunctional){
					mOutput.setPosition(mPotentiometerWantedPosition, Gains.armPosition);
				}

				break;
			case IDLE:
				mEncoderWantedPosition = 0;
				mPotentiometerWantedPosition = 0;
				mOutput.setPercentOutput(0.0);
				break;
			default:
				break;
		}
	}


	/**
	 * Checks whether or not the Arm has topped/bottomed out.
	 * Uses both HFX and encoders as redundant checks.
	 *
	 * @param state the robot state, used to obtain encoder values
	 */
	private void checkFrontBack(RobotState state) {
		if(state.armPosition < Constants.kArmFrontPosition) {
			isAtBack = true;
		} else {
			isAtFront = false;
		}
		if(state.armPosition > Constants.kArmFrontPosition) {
			isAtFront = true;
		} else {
			isAtBack = false;
		}
	}

    @Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	public TalonSRXOutput getOutput() {
		return mOutput;
	}

	public double getArmEncoderPosition() {
		return mRobotState.armEncoder;
	}

	public double getArmPotentiometerPosition() { return mRobotState.armPotentiometer; }

	public boolean getIsAtTop() {
		return isAtTop;
	}

	public boolean getIsAtFront() {
		return isAtFront; 
	}

	public boolean getIsAtBack() {
		return isAtBack;
	}


	public boolean onTargetEncoderPositioning(){
		if(mState != ArmState.CUSTOM_POSITIONING){
			return false;
		}
		return (Math.abs(mEncoderWantedPosition - mRobotState.armPosition) < Constants.kArmAcceptableEncoderError &&
				(Math.abs(mRobotState.armVelocity) < Constants.kArmAcceptableVelocityError));
	}

	public boolean onTargetPotentiometerPositioning(){
		if(mState != ArmState.CUSTOM_POSITIONING){
			return false;
		}
		return (Math.abs(mPotentiometerWantedPosition - mRobotState.armPosition) < Constants.kArmAcceptablePotentiometerError &&
				(Math.abs(mRobotState.armVelocity) < Constants.kArmAcceptableVelocityError));
	}

	public ArmState getState() {
		return mState;
	}


}