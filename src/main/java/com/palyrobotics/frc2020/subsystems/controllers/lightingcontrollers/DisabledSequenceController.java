package com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;

public class DisabledSequenceController extends Lighting.LEDController {
    int hue = 0;
    boolean isAdding = true;

    public DisabledSequenceController(int initIndex, int lastIndex){
        mInitIndex = initIndex;
        mLastIndex = lastIndex;
        for(var i = initIndex;i <= lastIndex;i++){
            mLightingOutputs.lightingOutput.add(new int[]{hue,247,87});
        }
    }

    @Override
    public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
        if(isAdding){
            hue++;
        }else{
            hue--;
        }
        if(hue >= 200){
            isAdding = false;
        }
        if(hue < 0){
            isAdding = true;
        }
        for(var i = 0;i < mLightingOutputs.lightingOutput.size();i++){
            mLightingOutputs.lightingOutput.get(i)[0] = hue;
        }
    }
}
