package com.palyrobotics.frc2020.auto;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveAlignRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveYawRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.*;
import com.palyrobotics.frc2020.subsystems.Indexer;

public class TrenchStealThreeShootThree extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		var setInitialOdometry = new DriveSetOdometryRoutine(0, 0, 0);

		var intakeTrenchBalls = new ParallelRaceRoutine(
				new SequentialRoutine(
						new DrivePathRoutine(newWaypoint(88, 0, 0)).setMovement(1.5, 2.5),
						new DriveYawRoutine(12.0)),
				new IndexerTimeRoutine(Double.POSITIVE_INFINITY),
				new IntakeBallRoutine(Double.POSITIVE_INFINITY, 2.0));

		var shootingPos = new ParallelRoutine(new DrivePathRoutine(newWaypoint(50, 150, 180)).setMovement(3.0, 5.0).driveInReverse(),
				new IndexerTimeRoutine(1.5), new SequentialRoutine(new IndexerHopperRoutine(Indexer.HopperState.OPEN),
						new IndexerHopperRoutine(Indexer.HopperState.CLOSED),
						new IndexerTimeRoutine(1.0)));

		var shootBalls = new SequentialRoutine(new DriveAlignRoutine(0), new ParallelRoutine(new ShooterVisionRoutine(8),
				new SequentialRoutine(new TimedRoutine(30), new IndexerTimeRoutine(7), new IndexerTimeRoutine(1.0),
						new IndexerFeedAllRoutine(1, true, false))));

		return new SequentialRoutine(setInitialOdometry, intakeTrenchBalls, shootingPos, shootBalls);
	}
}
