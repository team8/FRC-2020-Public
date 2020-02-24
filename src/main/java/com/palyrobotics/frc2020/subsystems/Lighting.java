package com.palyrobotics.frc2020.subsystems;

import java.util.ArrayList;
import java.util.List;

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
		OFF, IDLE, INIT, DISABLE, TARGET_FOUND, SHOOTER_FULLRPM, ROBOT_ALIGNING, CLIMB_EXTENDED, HOPPER_OPEN, INTAKE_EXTENDED, BALL_ENTERED, SPINNER_DONE
	}

	public abstract static class LEDController {

		protected static final double kZeroSpeed = 1e-4;

		protected LightingOutputs mOutputs = new LightingOutputs();
		protected Timer mTimer = new Timer();

		protected int mStartIndex;
		protected int mLastIndex;
		protected double mSpeed;

		protected LEDController(int startIndex, int lastIndex) {
			for (var i = startIndex; i <= lastIndex; i++) {
				mOutputs.lightingOutput.add(new Color.HSV());
			}
		}

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
	private List<LEDController> mLEDControllers = new ArrayList<>(); //array of active led controllers

	private Lighting() {
	}

	public static Lighting getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
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
					addToControllers(new OneColorController(mConfig.totalSegmentFirstIndex, mConfig.totalSegmentLastIndex, Color.HSV.kLime));
					break;
				case DISABLE:
					addToControllers(new ColorRangingController(mConfig.totalSegmentFirstIndex,
							mConfig.totalSegmentLastIndex));
					break;
				case TARGET_FOUND:
					addToControllers(new FlashingLightsController(mConfig.spinnerSegmentFirstIndex,
							mConfig.spinnerSegmentLastIndex, Color.HSV.kLime, 2));
					break;
				case SPINNER_DONE:
					addToControllers(new OneColorController(mConfig.frontLeftSegmentFirstIndex, mConfig.frontRightSegmentLastIndex, Color.HSV.kBlue, 2));
					break;
				case BALL_ENTERED:
					addToControllers(new FlashingLightsController(mConfig.totalSegmentFirstIndex,
							mConfig.totalSegmentLastIndex, new Color.HSV(30, 150, 150), 1, 3));
					break;
				case HOPPER_OPEN:
					addToControllers(new FlashingLightsController(mConfig.totalSegmentFirstIndex,
							mConfig.totalSegmentLastIndex, Color.HSV.kPurple, 1, 3));
					break;
				case CLIMB_EXTENDED:
					addToControllers(new FlashingLightsController(0,
							34, Color.HSV.kRed, 1, 3));
					break;
				case INTAKE_EXTENDED:
				case SHOOTER_FULLRPM:
					addToControllers(new OneColorController(mConfig.spinnerSegmentFirstIndex, mConfig.spinnerSegmentLastIndex, Color.HSV.kLime, 5));
					break;
				case ROBOT_ALIGNING:
					addToControllers(new FlashingLightsController(mConfig.spinnerSegmentFirstIndex, mConfig.spinnerSegmentLastIndex, Color.HSV.kLime, 1));
					break;
			}
		}

		resetLedStrip();
		mLEDControllers.removeIf(LEDController::checkFinished);

		for (LEDController ledController : mLEDControllers) {
			LightingOutputs controllerOutput = ledController.update(commands, state);
			for (int i = 0; i < controllerOutput.lightingOutput.size(); i++) {
				Color.HSV hsvValue = controllerOutput.lightingOutput.get(i);
				mOutputBuffer.setHSV(i + ledController.mStartIndex, hsvValue.getH(), hsvValue.getS(), hsvValue.getV());
			}
		}
	}

	private void addToControllers(LEDController controller) {
		mLEDControllers.removeIf(controllers -> controllers.mStartIndex == controller.mStartIndex &&
				controllers.mLastIndex == controller.mLastIndex);
		mLEDControllers.add(controller);
	}

	private void resetLedStrip() {
		for (int i = 0; i < mOutputBuffer.getLength(); i++) {
			mOutputBuffer.setRGB(i, 0, 0, 0);
		}
	}

	public AddressableLEDBuffer getOutput() {
		return mOutputBuffer;
	}
}
