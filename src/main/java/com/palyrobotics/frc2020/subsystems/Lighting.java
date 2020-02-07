package com.palyrobotics.frc2020.subsystems;

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
        IDLE, INIT, DISABLE, TARGET_FOUND, INDEXER_COUNT, CLIMB_TIME
    }

    private static Lighting sInstance = new Lighting();
    private LightingConfig mConfig = Configs.get(LightingConfig.class);
    private AddressableLEDBuffer mOutputBuffer = new AddressableLEDBuffer(50);
    private int mLedCounter = 0;
    private int[] lastLedRGB = new int[3]; // will be used later
    Lighting.LightingState currentState; // used to compare with new lighting states
    private LEDController[] mLEDControllers = new LEDController[] { null, null, null };

    public abstract static class LEDController {

        protected final LightingConfig mLightingConfig = Configs.get(LightingConfig.class);

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
        Lighting.LightingState state = commands.lightingWantedState;
        // testPWM.setRaw(255);
        // System.out.println("in subsytem");
        switch (state) {
            case IDLE:
                // System.out.println("idle");
                for (int i = 0; i < mOutputBuffer.getLength(); i++) {
                    lastLedRGB[0] = 0;
                    lastLedRGB[1] = 0;
                    lastLedRGB[2] = 0;
                    mOutputBuffer.setRGB(i, lastLedRGB[0], lastLedRGB[1], lastLedRGB[2]);
                }
                break;
            case INIT:
                if (state != currentState) {
                    for (int i = 0; i < mOutputBuffer.getLength(); i++) {
                        lastLedRGB[0] = 0;
                        lastLedRGB[1] = 0;
                        lastLedRGB[2] = 0;
                        mOutputBuffer.setRGB(i, lastLedRGB[0], lastLedRGB[1], lastLedRGB[2]);
                    }
                    // commands.addWantedRoutine(new ParallelRoutine(new
                    // InitSequenceRoutine(mIndexerOutputBuffer, 0, (mConfig.ledCount)/2), new
                    // InitSequenceRoutine(mIndexerOutputBuffer, mConfig.ledCount/2,
                    // mConfig.ledCount)));
                    mLEDControllers[0] = new InitSequenceController(0, (mConfig.ledCount - 1));
                    mLEDControllers[1] = new ConvergingBandsController(30, 46, 0, 75,  50, 100, 150, 150, 3);;
                    mLEDControllers[2] = null;
                    // commands.addWantedRoutine(new InitSequenceRoutine(mLimelightOutputBuffer));
                }
                break;
            case DISABLE:
                if (currentState != state) {
                    mLEDControllers[0] = new DisabledSequenceController(0, mConfig.ledCount - 1);
                    mLEDControllers[1] = null;
                    mLEDControllers[2] = null;
                }
                break;
            case TARGET_FOUND:
				/*for (int i = 0; i < mIndexerOutputBuffer.getLength(); i++) {
					if (mLedCounter % 2 == 0) {
						lastLedRGB[0] = 0;
						lastLedRGB[1] = 100;
						lastLedRGB[2] = 0;
						mIndexerOutputBuffer.setRGB(i, lastLedRGB[0], lastLedRGB[1], lastLedRGB[2]);
					} else {
						lastLedRGB[0] = 0;
						lastLedRGB[1] = 0;
						lastLedRGB[2] = 0;
						mIndexerOutputBuffer.setRGB(i, lastLedRGB[0], lastLedRGB[1], lastLedRGB[2]);
					}
				}*/
                if (currentState != state) {
                    mLEDControllers[0] = null;
                    mLEDControllers[1] = new ConvergingBandsController(30, 46, 0, 75,  50, 100, 150, 150, 3);
                    mLEDControllers[2] = null;
                }
                break;
            case INDEXER_COUNT:
//				if (tempBallCount > 5) {
//					tempBallCount = 0;
//				}
//				commands.addWantedRoutine(
//						new IndexerLEDsRoutine(tempBallCount, mIndexerOutputBuffer, 0, mConfig.ledCount - 1));
                break;
            case CLIMB_TIME:
                if(currentState != state){
                    mLEDControllers[0] = new ConvergingBandsController(0, 20, 0, 75,  50, 100, 255, 255, 3);
                    mLEDControllers[1] = null;
                    mLEDControllers[2] = null;
                }

                break;
            default:
                break;
        }
        mLedCounter++;
        currentState = state;
        for (LEDController ledController : mLEDControllers) {
            if (ledController != null) {
                LightingOutputs currentOutput = ledController.update(commands, robotState);
                for (var i = 0; i < currentOutput.lightingOutput.size(); i++) {
                    int[] rgbValue = currentOutput.lightingOutput.get(i);
                    mOutputBuffer.setHSV(i + ledController.mInitIndex, rgbValue[0], rgbValue[1], rgbValue[2]);
                }
            }
        }
    }


    public AddressableLEDBuffer getOutput() {
        return mOutputBuffer;
    }

}
