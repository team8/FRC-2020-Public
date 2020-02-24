package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.*;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerFeedAllRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IndexerTimeRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeBallRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.ShooterVisionRoutine;
import com.palyrobotics.frc2020.robot.OperatorInterface;

public abstract class FriendlyTrenchRoutine extends AutoBase {

	SequentialRoutine centerStraightFriendlyTrench() {
		return new SequentialRoutine(
				new DriveSetOdometryRoutine(0, 0, 180),
				new ParallelRoutine(
						new DrivePathRoutine(
								newWaypoint(40, 70, 0),
								newWaypoint(170, 70, 0)),
						new SequentialRoutine(
								new TimedRoutine(3.0),
								new ParallelRoutine(
										new IntakeBallRoutine(3.0),
										new IndexerTimeRoutine(3.0)))));
	}

	SequentialRoutine CenterReverseRight90DegreeFriendlyTrench() {
		return new SequentialRoutine(
				new DriveSetOdometryRoutine(0, 0, 180),
//				new VisionAlignRoutine(),
				getBallsRoutine,
				new DrivePathRoutine(newWaypoint(40, 65, 0)).driveInReverse(),
				new DrivePathRoutine(newWaypoint(170, 65, 0)));
	}

	SequentialRoutine CenterTwoPointTurnFriendlyTrench() {
		return new SequentialRoutine(
				new DriveSetOdometryRoutine(0, 0, 180),
//				new VisionAlignRoutine(),
				getBallsRoutine,
				new DrivePathRoutine(newWaypoint(25, -35, 90)).driveInReverse(),
				new DrivePathRoutine(newWaypoint(80, 65, 0), newWaypoint(170, 65, 0)));
	}

	SequentialRoutine Left180FriendlyTrench() {
		return new SequentialRoutine(
				new DriveSetOdometryRoutine(0, 0, 180),
				new DriveYawRoutine(0),
				getBallsRoutine,
				new DrivePathRoutine(newWaypoint(170, 0, 0)));
	}

	SequentialRoutine endRoutine() {
		return new SequentialRoutine(
//				new DriveAlignYawAssistedRoutine(180, kOneTimesZoomPipelineId),
				new DriveAlignYawAssistedRoutine(180, OperatorInterface.kOnesTimesZoomAlignButton),
//				new DriveYawRoutine(180.0),
//				new DriveAlignRoutine(OperatorInterface.kOnesTimesZoomAlignButton),
				new ParallelRoutine(
						new ShooterVisionRoutine(5),
						new IndexerFeedAllRoutine(5, true, true)));
	}
}
