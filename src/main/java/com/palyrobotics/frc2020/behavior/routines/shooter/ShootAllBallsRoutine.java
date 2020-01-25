package com.palyrobotics.frc2020.behavior.routines.shooter;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

public class ShootAllBallsRoutine extends TimeoutRoutineBase {

	public ShootAllBallsRoutine() {
		super(2.0); // TODO implement class
	}

	public ShootAllBallsRoutine(double timeout) {
		super(timeout);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return false;
	}
}
