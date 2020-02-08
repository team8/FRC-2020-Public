package com.palyrobotics.frc2020.subsystems;

import java.util.ArrayList;

import com.palyrobotics.frc2020.config.subsystem.LightingConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers.*;
import com.palyrobotics.frc2020.util.Color;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.LightingOutputs;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class Lighting extends SubsystemBase {

	public enum LightingState {
		IDLE, OFF, INIT, DISABLE, TARGET_FOUND, INDEXER_COUNT, SHOOTER_FULLRPM, CLIMB_EXTENDED, HOPPER_OPEN, INTAKE_EXTENDED
	}

	private static Lighting sInstance = new Lighting();
	private LightingConfig mConfig = Configs.get(LightingConfig.class);
	private AddressableLEDBuffer mOutputBuffer = new AddressableLEDBuffer(mConfig.ledCount);
	private Lighting.LightingState mState = LightingState.IDLE;
	private ArrayList<LEDController> mLEDControllers = new ArrayList<>();

	public abstract static class LEDController {

		protected int mInitIndex;
		protected int mLastIndex;
		protected LightingOutputs mLightingOutputs = new LightingOutputs();

		public final LightingOutputs update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
			updateSignal(commands, state);
			return mLightingOutputs;
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
		Lighting.LightingState wantedState = commands.lightingWantedState;
		boolean isNewState = mState != wantedState;
		if (isNewState) {
			mLEDControllers.clear();
			switch (mState) {
				case OFF:
					resetLedStrip();
					break;
				case IDLE:
					mLEDControllers.add(new OneColorController(mConfig.totalSegmentFirstIndex,
							mConfig.totalSegmentBackIndex, Color.HSV.WHITE));
					break;
				case INIT:
					resetLedStrip();
					mLEDControllers.add(
							new InitSequenceController(mConfig.backSegmentFirstIndex, mConfig.backSegmentBackIndex));
					mLEDControllers.add(new ConvergingBandsController(mConfig.limelightSegmentFirstIndex,
							mConfig.limelightSegmentBackIndex, Color.HSV.WHITE,
							Color.HSV.BLUE, 3));
					break;
				case DISABLE:
					mLEDControllers.add(new DisabledSequenceController(mConfig.backSegmentFirstIndex,
							mConfig.backSegmentBackIndex));
					break;
				case TARGET_FOUND:
					mLEDControllers.add(new FlashingLightsController(mConfig.limelightSegmentFirstIndex,
							mConfig.limelightSegmentBackIndex, Color.HSV.LIMELIGHT_GREEN, 3));
					break;
				case INDEXER_COUNT:
				case HOPPER_OPEN:
				case CLIMB_EXTENDED:
				case INTAKE_EXTENDED:
				case SHOOTER_FULLRPM:
					mLEDControllers.add(new FlashingLightsController(mConfig.limelightSegmentFirstIndex,
							mConfig.limelightSegmentBackIndex, Color.HSV.RED, 3));
					break;
				default:
					break;
			}
			mState = wantedState;
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
