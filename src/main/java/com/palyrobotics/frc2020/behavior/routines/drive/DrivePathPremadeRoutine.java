package com.palyrobotics.frc2020.behavior.routines.drive;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2020.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;

public class DrivePathPremadeRoutine extends DrivePathRoutine {

//	private static final String kTrajFolderName = "output";
	private static final Path kConfigFolder = Paths.get(Filesystem.getDeployDirectory().toString());

	private static Path resolveConfigPath(String name) {
			return kConfigFolder.resolve(name);
	}

	private String mTrajectoryFileName;

	public DrivePathPremadeRoutine(String trajectoryFileName) throws JsonProcessingException {
		this.mTrajectoryFileName = trajectoryFileName;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		// Required to start the timeout timer
		super.start(commands, state);
//		generateTrajectory();
	}

	@Override
	public void generateTrajectory(Pose2d startingPos) {
		var resolvedConfigPath = resolveConfigPath(this.mTrajectoryFileName);
		try {
			super.mTrajectory = TrajectoryUtil.fromPathweaverJson(resolvedConfigPath);
		}
	 	catch(IOException ex){
				DriverStation.reportError("Unable to open trajectory" + resolveConfigPath(this.mTrajectoryFileName).toString(), ex.getStackTrace());
	 	}
	}
}
