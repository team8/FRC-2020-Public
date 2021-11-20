package com.palyrobotics.frc2020.subsystems.controllers.lighting;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Lighting;
import com.palyrobotics.frc2020.subsystems.Shooter;
import com.palyrobotics.frc2020.util.Color;
import com.palyrobotics.frc2020.config.subsystem.LightingConfig;
import com.palyrobotics.frc2020.util.config.Configs;

public class ShooterColorController extends Lighting.LEDController {

    private LightingConfig mConfig = Configs.get(LightingConfig.class);
    private static ShooterColorController sInstance = new ShooterColorController();
    private int length;
    private int firstIndex;
    private int lastIndex;
    private int orangeLEDs;
    private int mDuration;

    public ShooterColorController()
    {
        super(0, 28);
        mStartIndex = 0;
        mLastIndex = 28;
        kPriority = 1;
    }
    public void initallize(int shooterVelocity, int maxShooterVelocity, int duration)
    {
        length = (int) shooterVelocity/maxShooterVelocity * 8;
        orangeLEDs = 0;
        if (Math.abs((((double) shooterVelocity/(double) maxShooterVelocity) * 4.0) - length) >= 0.2)
        {
            orangeLEDs++;
        }
        firstIndex = length;
        lastIndex = mConfig.ledCount-firstIndex;
        isOn = true;
        mDuration = duration;
    }
    @Override
    public void updateSignal(Commands commands, RobotState state)
    {
        if (length != 0)
        {
            for (int i = 0; i < firstIndex-orangeLEDs; i++)
            {
                mOutputs.lightingOutput.get(i).setHSV(Color.HSV.kGreen.getH(), Color.HSV.kGreen.getS(), Color.HSV.kGreen.getV());
            }
            for (int i = lastIndex+orangeLEDs; i < mConfig.ledCount; i++)
            {
                mOutputs.lightingOutput.get(i).setHSV(Color.HSV.kGreen.getH(), Color.HSV.kGreen.getS(), Color.HSV.kGreen.getV());
            }

            for (int i = firstIndex-orangeLEDs; i < firstIndex; i++)
            {
                mOutputs.lightingOutput.get(i).setHSV(Color.HSV.kOrange.getH(), Color.HSV.kOrange.getS(), Color.HSV.kOrange.getV());
            }
            for (int i = lastIndex; i < lastIndex+orangeLEDs; i++)
            {
                mOutputs.lightingOutput.get(i).setHSV(Color.HSV.kOrange.getH(), Color.HSV.kOrange.getS(), Color.HSV.kOrange.getV());
            }

            for (int i = firstIndex; i < 8; i++)
            {
                mOutputs.lightingOutput.get(i).setHSV(Color.HSV.kRed.getH(), Color.HSV.kRed.getS(), Color.HSV.kRed.getV());
            }
            for (int i = 20; i < lastIndex; i++)
            {
                mOutputs.lightingOutput.get(i).setHSV(Color.HSV.kRed.getH(), Color.HSV.kRed.getS(), Color.HSV.kRed.getV());
            }
        }
        else
        {
            for (int i = orangeLEDs; i < 8; i++)
            {
                mOutputs.lightingOutput.get(i).setHSV(Color.HSV.kRed.getH(), Color.HSV.kRed.getS(), Color.HSV.kRed.getV());
            }
            for (int i = 20; i < mConfig.ledCount-orangeLEDs; i++)
            {
                mOutputs.lightingOutput.get(i).setHSV(Color.HSV.kRed.getH(), Color.HSV.kRed.getS(), Color.HSV.kRed.getV());
            }

            for (int i = 0; i < orangeLEDs; i++)
            {
                mOutputs.lightingOutput.get(i).setHSV(Color.HSV.kOrange.getH(), Color.HSV.kOrange.getS(), Color.HSV.kOrange.getV());
            }
            for (int i = mConfig.ledCount-orangeLEDs; i < mConfig.ledCount; i++)
            {
                mOutputs.lightingOutput.get(i).setHSV(Color.HSV.kOrange.getH(), Color.HSV.kOrange.getS(), Color.HSV.kOrange.getV());
            }
        }
    }

    @Override
    public boolean checkFinished()
    {
        if (mDuration != -1 && mTimer.hasElapsed(mDuration))
        {
            isOn = false;
            return true;
        }
        return false;
    }
}
