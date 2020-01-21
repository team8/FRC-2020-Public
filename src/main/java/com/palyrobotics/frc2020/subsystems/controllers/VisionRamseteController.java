package com.palyrobotics.frc2020.subsystems.controllers;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;

/**
 * Implements constant curvature driving. Yoinked from 254 code
 */
public class VisionRamseteController extends Drive.DriveController {

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {

	}
}
