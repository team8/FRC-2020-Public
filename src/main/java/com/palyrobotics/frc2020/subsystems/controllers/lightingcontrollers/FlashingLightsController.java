package com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import edu.wpi.first.wpilibj.Timer;

public class FlashingLightsController extends Lighting.LEDController {
    Timer mTimer = new Timer();
    private int h;
    private int s;
    private int v;

    public FlashingLightsController (int initIndex, int lastIndex, int h, int s, int v) {
        mInitIndex = initIndex;
        mLastIndex = lastIndex;
        this.h = h;
        this.s = s;
        this.v = v;
        mTimer.start();
        for(var i = mInitIndex;i < mLastIndex;i++){
            mLightingOutputs.lightingOutput.add(new int[]{h,s,v});
        }
    }
    @Override
    public void updateSignal(Commands commands, RobotState state) {
        double time = Math.round(mTimer.get());
        if (time % 2 == 0) {
            for (int i = mInitIndex; i < mLastIndex; i++) {
                mLightingOutputs.lightingOutput.get(i - mInitIndex)[0] = h;
                mLightingOutputs.lightingOutput.get(i - mInitIndex)[1] = s;
                mLightingOutputs.lightingOutput.get(i - mInitIndex)[2] = v;
            }
        } else {
            for (int i = mInitIndex; i < mLastIndex; i++) {
                mLightingOutputs.lightingOutput.get(i - mInitIndex)[0] = 0;
                mLightingOutputs.lightingOutput.get(i - mInitIndex)[1] = 0;
                mLightingOutputs.lightingOutput.get(i - mInitIndex)[2] = 0;
            }
        }
    }
}
