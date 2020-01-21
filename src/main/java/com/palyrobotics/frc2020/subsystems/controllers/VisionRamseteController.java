package com.palyrobotics.frc2020.subsystems.controllers;

import java.util.*;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.SequentialRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.vision.Limelight;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.util.Units;

/**
 * Uses Ramsete to align to loading station
 */
public class VisionRamseteController extends Drive.DriveController {

	private Limelight mLimelight = Limelight.getInstance();
	private List<Pose2d> mWaypoints = new ArrayList<>();
	private Pose2d mOrigin = new Pose2d(0, 0, Rotation2d.fromDegrees(0));
	private List<RoutineBase> mRoutines = new ArrayList<>();

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		mWaypoints.add(mOrigin);
		mWaypoints.add(new Pose2d(Units.inchesToMeters(mLimelight.getPnPTranslationX()),
				Units.inchesToMeters(mLimelight.getPnPTranslationY()),
				Rotation2d.fromDegrees(mLimelight.getYawToTarget())));
		mRoutines.add(new DrivePathRoutine(mWaypoints));
		Commands.getInstance().addWantedRoutine(new SequentialRoutine(mRoutines));
		mWaypoints.clear();
		mRoutines.clear();
	}
}
