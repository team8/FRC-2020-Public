package com.palyrobotics.frc2020.behavior.routines.drive;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2020.util.TrajectoryReader.TrajectoryReader;

import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class DrivePathPremadeRoutine extends DrivePathRoutine {

	public List<Trajectory.State> generateTrajectory(String trajectoryFile) throws JsonProcessingException {
		TrajectoryReader trajReader = new TrajectoryReader(trajectoryFile);
		return trajReader.getTrajectory();
	}
}
