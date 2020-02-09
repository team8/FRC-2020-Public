package com.palyrobotics.frc2020.subsystems;

import java.util.ArrayList;

import com.palyrobotics.frc2020.config.subsystem.LightingConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.controllers.lighting.ConvergingBandsController;
import com.palyrobotics.frc2020.subsystems.controllers.lighting.FlashingLightsController;
import com.palyrobotics.frc2020.subsystems.controllers.lighting.InitSequenceController;
import com.palyrobotics.frc2020.subsystems.controllers.lighting.OneColorController;
import com.palyrobotics.frc2020.util.Color;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.LightingOutputs;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class Lighting extends SubsystemBase {

	public enum State {
		IDLE, OFF, INIT, DISABLE, TARGET_FOUND, INDEXER_COUNT, SHOOTER_FULLRPM, CLIMB_EXTENDED, HOPPER_OPEN, INTAKE_EXTENDED
	}

	private static Lighting sInstance = new Lighting();
	private LightingConfig mConfig = Configs.get(LightingConfig.class);
	private AddressableLEDBuffer mOutputBuffer = new AddressableLEDBuffer(mConfig.ledCount);
	private State mState = State.IDLE;
	private ArrayList<LEDController> mLEDControllers = new ArrayList<>();

	public abstract static class LEDController {

		protected int mInitIndex;
		protected int mLastIndex;
		protected LightingOutputs mOutputs = new LightingOutputs();

		public final LightingOutputs update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
			updateSignal(commands, state);
			return mOutputs;
		}

		public abstract void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state);
	}

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
			mLEDControllers.clear();
			switch (mState) {
				case OFF:
					resetLedStrip();
					break;
				case IDLE:
					mLEDControllers.add(new OneColorController(mConfig.totalSegmentFirstIndex,
							mConfig.totalSegmentBackIndex, Color.HSV.kWhite));
					break;
				case INIT:
					resetLedStrip();
					mLEDControllers.add(
							new InitSequenceController(mConfig.backSegmentFirstIndex, mConfig.backSegmentBackIndex));
					mLEDControllers.add(new ConvergingBandsController(mConfig.limelightSegmentFirstIndex,
							mConfig.limelightSegmentBackIndex, Color.HSV.kWhite,
							Color.HSV.kBlue, 3));
					break;
				case DISABLE:
//					mLEDControllers.add(new DisabledSequenceController(mConfig.backSegmentFirstIndex,
//							mConfig.backSegmentBackIndex));
					break;
				case TARGET_FOUND:
					mLEDControllers.add(new FlashingLightsController(mConfig.limelightSegmentFirstIndex,
							mConfig.limelightSegmentBackIndex, Color.HSV.kLime, 3));
					break;
				case INDEXER_COUNT:
				case HOPPER_OPEN:
				case CLIMB_EXTENDED:
				case INTAKE_EXTENDED:
				case SHOOTER_FULLRPM:
					mLEDControllers.add(new FlashingLightsController(mConfig.limelightSegmentFirstIndex,
							mConfig.limelightSegmentBackIndex, Color.HSV.kRed, 3));
					break;
				default:
					break;
			}
		}
		for (LEDController ledController : mLEDControllers) {
			LightingOutputs currentOutput = ledController.update(commands, robotState);
			for (int i = 0; i < currentOutput.lightingOutput.size(); i++) {
				Color.HSV hsvValue = currentOutput.lightingOutput.get(i);
				mOutputBuffer.setHSV(i + ledController.mInitIndex, hsvValue.getH(), hsvValue.getS(), hsvValue.getV());
			}
		}
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
