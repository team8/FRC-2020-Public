package com.palyrobotics.frc2020.subsystems;

import java.util.ArrayList;

import com.palyrobotics.frc2020.config.subsystem.LightingConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.controllers.lighting.*;
import com.palyrobotics.frc2020.util.Color;
import com.palyrobotics.frc2020.util.config.Configs;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.Timer;

public class Lighting extends SubsystemBase {

	public enum State {
		IDLE, OFF, INIT, DISABLE, TARGET_FOUND, SHOOTER_FULLRPM, CLIMB_EXTENDED, HOPPER_OPEN, INTAKE_EXTENDED, BALL_ENTERED
	}

	public abstract static class LEDController {

		private LightingConfig mConfig = Configs.get(LightingConfig.class);
		protected AddressableLEDBuffer mOutputs = new AddressableLEDBuffer(mConfig.ledCount);

		protected Timer mTimer = new Timer();

		protected int mInitIndex;
		protected int mLastIndex;

		public final AddressableLEDBuffer update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
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
	private ArrayList<LEDController> mLEDControllers = new ArrayList<>();
	private ArrayList<LEDController> mToRemove = new ArrayList<>();

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
					break;
				case IDLE:
					addToControllers(new OneColorController(mConfig.totalSegmentFirstIndex,
							mConfig.totalSegmentBackIndex, Color.HSV.kWhite));
					break;
				case INIT:
					resetLedStrip();
					addToControllers(
							new InitSequenceController(mConfig.backSegmentFirstIndex, mConfig.backSegmentBackIndex, 1));
					addToControllers(new ConvergingBandsController(mConfig.limelightSegmentFirstIndex,
							mConfig.limelightSegmentBackIndex, Color.HSV.kWhite,
							Color.HSV.kBlue, 3, 6));
					break;
				case DISABLE:
					addToControllers(new ColorRangingController(mConfig.backSegmentFirstIndex,
							mConfig.backSegmentBackIndex));
					break;
				case TARGET_FOUND:
					addToControllers(new FlashingLightsController(mConfig.limelightSegmentFirstIndex,
							mConfig.limelightSegmentBackIndex, Color.HSV.kLime, 3));
					break;
				case BALL_ENTERED:
				case HOPPER_OPEN:
				case CLIMB_EXTENDED:
				case INTAKE_EXTENDED:
				case SHOOTER_FULLRPM:
					addToControllers(new PulseController(mConfig.limelightSegmentFirstIndex,
							mConfig.limelightSegmentBackIndex, new Color.HSV[] { Color.HSV.kRed }, (double) 1 / 6));
					break;
			}
		}

		for (LEDController ledController : mLEDControllers) {
			if (ledController.checkFinished()) {
				mToRemove.add(ledController);
			} else {
				mOutputBuffer = ledController.update(commands, robotState);
			}
		}
		for (LEDController ledController : mToRemove) {
			mLEDControllers.remove(ledController);
		}
		mToRemove.clear();
	}

	private void addToControllers(LEDController controller) {
		//TODO Can this be moved to update?
		for (var i = mLEDControllers.size() - 1; i >= 0; i--) {
			if (mLEDControllers.get(i).mInitIndex == controller.mInitIndex &&
					mLEDControllers.get(i).mLastIndex == controller.mLastIndex) {
				mLEDControllers.remove(i);
			}
		}
		mLEDControllers.add(controller);
	}

	private void resetLedStrip() {
		mLEDControllers.clear();
		for (int i = 0; i < mOutputBuffer.getLength(); i++) {
			mOutputBuffer.setRGB(i, 0, 0, 0);
		}
	}

	public AddressableLEDBuffer getOutput() {
		return mOutputBuffer;
	}
}
