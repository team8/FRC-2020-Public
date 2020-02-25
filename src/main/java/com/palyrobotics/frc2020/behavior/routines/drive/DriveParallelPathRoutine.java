package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory.*;

import java.util.List;
import java.util.Set;

public class DriveParallelPathRoutine extends ParallelRoutine { // TODO implement class

	private final RoutineBase mRoutine;
	private final DrivePathRoutine mDrivePathRoutine;
	private final double mDistanceUntilEnd;
	private Translation2d mCurrentDifference;
	private List<State> mTrajectoryStates;
	private boolean isAdded;


	public DriveParallelPathRoutine(RoutineBase routine, DrivePathRoutine drivePathRoutine, double distanceUntilEndMeters) {
		super(drivePathRoutine);
		mRoutine = routine;
		mDrivePathRoutine = drivePathRoutine;
		mDistanceUntilEnd = distanceUntilEndMeters;
		mTrajectoryStates = drivePathRoutine.getTrajectory().getStates();
	}

	@Override
	public void update(Commands commands, @ReadOnly RobotState state) {
		mCurrentDifference = mTrajectoryStates.get(mTrajectoryStates.size() - 1).poseMeters.minus(state.drivePoseMeters).getTranslation();
		if (Math.pow(mCurrentDifference.getX(), 2) + Math.pow(mCurrentDifference.getY(), 2) <= Math.pow(mDistanceUntilEnd, 2) && !isAdded) {
			commands.addWantedRoutine(new ParallelRoutine(mRoutine));
			isAdded = false;
		}
		super.update(commands, state);
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mDrivePathRoutine.isFinished();
	}
}
