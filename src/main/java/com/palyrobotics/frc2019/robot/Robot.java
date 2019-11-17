package com.palyrobotics.frc2019.robot;

import com.palyrobotics.frc2019.behavior.RoutineManager;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotConfig;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.dashboard.LiveGraph;
import com.palyrobotics.frc2019.config.driveteam.DriveTeam;
import com.palyrobotics.frc2019.subsystems.*;
import com.palyrobotics.frc2019.util.commands.CommandReceiver;
import com.palyrobotics.frc2019.util.config.Configs;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2019.util.service.RobotService;
import com.palyrobotics.frc2019.vision.Limelight;
import com.palyrobotics.frc2019.vision.LimelightControlMode;
import com.revrobotics.CANSparkMax.IdleMode;
import edu.wpi.first.wpilibj.TimedRobot;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Robot extends TimedRobot {

    private static final RobotState sRobotState = RobotState.getInstance();
    private static Commands sCommands = Commands.getInstance();
    private final Limelight mLimelight = Limelight.getInstance();
    private final RobotConfig mConfig = Configs.get(RobotConfig.class);
    private LiveGraph mLiveGraph = LiveGraph.getInstance();
    private OperatorInterface mOperatorInterface = OperatorInterface.getInstance();
    private RoutineManager mRoutineManager = RoutineManager.getInstance();
    /* Subsystems */
    private Drive mDrive = Drive.getInstance();
    private Elevator mElevator = Elevator.getInstance();
    private Shooter mShooter = Shooter.getInstance();
    private Pusher mPusher = Pusher.getsInstance();
    private Fingers mFingers = Fingers.getInstance();
    private Intake mIntake = Intake.getInstance();
    private List<Subsystem>
            mSubsystems = List.of(mDrive, mElevator, mShooter, mPusher, mFingers, mIntake),
            mEnabledSubsystems;
    private HardwareUpdater mHardwareUpdater = new HardwareUpdater(mDrive, mElevator, mShooter, mPusher, mFingers, mIntake);
    private List<RobotService> mEnabledServices;

    public static RobotState getRobotState() {
        return sRobotState;
    }

    public static Commands getCommands() {
        return sCommands;
    }

    @Override
    public void robotInit() {
        setupSubsystemsAndServices();

        mHardwareUpdater.initHardware();

        DriveTeam.configConstants();

        mEnabledServices.forEach(RobotService::start);

        Configs.listen(RobotConfig.class, config -> setIdleModes());
    }

    private void setIdleModes() {
        Function<Boolean, IdleMode> f = isEnabled()
                ? c -> IdleMode.kBrake // Always brake if enabled
                : c -> c ? IdleMode.kCoast : IdleMode.kBrake; // Set to config when disabled
        mHardwareUpdater.setDriveIdleMode(f.apply(mConfig.coastDriveIfDisabled));
        mHardwareUpdater.setElevatorIdleMode(f.apply(mConfig.coastElevatorIfDisabled));
        mHardwareUpdater.setArmIdleMode(f.apply(mConfig.coastArmIfDisabled));
    }

    private void stageInit(RobotState.GamePeriod period)
    {
        sRobotState.gamePeriod = period;
        mRoutineManager.reset(sCommands);
        mEnabledSubsystems.forEach(Subsystem::start);
        mHardwareUpdater.updateHardware();
    }

    @Override
    public void autonomousInit() {
        stageInit(RobotState.GamePeriod.AUTO);
    }

    @Override
    public void autonomousPeriodic() {
        teleopPeriodic();
    }

    @Override
    public void testInit() {
        stageInit(RobotState.GamePeriod.TESTING);
    }

    @Override
    public void testPeriodic() {
        mLiveGraph.add("Left Ultrasonic", HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft.getRangeInches());
        mLiveGraph.add("Right Ultrasonic", HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight.getRangeInches());
        mLiveGraph.add("Pusher Ultrasonic", HardwareAdapter.getInstance().getPusher().pusherUltrasonic.getRangeInches());
        mLiveGraph.add("Arm Potentiometer", HardwareAdapter.getInstance().getIntake().potentiometer.get());
    }

    @Override
    public void teleopInit() {
        stageInit(RobotState.GamePeriod.TELEOP);
        sCommands.wantedDriveState = Drive.DriveState.CHEZY; // Switch to chezy after auto ends
        setIdleModes();

        // Set limelight to driver camera mode - redundancy for testing purposes
        mLimelight.setCamMode(LimelightControlMode.CamMode.DRIVER);

        CSVWriter.cleanFile();
    }

    @Override
    public void teleopPeriodic() {
        sCommands = mRoutineManager.update(mOperatorInterface.updateCommands(sCommands));
        mHardwareUpdater.updateState(sRobotState);
        for (Subsystem subsystem : mEnabledSubsystems) {
            subsystem.update(sCommands, sRobotState);
        }
        mHardwareUpdater.updateHardware();
    }

    @Override
    public void robotPeriodic() {
        mEnabledServices.forEach(RobotService::update);
    }

    @Override
    public void disabledInit() {
        sRobotState.gamePeriod = RobotState.GamePeriod.DISABLED;
        sRobotState.resetOdometry();
        sRobotState.resetUltrasonics();

        // Clear the commands
        sCommands = Commands.reset();

        // Stop subsystems and reset their states
        mEnabledSubsystems.forEach(Subsystem::stop);
        mHardwareUpdater.updateHardware();

        mLimelight.setCamMode(LimelightControlMode.CamMode.DRIVER);
        mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_OFF);

        HardwareAdapter.getInstance().getJoysticks().operatorXboxController.setRumble(false);
        setIdleModes();

        CSVWriter.write();

        // Manually run garbage collector
        System.gc();
    }

    @Override
    public void disabledPeriodic() {

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
