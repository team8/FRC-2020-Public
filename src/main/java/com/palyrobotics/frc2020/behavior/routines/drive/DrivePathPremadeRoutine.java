package com.palyrobotics.frc2020.behavior.routines.drive;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;

public class DrivePathPremadeRoutine extends DrivePathRoutine {

	private static final String kTrajFolderName = "built-trajectories", kLoggerTag = Util.classToJsonName(Configs.class);
	private static final Path kConfigFolder = Paths.get(Filesystem.getDeployDirectory().toString(), kTrajFolderName);

	private static Path resolveConfigPath(String name) {
		return kConfigFolder.resolve(String.format("%s.json", name));
	}

	private String mTrajectoryFileName;

	public DrivePathPremadeRoutine(String trajectoryFileName) throws JsonProcessingException {
		this.mTrajectoryFileName = trajectoryFileName;
	}

	public void generateTrajectory() {
		var resolvedConfigPath = resolveConfigPath(this.mTrajectoryFileName);
		try {
			super.mTrajectory = TrajectoryUtil.fromPathweaverJson(resolvedConfigPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
