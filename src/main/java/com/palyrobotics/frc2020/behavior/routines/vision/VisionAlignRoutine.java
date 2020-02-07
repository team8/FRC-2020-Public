package com.palyrobotics.frc2020.behavior.routines.vision;

import java.util.Set;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

public class VisionAlignRoutine extends RoutineBase {

	@Override
	public boolean checkFinished(RobotState state) {
		return false;
		// return Math.abs(mLimelight.getYawToTarget()) <= mConfig.acceptableError;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.setDriveVisionAlign();
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
