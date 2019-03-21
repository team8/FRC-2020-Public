package com.palyrobotics.frc2019.robot;

import java.util.logging.Level;

import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.auto.AutoModeSelector;
import com.palyrobotics.frc2019.behavior.RoutineManager;
import com.palyrobotics.frc2019.config.AutoDistances;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.dashboard.DashboardManager;
import com.palyrobotics.frc2019.config.driveteam.DriveTeam;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.Elevator;
import com.palyrobotics.frc2019.subsystems.Fingers;
import com.palyrobotics.frc2019.subsystems.Intake;
import com.palyrobotics.frc2019.subsystems.Pusher;
import com.palyrobotics.frc2019.subsystems.Shooter;
import com.palyrobotics.frc2019.subsystems.Shovel;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2019.util.logger.DataLogger;
import com.palyrobotics.frc2019.util.logger.Logger;
import com.palyrobotics.frc2019.util.loops.Looper;
import com.palyrobotics.frc2019.util.trajectory.RigidTransform2d;
import com.palyrobotics.frc2019.vision.Limelight;
import com.palyrobotics.frc2019.vision.LimelightControlMode;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {
	//Instantiate singleton classes
	private static RobotState robotState = RobotState.getInstance();

	public static RobotState getRobotState() {
		return robotState;
	}

	//Single instance to be passed around
	private static Commands commands = Commands.getInstance();

	public static Commands getCommands() {
		return commands;
	}

	private OperatorInterface operatorInterface = OperatorInterface.getInstance();
	private RoutineManager mRoutineManager = RoutineManager.getInstance();

	private CSVWriter mWriter = CSVWriter.getInstance();

	//Subsystem controllers
	private Drive mDrive = Drive.getInstance();
	private Elevator mElevator = Elevator.getInstance();
	private Shovel mShovel = Shovel.getInstance();
	private Shooter mShooter = Shooter.getInstance();
	private Pusher mPusher = Pusher.getInstance();
	private Fingers mFingers = Fingers.getInstance();
    private Intake mIntake = Intake.getInstance();

	//Hardware Updater
	private HardwareUpdater mHardwareUpdater = new HardwareUpdater(mDrive, mElevator, mShooter, mPusher, mShovel, mFingers, mIntake);

	// Started boolean for if auto has been started.
	private boolean mAutoStarted = false;

	private int disabledCycles;

	public Looper looper;
	
	@Override
	public void robotInit() {

		Logger.getInstance().setFileName("3-2-Testing");
		DataLogger.getInstance().setFileName("3-2-Testing");

		Logger.getInstance().start();
		DataLogger.getInstance().start();

		Logger.getInstance().logRobotThread(Level.INFO, "Start robotInit()");
		
		DashboardManager.getInstance().robotInit();

		mHardwareUpdater.initHardware();

		mElevator.clearWantedPositions();
		this.looper = new Looper();
//		looper.register(mHardwareUpdater.logLoop);

		mWriter.cleanFile();

		DriveTeam.configConstants();

		Logger.getInstance().logRobotThread(Level.INFO, "End robotInit()");

	}

	

	@Override
	public void autonomousInit() {

		 teleopInit();

// 		Logger.getInstance().start();
// 		DataLogger.getInstance().start();
//
// 		Logger.getInstance().logRobotThread(Level.INFO, "Start autoInit()");
//
// 		looper.start();
//
// 		DashboardManager.getInstance().toggleCANTable(true);
// 		robotState.gamePeriod = RobotState.GamePeriod.AUTO;
//
// 		robotState.matchStartTime = System.currentTimeMillis();
//
// 		mHardwareUpdater.updateState(robotState);
// 		mRoutineManager.reset(commands);
// 		robotState.reset(0, new RigidTransform2d());
// //		commands.wantedIntakeUpDownState = Intake.UpDownState.UP;
//
//         // Limelight LED on
//         Limelight.getInstance().setLEDMode(LimelightControlMode.LedMode.FORCE_ON);
//
//         mWriter.cleanFile();
//
// 		AutoDistances.updateAutoDistances();
//
// 		mWriter.cleanFile();
//
// 		startSubsystems();
// 		mHardwareUpdater.enableBrakeMode();
//
// 		Logger.getInstance().logRobotThread(Level.INFO, "End autoInit()");

	}


	@Override
	public void autonomousPeriodic() {

		teleopPeriodic();

//		 long start = System.nanoTime();
//		 if(!this.mAutoStarted) {
//		 	//Get the selected auto mode
//		 	AutoModeBase mode = AutoModeSelector.getInstance().getAutoMode();
//
//		 	//Prestart and run the auto mode
//		 	mode.prestart();
//		 	mRoutineManager.addNewRoutine(mode.getRoutine());
//
//		 	this.mAutoStarted = true;
//		 }
//		 if(this.mAutoStarted) {
//			 commands = mRoutineManager.update(commands);
//			 mHardwareUpdater.updateState(robotState);
//			 updateSubsystems();
//			 mHardwareUpdater.updateHardware();
//		 }
//
//         if(mWriter.getSize() > 10000) {
//             mWriter.write();
//		 }
//
//		 DataLogger.getInstance().logData(Level.FINE, "loop_dt", (System.nanoTime()-start)/1.0e6);
//		 DataLogger.getInstance().cycle();
	}

	@Override
	public void teleopInit() {
		Logger.getInstance().start();
		DataLogger.getInstance().start();

		Logger.getInstance().logRobotThread(Level.INFO, "Start teleopInit()");

		looper.start();

		robotState.gamePeriod = RobotState.GamePeriod.TELEOP;
		robotState.reset(0.0, new RigidTransform2d());
		mHardwareUpdater.updateState(robotState);
		mHardwareUpdater.updateHardware();
		mRoutineManager.reset(commands);
		DashboardManager.getInstance().toggleCANTable(true);
		commands.wantedDriveState = Drive.DriveState.CHEZY; //switch to chezy after auto ends
		commands.wantedGearboxState = Elevator.GearboxState.ELEVATOR;
		commands = operatorInterface.updateCommands(commands);
        mWriter.cleanFile();
		startSubsystems();
		mHardwareUpdater.enableBrakeMode();
		robotState.reset(0, new RigidTransform2d());
		robotState.matchStartTime = System.currentTimeMillis();

		// Set limelight to driver camera mode - redundancy for testing purposes
		Limelight.getInstance().setCamMode(LimelightControlMode.CamMode.DRIVER);

        Logger.getInstance().logRobotThread(Level.INFO, "End teleopInit()");
		
	}

	@Override
	public void teleopPeriodic() {
		long start = System.nanoTime();
		commands = mRoutineManager.update(operatorInterface.updateCommands(commands));
		
		mHardwareUpdater.updateState(robotState);
		
		updateSubsystems();
		
		//Update the hardware
		mHardwareUpdater.updateHardware();

		if(mWriter.getSize() > 10000) {
			mWriter.write();
		}
		
		DataLogger.getInstance().logData(Level.FINE, "loop_dt", (System.nanoTime()-start)/1.0e6);
		DataLogger.getInstance().cycle();

		System.out.println("Limelight zdist: " + Limelight.getInstance().getCorrectedEstimatedDistanceZ());

	}

	@Override
	public void disabledInit() {

		Logger.getInstance().logRobotThread(Level.INFO, "Start disabledInit()");
		Logger.getInstance().logRobotThread(Level.INFO, "Stopping logger...");

		Logger.getInstance().cleanup();
		DataLogger.getInstance().cleanup();

		mAutoStarted = false;

		looper.stop();

		robotState.reset(0, new RigidTransform2d());
		//Stops updating routines
		mRoutineManager.reset(commands);
		//Creates a new Commands instance in place of the old one
		Commands.reset();
		commands = Commands.getInstance();

		robotState.gamePeriod = RobotState.GamePeriod.DISABLED;
		//Stop controllers
		mDrive.setNeutral();
		stopSubsystems();

        // Set Limelight to vision pipeline to enable pit testing
		Limelight.getInstance().setCamMode(LimelightControlMode.CamMode.VISION);
        Limelight.getInstance().setLEDMode(LimelightControlMode.LedMode.CURRENT_PIPELINE_MODE);
		HardwareAdapter.getInstance().getJoysticks().operatorXboxController.setRumble(false);
		mHardwareUpdater.disableBrakeMode();

		mWriter.write();

		//Manually run garbage collector
		System.gc();
	}

	@Override
	public void disabledPeriodic() {
		System.out.println("Pot: " + HardwareAdapter.getInstance().getIntake().potentiometer.get());
        System.out.println("Pusher Ultrasonic: " + HardwareAdapter.getInstance().getPusher().pusherUltrasonic.getRangeInches());
        System.out.println("Left Ultrasonic: " + HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft.getRangeInches());
        System.out.println("Right Ultrasonic: " + HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight.getRangeInches());
//		System.out.println("PusherBackup Ultrasonic: " + HardwareAdapter.getInstance().getPusher().pusherSecondaryUltrasonic.getRangeInches());

        System.out.println();
        System.out.println();
	}

	private void startSubsystems() {
		mShovel.start();
		mDrive.start();
		mElevator.start();
		mShooter.start();
		mPusher.start();
		mFingers.start();
		mIntake.start();
	}

	private void updateSubsystems() {
		mDrive.update(commands, robotState);
		mElevator.update(commands, robotState);
		mShooter.update(commands, robotState);
		mPusher.update(commands, robotState);
		mFingers.update(commands, robotState);
		mShovel.update(commands, robotState);
		mIntake.update(commands, robotState);
	}


	private void stopSubsystems() {
		mDrive.stop();
		mElevator.stop();
		mShooter.stop();
		mPusher.stop();
		mFingers.stop();
		mShovel.stop();
		mIntake.stop();
	}

	@Override
	public void robotPeriodic() {
		// System.out.println("intake_enc: " + HardwareAdapter.getInstance().getIntake().intakeMasterSpark.getEncoder().getPosition());
		// System.out.println("intake_pot: " + HardwareAdapter.getInstance().getIntake().potentiometer.get());
		// System.out.println("left ultrasonic: " + HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft.getRangeInches());
		// System.out.println("right ultrasonic: " + HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight.getRangeInches());
	}
}
