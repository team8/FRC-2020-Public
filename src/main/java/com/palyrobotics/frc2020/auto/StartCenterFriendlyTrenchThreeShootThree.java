package com.palyrobotics.frc2020.auto;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedAllRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeLowerRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.ShooterVisionRoutine;
import com.palyrobotics.frc2020.robot.OperatorInterface;

/**
 * Start by aligning to the tower target, then backing up slowly and also make sure that just the
 * front bumper extends past the white line.
 *
 * @author Alexis
 */
@SuppressWarnings ("Duplicates")
public class StartCenterFriendlyTrenchThreeShootThree extends FriendlyTrenchRoutine {

	// Starts center, initial 180
	@Override
	public RoutineBase getRoutine() {
		return new SequentialRoutine(
				new DriveAlignRoutine(OperatorInterface.kOnesTimesZoomAlignRawButton),
				new ParallelRoutine(
						new IntakeLowerRoutine(),
						new ShooterVisionRoutine(5),
						new IndexerFeedAllRoutine(5, true, false)),
				centerStraightFriendlyTrench(),
				endRoutine());
	}
}
