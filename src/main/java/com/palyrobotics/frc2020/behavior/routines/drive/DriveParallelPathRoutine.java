package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.function.Predicate;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;

import edu.wpi.first.wpilibj.geometry.Pose2d;

public class DriveParallelPathRoutine extends ParallelRoutine {

	private final RoutineBase mRoutine;
	private final DrivePathRoutine mDrivePathRoutine;
	private Predicate<Pose2d> mAddRoutinePredicate;
	private boolean mHasAddedRoutine;

	/**
	 * Drive path routine which adds a specified routine when a certain condition is met with the
	 * current pose. The additional routine is only run during the path and will only be added once.
	 *
	 * @param routine             Routine to add when predicate is passed for the first time
	 * @param addRoutinePredicate Tested every update cycle to see if routine should be added based on
	 *                            robots current positioning. Pose is in meters.
	 */
	public DriveParallelPathRoutine(DrivePathRoutine drivePathRoutine, RoutineBase routine, Predicate<Pose2d> addRoutinePredicate) {
		super(drivePathRoutine);
		mRoutine = routine;
		mDrivePathRoutine = drivePathRoutine;
		mAddRoutinePredicate = addRoutinePredicate;
	}

	@Override
	public void update(Commands commands, @ReadOnly RobotState state) {
		if (!mHasAddedRoutine && mAddRoutinePredicate.test(state.drivePoseMeters)) {
			mRunningRoutines.add(mRoutine);
			mHasAddedRoutine = true;
		}
		super.update(commands, state);
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mDrivePathRoutine.isFinished();
	}
}
