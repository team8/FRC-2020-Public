package com.palyrobotics.frc2020.behavior.routines.intake;

import com.palyrobotics.frc2020.behavior.routines.waits.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

public class IntakeBallRoutine extends TimeoutRoutineBase { // TODO implement class

	public IntakeBallRoutine() {
		super(3.0);
	}

	public IntakeBallRoutine(double waitTime) {
		super(waitTime);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return false;
	}
}
