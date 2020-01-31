package com.palyrobotics.frc2020.robot;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.behavior.RoutineManager;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.commands.CommandReceiver;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2020.util.service.NetworkLogger;
import com.palyrobotics.frc2020.util.service.RobotService;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.geometry.Pose2d;

public class Robot extends TimedRobot {

	private static final String kLoggerTag = Util.classToJsonName(Robot.class);
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

	private Set<SubsystemBase> mSubsystems = Set.of(mClimber, mDrive, mIndexer, mIntake, mShooter, mSpinner),
			mEnabledSubsystems;
	private Set<RobotService> mServices = Set.of(new CommandReceiver(), new NetworkLogger()), mEnabledServices;

	@Override
	public void robotInit() {
		String setupSummary = setupSubsystemsAndServices();

		mHardwareWriter.configureHardware(mEnabledSubsystems);

		mEnabledServices.forEach(RobotService::start);

		Log.info(kLoggerTag, setupSummary);

		Configs.listen(RobotConfig.class, config -> {
			if (isDisabled()) {
				setDriveIdleMode(config.coastDriveIfDisabled);
			}
		});
	}

	// private void pathToCsv() {
	// var drivePath = new ShootThreeFriendlyTrenchThreeShootThree().getRoutine();
	// try (var writer = new PrintWriter(new BufferedWriter(new
	// FileWriter("auto.csv")))) {
	// writer.write("x,y");
	// for (Trajectory.State state : drivePath.getTrajectory().getStates()) {
	// var point = state.poseMeters.getTranslation();
	// writer.write(String.format("%f,%f%n", point.getX(), point.getY()));
	// }
	// } catch (IOException writeException) {
	// writeException.printStackTrace();
	// }
	// }

	@Override
	public void disabledInit() {
		mRobotState.gamePeriod = RobotState.GamePeriod.DISABLED;
		mRobotState.resetUltrasonics();

		resetCommandsAndRoutines();

		mLimelight.setCamMode(LimelightControlMode.CamMode.DRIVER);
		mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_OFF);

		HardwareAdapter.Joysticks.getInstance().operatorXboxController.setRumble(false);
		setDriveIdleMode(mConfig.coastDriveIfDisabled);

		CSVWriter.write();
	}

	private void resetCommandsAndRoutines() {
		mOperatorInterface.reset(mCommands);
		mRoutineManager.clearRunningRoutines();
		updateSubsystemsAndHardware();
	}

	private void updateSubsystemsAndHardware() {
		resetOdometryIfWanted();
		for (SubsystemBase subsystem : mEnabledSubsystems) {
			subsystem.update(mCommands, mRobotState);
		}
		mHardwareWriter.updateHardware(mEnabledSubsystems);
	}

	@Override
	public void autonomousInit() {
		startStage(RobotState.GamePeriod.AUTO);
	}

	private void startStage(RobotState.GamePeriod period) {
		mRobotState.gamePeriod = period;
		resetCommandsAndRoutines();
		setDriveIdleMode(false);
		CSVWriter.cleanFile();
		CSVWriter.resetTimer();
	}

	@Override
	public void teleopInit() {
		startStage(RobotState.GamePeriod.TELEOP);
		mCommands.setDriveTeleop();
	}

	@Override
	public void testInit() {
		startStage(RobotState.GamePeriod.TESTING);
	}

	@Override
	public void robotPeriodic() {
		mEnabledServices.forEach(RobotService::update);
	}

	@Override
	public void disabledPeriodic() {
	}

	@Override
	public void autonomousPeriodic() {
		mOperatorInterface.defaults(mCommands);
		mHardwareReader.updateState(mEnabledSubsystems, mRobotState);
		mRoutineManager.update(mCommands, mRobotState);
		updateSubsystemsAndHardware();
	}

	@Override
	public void teleopPeriodic() {
		mOperatorInterface.defaults(mCommands);
		mHardwareReader.updateState(mEnabledSubsystems, mRobotState);
		mOperatorInterface.updateCommands(mCommands, mRobotState);
		mRoutineManager.update(mCommands, mRobotState);
		HardwareAdapter.Joysticks.getInstance().operatorXboxController.setRumble(mCommands.wantedRumble);
		resetOdometryIfWanted();
		updateSubsystemsAndHardware();
	}

	@Override
	public void testPeriodic() {
		teleopPeriodic();
	}

	/**
	 * Resets the pose based on {@link Commands#driveWantedOdometryPose}. Sets it to
	 * null afterwards to avoid writing multiple updates to the controllers.
	 */
	private void resetOdometryIfWanted() {
		Pose2d wantedPose = mCommands.driveWantedOdometryPose;
		if (wantedPose != null) {
			mRobotState.resetOdometry(wantedPose);
			mHardwareWriter.resetDriveSensors(wantedPose);
			mCommands.driveWantedOdometryPose = null;
		}
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
		var summaryBuilder = new StringBuilder();
		summaryBuilder.append("\n===================\n");
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

	private void setDriveIdleMode(boolean isIdle) {
		// TODO: drive disabled neutral state should probably be in commands
		if (mEnabledSubsystems.contains(mDrive))
			mHardwareWriter.setDriveNeutralMode(isIdle ? NeutralMode.Coast : NeutralMode.Brake);
	}
}
