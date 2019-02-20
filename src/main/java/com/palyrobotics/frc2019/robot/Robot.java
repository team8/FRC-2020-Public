package com.palyrobotics.frc2019.robot;

import com.palyrobotics.frc2019.auto.AutoFMS;
import com.palyrobotics.frc2019.auto.AutoModeBase;
import com.palyrobotics.frc2019.auto.AutoModeSelector;
import com.palyrobotics.frc2019.behavior.RoutineManager;
import com.palyrobotics.frc2019.config.AutoDistances;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.ElevatorConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.dashboard.DashboardManager;
import com.palyrobotics.frc2019.config.driveteam.DriveTeam;
import com.palyrobotics.frc2019.subsystems.*;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2019.util.logger.Logger;
import com.palyrobotics.frc2019.util.trajectory.RigidTransform2d;
import com.palyrobotics.frc2019.vision.Limelight;
import com.palyrobotics.frc2019.vision.LimelightControlMode;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import java.util.logging.Level;

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

	@Override
	public void robotInit() {
		DashboardManager.getInstance().robotInit();

		mHardwareUpdater.initHardware();

		mWriter.cleanFile();

		DriveTeam.configConstants();

	}

	@Override
	public void autonomousInit() {
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
	}


	@Override
	public void autonomousPeriodic() {
		if(AutoFMS.isFMSDataAvailable() && !this.mAutoStarted) {
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

//		System.out.println(mRoutineManager.getCurrentRoutines().contains(new DriveSensorResetRoutine(1.0)));
//		System.out.println("Position: " + Robot.getRobotState().getLatestFieldToVehicle().getValue());
	}

	@Override
	public void teleopInit() {
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

        // Limelight LED on
        Limelight.getInstance().setLEDMode(LimelightControlMode.LedMode.FORCE_ON);

        Logger.getInstance().logRobotThread(Level.INFO, "End teleopInit()");
	}

	@Override
	public void teleopPeriodic() {
		commands = mRoutineManager.update(operatorInterface.updateCommands(commands));
		mHardwareUpdater.updateState(robotState);
		updateSubsystems();

		//Update the hardware
		mHardwareUpdater.updateHardware();
        System.out.println(HardwareAdapter.getInstance().getPusher().pusherSpark.getAppliedOutput());
//        System.out.println(HardwareAdapter.getInstance().getElevator().elevatorMasterSpark.getEncoder().getPosition()/ ElevatorConstants.kElevatorRotationsPerInch);

        if(mWriter.getSize() > 10000) {
            mWriter.write();
        }

	}

	@Override
	public void disabledInit() {
		mAutoStarted = false;

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

        // Limelight LED off
        Limelight.getInstance().setLEDMode(LimelightControlMode.LedMode.FORCE_OFF);
		HardwareAdapter.getInstance().getJoysticks().operatorXboxController.setRumble(false);

        mWriter.write();

		//Manually run garbage collector
		System.gc();
	}

	@Override
	public void disabledPeriodic() {

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

	}
}
