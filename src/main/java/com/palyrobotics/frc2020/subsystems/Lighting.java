package com.palyrobotics.frc2020.subsystems;

import java.util.ArrayList;

import com.palyrobotics.frc2020.config.subsystem.LightingConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.controllers.lighting.*;
import com.palyrobotics.frc2020.util.Color;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.LightingOutputs;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;

public class Lighting extends SubsystemBase {

	public enum State {
		IDLE, OFF, INIT, DISABLE, TARGET_FOUND, SHOOTER_FULLRPM, CLIMB_EXTENDED, HOPPER_OPEN, INTAKE_EXTENDED, BALL_ENTERED, SPINNER_DONE
	}

	public abstract static class LEDController {

		protected LightingOutputs mOutputs = new LightingOutputs();

		protected Timer mTimer = new Timer();

		protected int mInitIndex; //indexes that led controller will write to
		protected int mLastIndex;

		public final LightingOutputs update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
			updateSignal(commands, state);
			return mOutputs;
		}

		public abstract void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state);

		public boolean checkFinished() {
			return false;
		}
	}

	private static Lighting sInstance = new Lighting();
	private LightingConfig mConfig = Configs.get(LightingConfig.class);
	private AddressableLEDBuffer mOutputBuffer = new AddressableLEDBuffer(mConfig.ledCount);
	private State mState;
	private ArrayList<LEDController> mLEDControllers = new ArrayList<>(); //array of active led controllers
	private ArrayList<LEDController> mToRemove = new ArrayList<>(); //array of all controller that are finished

	private Lighting() {
	}

	public static Lighting getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		State wantedState = commands.lightingWantedState;
		boolean isNewState = mState != wantedState;
		mState = wantedState;
		if (isNewState) {
			switch (mState) {
				case OFF:
					resetLedStrip();
					mLEDControllers.clear();
					break;
				case IDLE:
					addToControllers(new OneColorController(mConfig.totalSegmentFirstIndex,
							mConfig.totalSegmentLastIndex, Color.HSV.kWhite));
					break;
				case INIT:
					resetLedStrip();
					mLEDControllers.clear();
					addToControllers(
							new InitSequenceController(mConfig.backSupportSegmentFirstIndex, mConfig.backSupportSegmentLastIndex, 1.0 / 25.0));
					addToControllers(new ConvergingBandsController(mConfig.spinnerSegmentFirstIndex,
							mConfig.spinnerSegmentLastIndex, Color.HSV.kWhite,
							Color.HSV.kBlue, 3, 1.0 / 5.0));
					break;
				case DISABLE:
					addToControllers(new ColorRangingController(mConfig.backSupportSegmentFirstIndex,
							mConfig.backSupportSegmentLastIndex));
					break;
				case TARGET_FOUND:
					addToControllers(new FlashingLightsController(mConfig.spinnerSegmentFirstIndex,
							mConfig.spinnerSegmentLastIndex, Color.HSV.kLime, 1));
					break;
				case SPINNER_DONE:
				case BALL_ENTERED:
				case HOPPER_OPEN:
				case CLIMB_EXTENDED:
				case INTAKE_EXTENDED:
				case SHOOTER_FULLRPM:
					addToControllers(new PulseController(mConfig.spinnerSegmentFirstIndex,
							mConfig.spinnerSegmentLastIndex, new Color.HSV[] { Color.HSV.kLime, Color.HSV.kBlue, Color.HSV.kLime, Color.HSV.kBlue }, 1.0));
					break;
			}
		}

		resetLedStrip();

		for (LEDController ledController : mLEDControllers) {
			if (ledController.checkFinished()) {
				mToRemove.add(ledController);
			} else {
				LightingOutputs controllerOutput = ledController.update(commands, robotState);
				for (int i = 0; i < controllerOutput.lightingOutput.size(); i++) {
					Color.HSV hsvValue = controllerOutput.lightingOutput.get(i);
					mOutputBuffer.setHSV(i + ledController.mInitIndex, hsvValue.getH(), hsvValue.getS(), hsvValue.getV());
				}
			}
		}
		for (LEDController ledController : mToRemove) {
			mLEDControllers.remove(ledController);
		}
		mToRemove.clear();
	}

	private void addToControllers(LEDController controller) {
		removeOldControllers(controller);
		mLEDControllers.add(controller);
	}

	private void resetLedStrip() {
		for (int i = 0; i < mOutputBuffer.getLength(); i++) {
			mOutputBuffer.setRGB(i, 0, 0, 0);
		}
	}

	private void removeOldControllers(LEDController controller) {
		for (var i = mLEDControllers.size() - 1; i >= 0; i--) {
			if (mLEDControllers.get(i).mInitIndex == controller.mInitIndex &&
					mLEDControllers.get(i).mLastIndex == controller.mLastIndex) {
				mLEDControllers.remove(i);
			}
		}
	}

	public AddressableLEDBuffer getOutput() {
		return mOutputBuffer;
	}
}
