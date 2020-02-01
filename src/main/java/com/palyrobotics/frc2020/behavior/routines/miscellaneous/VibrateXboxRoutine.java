package com.palyrobotics.frc2020.behavior.routines.miscellaneous;

import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

public class VibrateXboxRoutine extends TimedRoutine {

	public VibrateXboxRoutine(double durationSeconds) {
		super(durationSeconds);
	}

	@Override
	public void update(Commands commands, @ReadOnly RobotState state) {
		commands.wantedRumble = true;
	}
}
