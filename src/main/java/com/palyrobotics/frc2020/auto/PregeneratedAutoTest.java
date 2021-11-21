package com.palyrobotics.frc2020.auto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2020.behavior.ParallelRoutine;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathPremadeRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.PredicateDriveWaitRoutine;
import com.palyrobotics.frc2020.behavior.routines.superstructure.IntakeLowerRoutine;
import com.palyrobotics.frc2020.util.Util;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.util.Units;

import java.util.function.Predicate;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

public class PregeneratedAutoTest extends AutoBase {

	@Override
	public RoutineBase getRoutine() throws JsonProcessingException {
		Predicate<Pose2d> inTrenchTest = poseMeters -> poseMeters.getTranslation().getX() > Units.inchesToMeters(30.0);
		SequentialRoutine driveCollect = new SequentialRoutine(
				new ParallelRoutine(
						new DrivePathPremadeRoutine("DriveEnemyTrenchPt1.wpilib.json.wpilib"),
						new SequentialRoutine(
								new PredicateDriveWaitRoutine(inTrenchTest), new IntakeLowerRoutine()
						)
				),
				new DrivePathPremadeRoutine("DriveEnemyTrenchPt2.wpilib.json.wpilib"));

	    return driveCollect;
	}
}
