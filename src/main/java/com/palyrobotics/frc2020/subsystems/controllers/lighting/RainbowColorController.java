package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.subsystems.Shooter;
import com.palyrobotics.frc2020.util.Color;
import com.palyrobotics.frc2020.config.subsystem.LightingConfig;
import com.palyrobotics.frc2020.util.config.Configs;

public class RainbowColorController extends Lighting.LEDController {

    private LightingConfig mConfig = Configs.get(LightingConfig.class);
    private static RainbowColorController sInstance = new RainbowColorController();
    private int firstIndex;
    private int lastIndex;
    private int mDuration;
    private int index;
    private int colorHSVValues[][];
    public RainbowColorController()
    {
        mStartIndex = 0;
        mLastIndex = 28;
        kPriority = 1;
    }

    public void initallize(int startIndex, int endIndex, int duration)
    {
        firstIndex = startIndex;
        lastIndex = endIndex;
        mTimer.start();
        mDuration = duration;
        isOn = true;
        index = 0;
        colorHSVValues = new int[][]{{0, 23, 47, 70, 93, 116, 140, 163, 186, 209, 233, 256},{247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247, 247},{87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87}};

    }
    @Override
    public void updateSignal(Commands commands, RobotState state)
    {
        for (int i = firstIndex; i <= lastIndex; i++)
        {
            mOutputs.lightingOutput.get(i).setHSV(colorHSVValues[(i+index)%12][0], colorHSVValues[(i+index)%12][1], colorHSVValues[(i+index)%12][2]);
        }
        index = (index + 1)%12;
    }

    @Override
    public boolean checkFinished()
    {
        if (mDuration != -1 && mTimer.hasElapsed(mDuration)) {
            isOn = false;
            return true;
        }
        return false;
    }
}
