package com.palyrobotics.frc2020.auto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathPremadeRoutine;

public class PregeneratedAutoTest extends AutoBase {

	@Override
	public RoutineBase getRoutine() throws JsonProcessingException {
		var temp = new DrivePathPremadeRoutine("2BallAuto.wpilib");

		return temp;
	}
}
