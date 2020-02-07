package com.palyrobotics.frc2020.subsystems.controllers.lightingcontrollers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import edu.wpi.first.wpilibj.Timer;

public class InitSequenceController extends Lighting.LEDController {
    int ledIndex;

    public InitSequenceController(int initIndex, int lastIndex) {
        mInitIndex = initIndex;
        mLastIndex = lastIndex;
        ledIndex = initIndex;
    }

    @Override
    public void updateSignal(Commands commands, RobotState state) {
        if (ledIndex <= mLastIndex) {
            mLightingOutputs.lightingOutput.add(new int[] {(int) ((ledIndex - mInitIndex) * Math.log(ledIndex)  + 5), 247, 100 });
            ledIndex += 1;
        }
        Timer.delay(0.07);
    }
}
