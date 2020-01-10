package com.palyrobotics.frc2020.robot;

import com.palyrobotics.frc2020.behavior.RoutineManager;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.config.dashboard.LiveGraph;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.Intake;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import com.palyrobotics.frc2020.util.commands.CommandReceiver;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2020.util.service.RobotService;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.wpilibj.TimedRobot;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Robot extends TimedRobot {

    private Commands mCommands = Commands.getInstance();
    private final RobotState mRobotState = RobotState.getInstance();
    private final Limelight mLimelight = Limelight.getInstance();
    private final RobotConfig mConfig = Configs.get(RobotConfig.class);
    private final LiveGraph mLiveGraph = LiveGraph.getInstance();
    private final OperatorInterface mOperatorInterface = OperatorInterface.getInstance();
    private final RoutineManager mRoutineManager = RoutineManager.getInstance();
    /* Subsystems */
    private final Drive mDrive = Drive.getInstance();
    private final Spinner mSpinner = Spinner.getInstance();
    private final Intake mIntake = Intake.getInstance();
    private List<Subsystem>
            mSubsystems = List.of(mDrive, mSpinner, mIntake),
            mEnabledSubsystems;
    private final HardwareUpdater mHardwareUpdater = new HardwareUpdater(mDrive, mSpinner, mIntake);
    private List<RobotService> mEnabledServices;

    private void startStage(RobotState.GamePeriod period) {
        mRobotState.gamePeriod = period;
        resetCommandsAndRoutines();
        setDriveIdleMode(false);
        CSVWriter.cleanFile();
        CSVWriter.resetTimer();
    }

    private void setDriveIdleMode(boolean isIdle) {
        mHardwareUpdater.setDriveIdleMode(isIdle ? IdleMode.kCoast : IdleMode.kBrake);
    }

    private void resetCommandsAndRoutines() {
        mCommands = Commands.resetInstance();
        mRoutineManager.reset(mCommands);
        updateSubsystemsAndHardware();
    }

    private void resetOdometry() {
        mHardwareUpdater.resetDriveSensors();
        mRobotState.resetOdometry();
    }

    private void updateSubsystemsAndHardware() {
        mHardwareUpdater.updateState(mRobotState);
        for (Subsystem subsystem : mEnabledSubsystems) {
            subsystem.update(mCommands, mRobotState);
        }
        mHardwareUpdater.updateHardware();
    }

    @Override
    public void robotInit() {
        setupSubsystemsAndServices();

        mHardwareUpdater.initHardware();

        mEnabledServices.forEach(RobotService::start);

        Configs.listen(RobotConfig.class, config -> {
            if (isDisabled()) {
                setDriveIdleMode(config.coastDriveIfDisabled);
            }
        });
    }

    @Override
    public void robotPeriodic() {
        mEnabledServices.forEach(RobotService::update);
    }

    @Override
    public void disabledInit() {
        mRobotState.gamePeriod = RobotState.GamePeriod.DISABLED;

        // TODO: ultrasonics

        resetCommandsAndRoutines();

        mLimelight.setCamMode(LimelightControlMode.CamMode.DRIVER);
        mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_OFF);

        HardwareAdapter.getInstance().getJoysticks().operatorXboxController.setRumble(false);
        setDriveIdleMode(mConfig.coastDriveIfDisabled);

        CSVWriter.write();
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void autonomousInit() {
        startStage(RobotState.GamePeriod.AUTO);
        resetOdometry();
    }

    @Override
    public void autonomousPeriodic() {
        mCommands = mRoutineManager.update(mCommands);
        updateSubsystemsAndHardware();
    }

    @Override
    public void testInit() {
        startStage(RobotState.GamePeriod.TESTING);
        resetOdometry();
    }

    @Override
    public void testPeriodic() {
        teleopPeriodic();
    }

    @Override
    public void teleopInit() {
        startStage(RobotState.GamePeriod.TELEOP);
        mCommands.setDriveTeleop();
    }

    @Override
    public void teleopPeriodic() {
        mCommands = mRoutineManager.update(mOperatorInterface.updateCommands(mCommands));
        updateSubsystemsAndHardware();
    }

    private void setupSubsystemsAndServices() {
        // TODO hard to read if unfamiliar with streams. maybe change to non-functional style
        Map<String, Supplier<RobotService>> configToService = Map.of(
                "commandReceiver", CommandReceiver::new
        );
        mEnabledServices = mConfig.enabledServices.stream()
                .map(serviceName -> configToService.get(serviceName).get())
                .collect(Collectors.toList());
        Map<String, Subsystem> configToSubsystem = mSubsystems.stream()
                .collect(Collectors.toMap(Subsystem::getConfigName, Function.identity()));
        mEnabledSubsystems = mConfig.enabledSubsystems.stream()
                .map(configToSubsystem::get)
                .collect(Collectors.toList());
        System.out.println("Enabled subsystems: ");
        mEnabledSubsystems.forEach(System.out::println);
    }
}
