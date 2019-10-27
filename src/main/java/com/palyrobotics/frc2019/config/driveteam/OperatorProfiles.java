package com.palyrobotics.frc2019.config.driveteam;

import com.palyrobotics.frc2019.config.constants.OtherConstants;

public class OperatorProfiles {
    public static void configureConstants() {
        switch (OtherConstants.kOperatorName) {
            case GRIFFIN:
                OtherConstants.operatorXBoxController = true;
                break;
        }
    }
}
