package com.palyrobotics.frc2018.config.driveteam;

import com.palyrobotics.frc2018.config.Constants;

public class OperatorProfiles {
	public static void configureConstants() {
		switch(Constants.kOperatorName) {
			case JACOB:
				Constants.kArmUncalibratedManualPower = 0.7;
				Constants.kArmConstantDownPower = -0.2;
				Constants.operatorXBoxController = true;
				break;
		}
	}
}
