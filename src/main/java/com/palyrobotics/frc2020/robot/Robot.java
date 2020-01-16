package com.palyrobotics.frc2020.robot;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.behavior.RoutineManager;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.dashboard.LiveGraph;
import com.palyrobotics.frc2020.subsystems.*;
import com.palyrobotics.frc2020.util.StringUtil;
import com.palyrobotics.frc2020.util.commands.CommandReceiver;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2020.util.service.NetworkLogger;
import com.palyrobotics.frc2020.util.service.RobotService;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {

	private static final String LOGGER_TAG = StringUtil.classToJsonName(Robot.class);
	private final RobotState mRobotState = RobotState.getInstance();
	private final Limelight mLimelight = Limelight.getInstance();
	private final RobotConfig mConfig = Configs.get(RobotConfig.class);
	private final LiveGraph mLiveGraph = LiveGraph.getInstance();
	private final OperatorInterface mOperatorInterface = OperatorInterface.getInstance();
	private final RoutineManager mRoutineManager = RoutineManager.getInstance();
	/* Subsystems */
	private final Drive mDrive = Drive.getInstance();
	private final Spinner mSpinner = Spinner.getInstance();
	private final Indexer mIndexer = Indexer.getInstance();
	private final Intake mIntake = Intake.getInstance();
	private final HardwareReader mHardwareReader = HardwareReader.getInstance();
	private final HardwareWriter mHardwareWriter = HardwareWriter.getInstance();
	private Commands mCommands = Commands.getInstance();
	private List<Subsystem> mSubsystems = List.of(mDrive, mSpinner, mIndexer, mIntake), mEnabledSubsystems;
	private List<RobotService> mServices = List.of(new CommandReceiver(), new NetworkLogger()), mEnabledServices;

	@Override
	public void robotInit() {
		setupSubsystemsAndServices();

		mHardwareWriter.configureHardware();

		mEnabledServices.forEach(RobotService::start);

		Configs.listen(RobotConfig.class, config -> {
			if (isDisabled()) {
				setDriveIdleMode(config.coastDriveIfDisabled);
			}
		});
	}

	@Override
	public void disabledInit() {
		mRobotState.gamePeriod = RobotState.GamePeriod.DISABLED;

		// TODO: ultrasonics

		resetCommandsAndRoutines();

		mLimelight.setCamMode(LimelightControlMode.CamMode.DRIVER);
		mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_OFF);

		HardwareAdapter.Joysticks.getInstance().operatorXboxController.setRumble(false);
		setDriveIdleMode(mConfig.coastDriveIfDisabled);

		CSVWriter.write();
	}

	private void resetCommandsAndRoutines() {
		mCommands = Commands.resetInstance();
		mRoutineManager.clearRunningRoutines();
		updateSubsystemsAndHardware();
	}

	private void updateSubsystemsAndHardware() {
		mHardwareReader.updateState(mRobotState);
		for (Subsystem subsystem : mEnabledSubsystems) {
			subsystem.update(mCommands, mRobotState);
		}
		mHardwareWriter.updateHardware();
	}

	@Override
	public void autonomousInit() {
		startStage(RobotState.GamePeriod.AUTO);
		resetOdometry();
	}

	private void startStage(RobotState.GamePeriod period) {
		mRobotState.gamePeriod = period;
		resetCommandsAndRoutines();
		setDriveIdleMode(false);
		CSVWriter.cleanFile();
		CSVWriter.resetTimer();
	}

	private void resetOdometry() {
		mHardwareWriter.resetDriveSensors();
		mRobotState.resetOdometry();
	}

	@Override
	public void teleopInit() {
		startStage(RobotState.GamePeriod.TELEOP);
		mCommands.setDriveTeleop();
	}

	@Override
	public void testInit() {
		startStage(RobotState.GamePeriod.TESTING);
		resetOdometry();
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
		mRoutineManager.update(mCommands);
		updateSubsystemsAndHardware();
	}

	@Override
	public void teleopPeriodic() {
		mRoutineManager.update(mCommands);
		mOperatorInterface.updateCommands(mCommands);
		updateSubsystemsAndHardware();
	}

	@Override
	public void testPeriodic() {
		teleopPeriodic();
	}

	private void setupSubsystemsAndServices() {
		// TODO hard to read if unfamiliar with streams. maybe change to non-functional
		// style
		Map<String, RobotService> configToService = mServices.stream()
				.collect(Collectors.toMap(RobotService::getConfigName, Function.identity()));
		mEnabledServices = mConfig.enabledServices.stream().map(configToService::get).collect(Collectors.toList());
		Map<String, Subsystem> configToSubsystem = mSubsystems.stream()
				.collect(Collectors.toMap(Subsystem::getName, Function.identity()));
		mEnabledSubsystems = mConfig.enabledSubsystems.stream().map(configToSubsystem::get)
				.collect(Collectors.toList());
		var builder = new StringBuilder();
		builder.append("\n===================\n");
		builder.append("Enabled subsystems:\n");
		builder.append("-------------------\n");
		for (Subsystem enabledSubsystem : mEnabledSubsystems) {
			builder.append(enabledSubsystem.getName()).append("\n");
		}
		builder.append("=================\n");
		builder.append("Enabled services:\n");
		builder.append("-----------------\n");
		for (RobotService enabledService : mEnabledServices) {
			builder.append(enabledService.getConfigName()).append("\n");
		}
		Log.info(LOGGER_TAG, builder.toString());
	}

	private void setDriveIdleMode(boolean isIdle) {
		mHardwareWriter.setDriveIdleMode(isIdle ? IdleMode.kCoast : IdleMode.kBrake);
	}
}
