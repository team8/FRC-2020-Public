package com.palyrobotics.frc2019.robot;

import com.palyrobotics.frc2019.behavior.RoutineManager;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.configv2.RobotConfig;
import com.palyrobotics.frc2019.config.dashboard.DashboardManager;
import com.palyrobotics.frc2019.config.driveteam.DriveTeam;
import com.palyrobotics.frc2019.subsystems.*;
import com.palyrobotics.frc2019.util.commands.CommandReceiver;
import com.palyrobotics.frc2019.util.configv2.Configs;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2019.util.service.RobotService;
import com.palyrobotics.frc2019.util.trajectory.RigidTransform2d;
import com.palyrobotics.frc2019.vision.Limelight;
import com.palyrobotics.frc2019.vision.LimelightControlMode;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Robot extends TimedRobot {

    private static final RobotState sRobotState = RobotState.getInstance();
    private final Limelight mLimelight = Limelight.getInstance();

    public static RobotState getRobotState() {
        return sRobotState;
    }

    private static Commands mCommands = Commands.getInstance();

    public static Commands getCommands() {
        return mCommands;
    }

    private OperatorInterface operatorInterface = OperatorInterface.getInstance();
    private RoutineManager mRoutineManager = RoutineManager.getInstance();

    /* Subsystems */
    private Drive mDrive = Drive.getInstance();
    private Elevator mElevator = Elevator.getInstance();
    private Shovel mShovel = Shovel.getInstance();
    private Shooter mShooter = Shooter.getInstance();
    private Pusher mPusher = Pusher.getsInstance();
    private Fingers mFingers = Fingers.getInstance();
    private Intake mIntake = Intake.getInstance();
    private List<Subsystem>
            mSubsystems = List.of(mDrive, mElevator, mShooter, mPusher, mShovel, mFingers, mIntake),
            mEnabledSubsystems;

    private HardwareUpdater mHardwareUpdater = new HardwareUpdater(mDrive, mElevator, mShooter, mPusher, mShovel, mFingers, mIntake);

    private List<RobotService> mEnabledServices;

    @Override
    public void robotInit() {

//        Logger.getInstance().setFileName("3-2-Testing");
//        DataLogger.getInstance().setFileName("3-2-Testing");
//
//        Logger.getInstance().start();
//        DataLogger.getInstance().start();
//
//        Logger.getInstance().logRobotThread(Level.INFO, "Start robotInit()");

        setupSubsystemsAndServices();

        mHardwareUpdater.initHardware();

        mElevator.resetWantedPosition();

        CSVWriter.cleanFile();

        DriveTeam.configConstants();

        mEnabledServices.forEach(RobotService::start);

        if (RobotBase.isSimulation()) sRobotState.matchStartTimeMs = System.currentTimeMillis();

//        Logger.getInstance().logRobotThread(Level.INFO, "End robotInit()");
    }

    @Override
    public void autonomousInit() {

        teleopInit();

//        if(robotState.cancelAuto) {
//            robotState.gamePeriod = RobotState.GamePeriod.TELEOP;
//            teleopInit();
//            breakoutTeleopInitCalled = true;
//        } else {
//
//            Logger.getInstance().start();
//            DataLogger.getInstance().start();
//
//            Logger.getInstance().logRobotThread(Level.INFO, "Start autoInit()");
//
//            looper.start();
//
//            DashboardManager.getInstance().toggleCANTable(true);
//            robotState.gamePeriod = RobotState.GamePeriod.AUTO;
//
//            robotState.matchStartTime = System.currentTimeMillis();
//
//            mHardwareUpdater.updateState(robotState);
//            mRoutineManager.reset(commands);
//            robotState.reset(0, new RigidTransform2d());
//            //		commands.wantedIntakeUpDownState = Intake.UpDownState.UP;
//
//            // Limelight LED on
////         mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_ON);
//
//            mWriter.cleanFile();
//
//            AutoDistances.updateAutoDistances();
//
//            mWriter.cleanFile();
//
//            startSubsystems();
//            mHardwareUpdater.enableBrakeMode();
//
//            Logger.getInstance().logRobotThread(Level.INFO, "End autoInit()");
//        }

    }

    @Override
    public void autonomousPeriodic() {

//        System.out.println("Left Encoder: " + robotState.drivePose.leftEnc);
//        System.out.println("Right Encoder: " + robotState.drivePose.rightEnc);
//        System.out.println("Gyro Heading: " + robotState.drivePose.heading);

        teleopPeriodic();

//         if(robotState.cancelAuto) {
//			 robotState.gamePeriod = RobotState.GamePeriod.TELEOP;
//			 if(breakoutTeleopInitCalled) {
//			 	teleopInit();
//			 	breakoutTeleopInitCalled = true;
//			 }
//			 teleopPeriodic();
//			 mRoutineManager.reset(commands);
//			 System.out.println("CANCELING AUTO, MOVING TO TELE");
//         } else {
//             long start = System.nanoTime();
//             if (!this.mAutoStarted) {
//                 //Get the selected auto mode
//                 AutoModeBase mode = AutoModeSelector.getInstance().getAutoMode();
//
//                 //Prestart and run the auto mode
//                 mode.prestart();
//                 mRoutineManager.addNewRoutine(mode.getRoutine());
//
//                 this.mAutoStarted = true;
//             }
//             if (this.mAutoStarted) {
//                 commands = mRoutineManager.update(commands);
//                 mHardwareUpdater.updateState(robotState);
//                 updateSubsystems();
//                 mHardwareUpdater.updateHardware();
//             }
//
//             if (mWriter.getSize() > 10000) {
//                 mWriter.write();
//             }
//
//             DataLogger.getInstance().logData(Level.FINE, "loop_dt", (System.nanoTime() - start) / 1.0e6);
//             DataLogger.getInstance().cycle();
//         }
    }

    @Override
    public void teleopInit() {
//		System.out.println("TELEOP STARTED");
//        Logger.getInstance().start();
//        DataLogger.getInstance().start();
//
//        Logger.getInstance().logRobotThread(Level.INFO, "Start teleopInit()");

        sRobotState.gamePeriod = RobotState.GamePeriod.TELEOP;
        sRobotState.reset(0.0, new RigidTransform2d());
        mHardwareUpdater.updateState(sRobotState);
        mRoutineManager.reset(mCommands);
        DashboardManager.getInstance().toggleCANTable(true);
        mCommands.wantedDriveState = Drive.DriveState.CHEZY; // Switch to chezy after auto ends
        mCommands = operatorInterface.updateCommands(mCommands);
        CSVWriter.cleanFile();
        mEnabledSubsystems.forEach(Subsystem::start);
        mHardwareUpdater.updateHardware();
        mHardwareUpdater.enableBrakeMode();
        sRobotState.reset(0.0, new RigidTransform2d());
        sRobotState.matchStartTimeMs = System.currentTimeMillis();

        // Set limelight to driver camera mode - redundancy for testing purposes
        mLimelight.setCamMode(LimelightControlMode.CamMode.DRIVER);

//        Logger.getInstance().logRobotThread(Level.INFO, "End teleopInit()");
    }

    @Override
    public void testInit() {
//        System.out.println(HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getPosition());
        System.out.printf("Potentiometer Arm: %s%n", HardwareAdapter.getInstance().getIntake().potentiometer.get());
    }

    @Override
    public void testPeriodic() {

    }

    @Override
    public void teleopPeriodic() {
        mCommands = mRoutineManager.update(operatorInterface.updateCommands(mCommands));
        mHardwareUpdater.updateState(sRobotState);
        for (Subsystem subsystem : mEnabledSubsystems) {
            subsystem.update(mCommands, sRobotState);
        }
        mHardwareUpdater.updateHardware();
    }

    @Override
    public void disabledInit() {
//        Logger.getInstance().logRobotThread(Level.INFO, "Start disabledInit()");
//        Logger.getInstance().logRobotThread(Level.INFO, "Stopping logger...");
//
//        Logger.getInstance().cleanup();
//        DataLogger.getInstance().cleanup();

        sRobotState.reset(0.0, new RigidTransform2d());
        // Stops updating routines
        mRoutineManager.reset(mCommands);

        // Creates a new Commands instance in place of the old one
        Commands.reset();
        mCommands = Commands.getInstance();

        sRobotState.gamePeriod = RobotState.GamePeriod.DISABLED;
        // Stop controllers
        mDrive.setNeutral();
        mEnabledSubsystems.forEach(Subsystem::stop);

        // Set Limelight to vision pipeline to enable pit testing
        mLimelight.setCamMode(LimelightControlMode.CamMode.VISION);
        mLimelight.setLEDMode(LimelightControlMode.LedMode.CURRENT_PIPELINE_MODE);
        HardwareAdapter.getInstance().getJoysticks().operatorXboxController.setRumble(false);
        mHardwareUpdater.disableBrakeMode();

        CSVWriter.write();

        // Manually run garbage collector
        System.gc();
    }

    @Override
    public void disabledPeriodic() {
//		System.out.println("Pot: " + HardwareAdapter.getInstance().getIntake().potentiometer.get());
//        System.out.println("Pusher Ultrasonic: " + HardwareAdapter.getInstance().getPusher().pusherUltrasonic.getRangeInches());
//        System.out.println("Left Ultrasonic: " + HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft.getRangeInches());
//        System.out.println("Right Ultrasonic: " + HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight.getRangeInches());
//		System.out.println("PusherBackup Ultrasonic: " + HardwareAdapter.getInstance().getPusher().pusherSecondaryUltrasonic.getRangeInches());

//		System.out.println(HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getPosition());

//        System.out.println();
//        System.out.println();
    }

    @Override
    public void robotPeriodic() {
        mEnabledServices.forEach(RobotService::update);

        // System.out.println("intake_enc: " + HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getPosition());
        // System.out.println("intake_pot: " + HardwareAdapter.getInstance().getIntake().potentiometer.get());
        // System.out.println("left ultrasonic: " + HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft.getRangeInches());
        // System.out.println("right ultrasonic: " + HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight.getRangeInches());

    }

    private void setupSubsystemsAndServices() {
        // TODO meh
        RobotConfig services = Configs.get(RobotConfig.class);
        Map<String, Supplier<RobotService>> configToService = Map.of(
                "commandReceiver", CommandReceiver::new,
                "dashboardManager", DashboardManager::new
        );
        mEnabledServices = services.enabledServices.stream()
                .map(serviceName -> configToService.get(serviceName).get())
                .collect(Collectors.toList());
        Map<String, Subsystem> configToSubsystem = mSubsystems.stream()
                .collect(Collectors.toMap(Subsystem::getConfigName, Function.identity()));
        mEnabledSubsystems = services.enabledSubsystems.stream()
                .map(configToSubsystem::get)
                .collect(Collectors.toList());
        mEnabledSubsystems.forEach(System.out::println);
    }
}
