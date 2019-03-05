package com.palyrobotics.frc2019.robot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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

	private Runnable teleopLoop;

	{
		teleopLoop = () -> {
		
			long start = System.nanoTime();

			long t1 = System.nanoTime();
			commands = mRoutineManager.update(operatorInterface.updateCommands(commands));
			long t2 = System.nanoTime();
			System.out.println("routine_manager_delta_t: " + (t2-t1)/1.0e6);
			
			t1 = System.nanoTime();
			mHardwareUpdater.updateState(robotState);
			t2 = System.nanoTime();
			System.out.println("update_state_delta_t: " + (t2-t1)/1.0e6);
			
			t1 = System.nanoTime();
			updateSubsystems();
			t2 = System.nanoTime();
			System.out.println("update_subsystems_delta_t: " + (t2-t1)/1.0e6);
			
			//Update the hardware
			t1 = System.nanoTime();
			mHardwareUpdater.updateHardware();
			t2 = System.nanoTime();
			System.out.println("update_hardware_delta_t: " + (t2-t1)/1.0e6);

	//        System.out.println(HardwareAdapter.getInstance().getPusher().pusherSpark.getAppliedOutput());
	//        System.out.println(HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.getEncoder().getPosition()/ ElevatorConstants.kElevatorRotationsPerInch);

			t1 = System.nanoTime();
			if(mWriter.getSize() > 10000) {
				mWriter.write();
			}
			t2 = System.nanoTime();
			System.out.println("writer_delta_t: " + (t2-t1)/1.0e6);

			System.out.println("loop_delta_t: " + (System.nanoTime()-start)/1.0e6);
			
			DataLogger.getInstance().logData(Level.FINE, "loop_dt", (System.nanoTime()-start)/1.0e6);
		
		};
	}

	private ScheduledExecutorService scheduler = null;

	@Override
	public void robotInit() {

		// Logger.getInstance().setFileName("3-2-Testing");
		// DataLogger.getInstance().setFileName("3-2-Testing");

		// Logger.getInstance().start();
		// DataLogger.getInstance().start();

		Logger.getInstance().logRobotThread(Level.INFO, "Start robotInit()");
		
		DashboardManager.getInstance().robotInit();

		mHardwareUpdater.initHardware();

		mElevator.clearWantedPositions();
		this.looper = new Looper();
		looper.register(mHardwareUpdater.logLoop);

		mWriter.cleanFile();

		DriveTeam.configConstants();

		Logger.getInstance().logRobotThread(Level.INFO, "End robotInit()");

	}

	

	@Override
	public void autonomousInit() {
		// Logger.getInstance().start();
		// DataLogger.getInstance().start();

		Logger.getInstance().logRobotThread(Level.INFO, "Start autoInit()");

		looper.start();

		DashboardManager.getInstance().toggleCANTable(true);
		robotState.gamePeriod = RobotState.GamePeriod.AUTO;
		mHardwareUpdater.configureHardware();

		robotState.matchStartTime = System.currentTimeMillis();

		mHardwareUpdater.updateState(robotState);
		mRoutineManager.reset(commands);
		robotState.reset(0, new RigidTransform2d());
//		commands.wantedIntakeUpDownState = Intake.UpDownState.UP;

        // Limelight LED on
        Limelight.getInstance().setLEDMode(LimelightControlMode.LedMode.FORCE_ON);

        mWriter.cleanFile();

		AutoDistances.updateAutoDistances();

		mWriter.cleanFile();

		startSubsystems();
		mHardwareUpdater.enableBrakeMode();

		Logger.getInstance().logRobotThread(Level.INFO, "End autoInit()");

	}


	@Override
	public void autonomousPeriodic() {

		long start = System.nanoTime();
		if(!this.mAutoStarted) {
			//Get the selected auto mode
			AutoModeBase mode = AutoModeSelector.getInstance().getAutoMode();

			//Prestart and run the auto mode
			mode.prestart();
			mRoutineManager.addNewRoutine(mode.getRoutine());

			this.mAutoStarted = true;
		}
		if(this.mAutoStarted) {
			commands = mRoutineManager.update(commands);
			mHardwareUpdater.updateState(robotState);
			updateSubsystems();
			mHardwareUpdater.updateHardware();
		}

        if(mWriter.getSize() > 10000) {
            mWriter.write();
		}
		
		DataLogger.getInstance().logData(Level.FINE, "loop_dt", (System.nanoTime()-start)/1.0e6);

//		System.out.println(mRoutineManager.getCurrentRoutines().contains(new DriveSensorResetRoutine(1.0)));
//		System.out.println("Position: " + Robot.getRobotState().getLatestFieldToVehicle().getValue());
	}

	@Override
	public void teleopInit() {
		// Logger.getInstance().start();
		// DataLogger.getInstance().start();

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
		
		scheduler = Executors.newScheduledThreadPool(1);
		ScheduledFuture scheduleFuture = scheduler.scheduleAtFixedRate(teleopLoop, 0, 20, TimeUnit.MILLISECONDS);

	}

	@Override
	public void teleopPeriodic() {
		//do nothing
	}

	@Override
	public void disabledInit() {

		if (scheduler != null) {
			scheduler.shutdown();
		}

		Logger.getInstance().logRobotThread(Level.INFO, "Start disabledInit()");
		Logger.getInstance().logRobotThread(Level.INFO, "Stopping logger...");

		// Logger.getInstance().cleanup();
		// DataLogger.getInstance().cleanup();

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
//		System.out.println("Left:" + HardwareAdapter.getInstance().getIntake().intakeUltrasonicLeft.getRangeInches());
//		System.out.println("Right:" + HardwareAdapter.getInstance().getIntake().intakeUltrasonicRight.getRangeInches());
//		System.out.println("Pusher:" + HardwareAdapter.getInstance().getPusher().pusherUltrasonic.getRangeInches());
		System.out.println("Arm Pot" + HardwareAdapter.getInstance().getIntake().potentiometer.get());

	}

	private void startSubsystems() {
		mShovel.start();
		mDrive.start();
		mElevator.start();
		mShooter.start();
		mPusher.start();
		mFingers.start();
//		mIntake.start();
	}

	private void updateSubsystems() {
//	    System.out.println(HardwareAdapter.getInstance().getIntake().potentiometer.get());
		mDrive.update(commands, robotState);
		mElevator.update(commands, robotState);
		mShooter.update(commands, robotState);
		mPusher.update(commands, robotState);
		mFingers.update(commands, robotState);
		mShovel.update(commands, robotState);
//		mIntake.update(commands, robotState);
	}


	private void stopSubsystems() {
		mDrive.stop();
		mElevator.stop();
		mShooter.stop();
		mPusher.stop();
		mFingers.stop();
		mShovel.stop();
//		mIntake.stop();
	}

	@Override
	public void robotPeriodic() {

	}
}
