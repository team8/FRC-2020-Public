package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;
import static com.palyrobotics.frc2020.vision.Limelight.kOneTimesZoomPipelineId;

import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignYawAssistedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeStowRoutine;

public abstract class FriendlyTrenchRoutine extends AutoBase {

	SequentialRoutine CenterStraightFriendlyTrench() {
		return new SequentialRoutine(
				new DriveSetOdometryRoutine(0, 0, 180),
				getBallsRoutine, new DrivePathRoutine(
						newWaypoint(40, 55, 0),
						newWaypoint(170, 55, 0)));
	}

	SequentialRoutine CenterReverseRight90DegreeFriendlyTrench() {
		return new SequentialRoutine(
				new DriveSetOdometryRoutine(0, 0, 180),
				getBallsRoutine,
				new DrivePathRoutine(newWaypoint(40, 55, 0)).reverse(),
				new DrivePathRoutine(newWaypoint(170, 55, 0)));
	}

	SequentialRoutine CenterTwoPointTurnFriendlyTrench() {
		return new SequentialRoutine(
				new DriveSetOdometryRoutine(0, 0, 180),
				new DrivePathRoutine(newWaypoint(25, -35, 90)).reverse(),
				getBallsRoutine,
				new DrivePathRoutine(newWaypoint(80, 45, 0), newWaypoint(170, 45, 0)));
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
				new IntakeStowRoutine(),
				new DriveAlignYawAssistedRoutine(180, kOneTimesZoomPipelineId),
				shootBallsRoutine);
	}
}
