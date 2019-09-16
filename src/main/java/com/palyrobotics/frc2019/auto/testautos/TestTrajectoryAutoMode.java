package com.palyrobotics.frc2019.auto.testautos;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.behavior.SequentialRoutine;
import com.palyrobotics.frc2019.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2019.util.trajectory.Path;
import com.palyrobotics.frc2019.util.trajectory.Path.Waypoint;
import com.palyrobotics.frc2019.util.trajectory.Translation2d;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nihar on 4/5/17.
 */
public class TestTrajectoryAutoMode extends AutoModeBase {
	private Path mPath;

	public TestTrajectoryAutoMode() {
	}

	@Override
	public String toString() {
		return "TestTrajectoryAutoMode";
	}

	@Override
	public void prestart() {
	}

	@Override
	public Routine getRoutine() {
		List<Waypoint> path = new ArrayList<>();

		//Path 1: Forward and left
//		path.add(new Waypoint(new Translation2d(0, 0), 72.0));
//		path.add(new Waypoint(new Translation2d(110, 0), 72.0));
//		path.add(new Waypoint(new Translation2d(110, 80), 0.0));
//		path.add(new Waypoint(new Translation2d(70, -70), 72.0));
//		path.add(new Waypoint(new Translation2d(140, 0), 72.0));
//		path.add(new Waypoint(new Translation2d(70, 70), 72.0));
//		path.add(new Waypoint(new Translation2d(0, 0), 0.0));
//		path.add(new Waypoint(new Translation2d(110, 80), 0.0));
//		path.add(new Waypoint(new Translation2d(40, 80), 0.0));

		//Path 2: Lollipop
		/*
		 * path.add(new Waypoint(new Translation2d(0,0), 6.0)); path.add(new Waypoint(new Translation2d(60,0), 6.0)); path.add(new Waypoint(new
		 * Translation2d(120, 60), 6.0)); path.add(new Waypoint(new Translation2d(180,0), 6.0)); path.add(new Waypoint(new Translation2d(120,-60), 6.0));
		 * path.add(new Waypoint(new Translation2d(60, 0), 6.0)); path.add(new Waypoint(new Translation2d(0, 0), 0.0));
		 */

		//Path 3: Full ten-point test course
		path.add(new Waypoint(new Translation2d(0, 0)));
		path.add(new Waypoint(new Translation2d(120, 0)));
		path.add(new Waypoint(new Translation2d(0, 180)));
		path.add(new Waypoint(new Translation2d(0, 225)));
		path.add(new Waypoint(new Translation2d(120, 225)));
		path.add(new Waypoint(new Translation2d(120, 200)));
		path.add(new Waypoint(new Translation2d(60, 200)));
		path.add(new Waypoint(new Translation2d(60, 100)));
		path.add(new Waypoint(new Translation2d(120, 100)));
		path.add(new Waypoint(new Translation2d(160, 125)));

		ArrayList<Routine> routines = new ArrayList<>();
//		routines.add(new DriveSensorResetRoutine());
		routines.add(new DrivePathRoutine(new Path(path), false));

		return new SequentialRoutine(routines);
	}

	@Override
	public String getKey() {
		return "Test Trajectory Auto Mode";
	}
}
