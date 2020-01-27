package com.palyrobotics.frc2020.robot;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.behavior.RoutineManager;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.StringUtil;
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

	private static final String LOGGER_TAG = StringUtil.classToJsonName(Robot.class);
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

	private List<SubsystemBase> mSubsystems = List.of(mClimber, mDrive, mIndexer, mIntake, mShooter, mSpinner),
			mEnabledSubsystems;
	private List<RobotService> mServices = List.of(new CommandReceiver(), new NetworkLogger()), mEnabledServices;

	@Override
	public void robotInit() {
		String setupSummary = setupSubsystemsAndServices();

		mHardwareWriter.configureHardware();

		mEnabledServices.forEach(RobotService::start);

		Log.info(LOGGER_TAG, setupSummary);

		Configs.listen(RobotConfig.class, config -> {
			if (isDisabled()) {
				setDriveIdleMode(config.coastDriveIfDisabled);
			}
		});
	}

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
		mCommands.reset();
		mRoutineManager.clearRunningRoutines();
		updateSubsystemsAndHardware();
	}

	private void updateSubsystemsAndHardware() {
		for (SubsystemBase subsystem : mEnabledSubsystems) {
			subsystem.update(mCommands, mRobotState);
		}
		mHardwareWriter.updateHardware();
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
		mCommands.reset();
		mHardwareReader.updateState(mRobotState);
		mRoutineManager.update(mCommands, mRobotState);
		resetOdometryIfWanted();
		updateSubsystemsAndHardware();
	}

	@Override
	public void teleopPeriodic() {
		mCommands.reset();
		mHardwareReader.updateState(mRobotState);
		mOperatorInterface.updateCommands(mCommands, mRobotState);
		mRoutineManager.update(mCommands, mRobotState);
		resetOdometryIfWanted();
		updateSubsystemsAndHardware();
	}

	@Override
	public void testPeriodic() {
		teleopPeriodic();
	}

	/**
	 * Resets the pose held by the odometry from {@link #mCommands}.
	 *
	 * Must happen before updating {@link #mRobotState} or routines using odometry
	 * ({@link com.palyrobotics.frc2020.behavior.routines.drive.DrivePathRoutine})
	 * since odometry is assumed to be in a correct state there.
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
		// TODO hard to read if unfamiliar with streams. maybe change to non-functional
		// style
		Map<String, RobotService> configToService = mServices.stream()
				.collect(Collectors.toMap(RobotService::getConfigName, Function.identity()));
		mEnabledServices = mConfig.enabledServices.stream().map(configToService::get).collect(Collectors.toList());
		Map<String, SubsystemBase> configToSubsystem = mSubsystems.stream()
				.collect(Collectors.toMap(SubsystemBase::getName, Function.identity()));
		mEnabledSubsystems = mConfig.enabledSubsystems.stream().map(configToSubsystem::get)
				.collect(Collectors.toList());
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
		mHardwareWriter.setDriveNeutralMode(isIdle ? NeutralMode.Coast : NeutralMode.Brake);
	}
}
