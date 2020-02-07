package com.palyrobotics.frc2020.subsystems;

import java.util.ArrayList;

import com.palyrobotics.frc2020.config.subsystem.LightingConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers.ConvergingBandsController;
import com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers.DisabledSequenceController;
import com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers.FlashingLightsController;
import com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers.InitSequenceController;
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
	private Lighting.LightingState mState = LightingState.INIT; // used to compare with new lighting states
	private ArrayList<LEDController> mLEDControllers = new ArrayList<>(3);

	public abstract static class LEDController {

		public int mInitIndex;
		public int mLastIndex;
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
		if (wantedState != mState) {
		    mLEDControllers.clear();
            switch (mState) {
                case OFF:
                case IDLE:
                    resetLedStrip();
                    break;
                case INIT:
                    resetLedStrip();
                    mLEDControllers.add(new InitSequenceController(0, (mConfig.ledCount - 1)));
                    mLEDControllers.add(new ConvergingBandsController(30, 46, 0, 75, 50, 100, 150, 150, 3));
                    break;
                case DISABLE:
                    mLEDControllers.add(new DisabledSequenceController(0, mConfig.ledCount - 1));
                    break;
                case TARGET_FOUND:
                    mLEDControllers.add(new ConvergingBandsController(30, 46, 0, 75, 50, 100, 150, 150, 3));
                    break;
                case INDEXER_COUNT:
                    break;
                case SHOOTER_FULLRPM:
                    mLEDControllers.add(new FlashingLightsController(0, 20, 60, 255, 40, 3));
                    break;
                default:
                    break;
            }
            mState = wantedState;
        }
			for (LEDController ledController : mLEDControllers) {
				LightingOutputs currentOutput = ledController.update(commands, robotState);
				for (var i = 0; i < currentOutput.lightingOutput.size(); i++) {
					int[] rgbValue = currentOutput.lightingOutput.get(i);
					mOutputBuffer.setHSV(i + ledController.mInitIndex, rgbValue[0], rgbValue[1], rgbValue[2]);
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
