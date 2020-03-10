package com.palyrobotics.frc2020.robot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.auto.StartCenterFriendlyTrenchThreeShootThree;
import com.palyrobotics.frc2020.auto.TrenchStealTwoShootFive;
import com.palyrobotics.frc2020.behavior.MultipleRoutineBase;
import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.behavior.RoutineManager;
import com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2020.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.LoopOverrunDebugger;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.commands.CommandReceiverService;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2020.util.dashboard.LiveGraph;
import com.palyrobotics.frc2020.util.service.NetworkLoggerService;
import com.palyrobotics.frc2020.util.service.RobotService;
import com.palyrobotics.frc2020.util.service.TelemetryService;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

public class Robot extends TimedRobot {

	public static final double kPeriod = 0.02;
	private static final String kLoggerTag = Util.classToJsonName(Robot.class);
	private static final boolean kCanUseHardware = RobotBase.isReal() || !System.getProperty("os.name").startsWith("Mac");
	private final RobotState mRobotState = new RobotState();
	private final Limelight mLimelight = Limelight.getInstance();
	private final RobotConfig mConfig = Configs.get(RobotConfig.class);
	private final OperatorInterface mOperatorInterface = new OperatorInterface();
	private final RoutineManager mRoutineManager = new RoutineManager();
	private final HardwareReader mHardwareReader = new HardwareReader();
	private final HardwareWriter mHardwareWriter = new HardwareWriter();
	private final Commands mCommands = new Commands();

	/* Subsystems */
	private final Climber mClimber = Climber.getInstance();
	private final Drive mDrive = Drive.getInstance();
	private final Indexer mIndexer = Indexer.getInstance();
	private final Intake mIntake = Intake.getInstance();
	private final Shooter mShooter = Shooter.getInstance();
	private final Spinner mSpinner = Spinner.getInstance();
	private final Lighting mLighting = Lighting.getInstance();

	private Set<SubsystemBase> mSubsystems = Set.of(mClimber, mDrive, mIndexer, mIntake, mLighting, mShooter, mSpinner),
			mEnabledSubsystems;
	private Set<RobotService> mServices = Set.of(new CommandReceiverService(), new NetworkLoggerService(),
			new TelemetryService()),
			mEnabledServices;

	public Robot() {
		super(kPeriod);
	}

	@Override
	public void robotInit() {
		LiveWindow.disableAllTelemetry();

		String setupSummary = setupSubsystemsAndServices();

		if (kCanUseHardware) mHardwareWriter.configureHardware(mEnabledSubsystems);

		mEnabledServices.forEach(RobotService::start);

		Log.info(kLoggerTag, setupSummary);

		Configs.listen(RobotConfig.class, config -> {
			if (isDisabled()) {
				updateDriveNeutralMode(config.coastDriveWhenDisabled);
			}
		});
		mCommands.lightingWantedState = Lighting.State.INIT;
		updateLighting();
	}

	@Override
	public void simulationInit() {
//		Log.info(kLoggerTag, "Writing path CSV file...");
		pathToCsv();
	}

	private void pathToCsv() {
		var drivePath = new StartCenterFriendlyTrenchThreeShootThree().getRoutine();
		try (var writer = new PrintWriter(new BufferedWriter(new FileWriter("auto.csv")))) {
			writer.write("x,y,d" + '\n');
			var points = new LinkedList<Pose2d>();
			recurseRoutine(drivePath, points);
			for (Pose2d pose : points) {
				Translation2d point = pose.getTranslation();
				writer.write(String.format("%f,%f,%f%n", point.getY() * -39.37, point.getX() * 39.37, pose.getRotation().getDegrees()));
			}
		} catch (IOException writeException) {
			writeException.printStackTrace();
		}
	}

	private void recurseRoutine(RoutineBase routine, Deque<Pose2d> points) {
		if (routine instanceof MultipleRoutineBase) {
			var multiple = (MultipleRoutineBase) routine;
			for (RoutineBase childRoutine : multiple.getRoutines()) {
				recurseRoutine(childRoutine, points);
			}
		} else if (routine instanceof DriveSetOdometryRoutine) {
			var odometry = (DriveSetOdometryRoutine) routine;
			var pose = odometry.getTargetPose();
			points.addLast(pose);
		} else if (routine instanceof DrivePathRoutine) {
			var path = (DrivePathRoutine) routine;
			System.out.println(points.getLast());
			path.generateTrajectory(points.getLast());
			for (Trajectory.State state : path.getTrajectory().getStates()) {
				var pose = state.poseMeters;
				points.addLast(pose);
			}
		}
	}

	@Override
	public void disabledInit() {
		mRobotState.gamePeriod = RobotState.GamePeriod.DISABLED;
		resetCommandsAndRoutines();

		HardwareAdapter.Joysticks.getInstance().operatorXboxController.setRumble(false);
		updateDriveNeutralMode(mConfig.coastDriveWhenDisabled);

		CSVWriter.write();

		mCommands.lightingWantedState = Lighting.State.DISABLE;
		updateLighting();
	}

	@Override
	public void autonomousInit() {
		startStage(RobotState.GamePeriod.AUTO);
		mCommands.addWantedRoutine(new TrenchStealTwoShootFive().getRoutine());
//		mCommands.addWantedRoutine(new StartCenterFriendlyTrenchThreeShootThree().getRoutine());
	}

	private void startStage(RobotState.GamePeriod period) {
		mRobotState.gamePeriod = period;
		resetCommandsAndRoutines();
		updateDriveNeutralMode(false);
		CSVWriter.cleanFile();
		CSVWriter.resetTimer();
	}

	@Override
	public void teleopInit() {
		startStage(RobotState.GamePeriod.TELEOP);
		mCommands.setDriveTeleop();
		mCommands.lightingWantedState = Lighting.State.OFF;
		updateLighting();
	}

	@Override
	public void testInit() {
		startStage(RobotState.GamePeriod.TESTING);
	}

	@Override
	public void robotPeriodic() {
		for (RobotService robotService : mEnabledServices) {
			robotService.update(mRobotState, mCommands);
		}
		LiveGraph.add("visionEstimatedDistance", mLimelight.getEstimatedDistanceInches());
		LiveGraph.add("isEnabled", isEnabled());
		mOperatorInterface.resetPeriodic(mCommands);
	}

	@Override
	public void simulationPeriodic() {

	}

	@Override
	public void disabledPeriodic() {
		updateVision(mConfig.enableVisionWhenDisabled, mConfig.visionPipelineWhenDisabled);
	}

	@Override
	public void autonomousPeriodic() {
//		mOperatorInterface.defaults(mCommands);
		updateRobotState();
		mRoutineManager.update(mCommands, mRobotState);
		updateSubsystemsAndApplyOutputs();
	}

	public static LoopOverrunDebugger mDebugger = new LoopOverrunDebugger("teleop", 0.02);

	@Override
	public void teleopPeriodic() {
//		mOperatorInterface.defaults(mCommands);
		mDebugger.reset();
		updateRobotState();
		mDebugger.addPoint("robotState");
		mOperatorInterface.updateCommands(mCommands, mRobotState);
		mDebugger.addPoint("updateCommands");
		mRoutineManager.update(mCommands, mRobotState);
		mDebugger.addPoint("routineManagerUpdate");
		updateSubsystemsAndApplyOutputs();
		mDebugger.addPoint("updateSubsystemsAndApplyOutputs");
		mDebugger.finish();
	}

	@Override
	public void testPeriodic() {
		teleopPeriodic();
	}

	private void resetCommandsAndRoutines() {
		mOperatorInterface.reset(mCommands);
		mRoutineManager.clearRunningRoutines();
		updateSubsystemsAndApplyOutputs();
	}

	private void updateRobotState() {
		if (kCanUseHardware) mHardwareReader.updateState(mEnabledSubsystems, mRobotState);
		mRobotState.shooterIsReadyToShoot = mShooter.isReadyToShoot();
	}

	/**
	 * Resets the pose based on {@link Commands#driveWantedOdometryPose}. Sets it to null afterwards to
	 * avoid writing multiple updates to the controllers.
	 */
	private void resetOdometryIfWanted() {
		Pose2d wantedPose = mCommands.driveWantedOdometryPose;
		if (wantedPose != null) {
			mRobotState.resetOdometry(wantedPose);
			if (kCanUseHardware) mHardwareWriter.resetDriveSensors(wantedPose);
			mCommands.driveWantedOdometryPose = null;
		}
	}

	private void updateSubsystemsAndApplyOutputs() {
		resetOdometryIfWanted();
		for (SubsystemBase subsystem : mEnabledSubsystems) {
			subsystem.update(mCommands, mRobotState);
			mDebugger.addPoint(subsystem.getName());
		}
		if (kCanUseHardware) {
			mHardwareWriter.updateHardware(mEnabledSubsystems, mRobotState);
			mHardwareWriter.setClimberSoftLimitsEnabled(mCommands.climberWantsSoftLimits);
		}
		mDebugger.addPoint("updateHardware");
		updateVision(mCommands.visionWanted, mCommands.visionWantedPipeline);
		updateCompressor();
	}

	private void updateCompressor() {
		var compressor = HardwareAdapter.MiscellaneousHardware.getInstance().compressor;
		if (mCommands.wantedCompression) {
			compressor.start();
		} else {
			compressor.stop();
		}
	}

	private void updateVision(boolean visionWanted, int visionPipeline) {
		if (visionWanted) {
			mLimelight.setCamMode(LimelightControlMode.CamMode.VISION);
			mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_ON);
		} else {
			mLimelight.setCamMode(LimelightControlMode.CamMode.DRIVER);
			mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_OFF);
		}
		mLimelight.setPipeline(visionPipeline);
	}

	private String setupSubsystemsAndServices() {
		// TODO: same logic twice in a row
		Map<String, RobotService> configToService = mServices.stream()
				.collect(Collectors.toUnmodifiableMap(RobotService::getConfigName, Function.identity()));
		mEnabledServices = mConfig.enabledServices.stream().map(configToService::get)
				.collect(Collectors.toUnmodifiableSet());
		Map<String, SubsystemBase> configToSubsystem = mSubsystems.stream()
				.collect(Collectors.toUnmodifiableMap(SubsystemBase::getName, Function.identity()));
		mEnabledSubsystems = mConfig.enabledSubsystems.stream().map(configToSubsystem::get)
				.collect(Collectors.toUnmodifiableSet());
		var summaryBuilder = new StringBuilder("\n");
		summaryBuilder.append("===================\n");
		summaryBuilder.append("Enabled subsystems:\n");
		summaryBuilder.append("-------------------\n");
		for (SubsystemBase enabledSubsystem : mEnabledSubsystems) {
			summaryBuilder.append(enabledSubsystem.getName()).append("\n");
		}
		summaryBuilder.append("=================\n");
		summaryBuilder.append("Enabled services:\n");
		summaryBuilder.append("-----------------\n");
		for (RobotService enabledService : mEnabledServices) {
			summaryBuilder.append(enabledService.getConfigName()).append("\n");
		}
		return summaryBuilder.toString();
	}

	private void updateDriveNeutralMode(boolean isIdle) {
		if (kCanUseHardware && mEnabledSubsystems.contains(mDrive)) mHardwareWriter.setDriveNeutralMode(isIdle ? NeutralMode.Coast : NeutralMode.Brake);
	}

	private void updateLighting() {
		if (kCanUseHardware && mEnabledSubsystems.contains(mLighting)) {
			mLighting.update(mCommands, mRobotState);
			mHardwareWriter.updateLighting();
		}
	}
}
