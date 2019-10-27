package com.palyrobotics.frc2019.config.driveteam;

import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.constants.OtherConstants;

class DriverProfiles {

    /**
     * Class for configuring the control DrivetrainConstants for the robot Has one static method which configures the DrivetrainConstants based off the driver
     *
     * @author Justin
     */
    static void configureConstants() {
        switch (OtherConstants.kDriverName) {
            case BRYAN:
                DrivetrainConstants.kDeadband = 0.02;
                DrivetrainConstants.kMaxAccelRate = 0.02;

                DrivetrainConstants.kDriveSensitivity = 0.85;

                DrivetrainConstants.kQuickTurnSensitivity = 0.7;//0.8;
                DrivetrainConstants.kPreciseQuickTurnSensitivity = 0.4;//0.35;

                DrivetrainConstants.kQuickTurnSensitivityThreshold = 1.01;

                DrivetrainConstants.kQuickStopAccumulatorDecreaseRate = 0.8;//0.8;

                DrivetrainConstants.kQuickStopAccumulatorDecreaseThreshold = 1.2;//1.2;
                DrivetrainConstants.kNegativeInertiaScalar = 6.0;

                DrivetrainConstants.kAlpha = 0.45;//0.55;//0.45;

                DrivetrainConstants.kCyclesUntilStop = 50;
                break;
        }
    }
}