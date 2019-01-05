package com.palyrobotics.frc2018.vision;

import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.util.logger.Logger;

public class AndroidComputerTest {

    public static void main(String[] args) {
        Logger.getInstance().setFileName("Android Test");
        Logger.getInstance().start();
        VisionManager.getInstance().start(Constants.kVisionManagerUpdateRate, true);
    }
}
