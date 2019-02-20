package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.dashboard.DashboardManager;
import com.palyrobotics.frc2019.config.dashboard.DashboardValue;
import com.palyrobotics.frc2019.subsystems.controllers.*;
import com.palyrobotics.frc2019.util.*;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2019.util.logger.Logger;
import com.palyrobotics.frc2019.util.trajectory.Path;

import java.util.logging.Level;

/**
 * Represents the drivetrain Uses controllers or cheesydrivehelper/proportionaldrivehelper to calculate DriveSignal
 * 
 * @author Not Nihar
 */
public class Drive extends Subsystem {
	private static Drive instance = new Drive();

	public static Drive getInstance() {
		return instance;
	}

	/**
	 * <h1>Various control states for the drivetrain</h1>
	 * 
	 * <p>
	 * 
	 * {@code CHEZY} creates a {@link CheesyDriveHelper} drive with joystick values.
	 * {@code OFF_BOARD_CONTROLLER} creates a CANTalon offboard loop.
	 * {@code ON_BOARD_CONTROLLER} makes a control loop calculated in code with an open loop. 
	 * It uses drive outputs passed in through commands.
	 * {@code NEUTRAL} does nothing.
	 */
	public enum DriveState {
		CHEZY, OFF_BOARD_CONTROLLER, ON_BOARD_CONTROLLER, OPEN_LOOP, NEUTRAL, VISION_ASSIST
	}

	private DriveState mState = DriveState.NEUTRAL;

	//Helper class to calculate teleop output
	private CheesyDriveHelper mCDH = new CheesyDriveHelper();

	private VisionDriveHelper mVDH = new VisionDriveHelper();

	private Drive.DriveController mController = null;
	//Used for off board controllers to be called only once
	private boolean newController = false;

	//Encoder DPP
	private final double kWheelbaseWidth; //Get from CAD
	private final double kTurnSlipFactor; //Measure empirically

	//Cache poses to not be allocating at 200Hz
	private Pose mCachedPose = new Pose(0, 0, 0, 0, 0, 0, 0, 0);
	//Cached robot state, updated by looper
	private RobotState mCachedRobotState;
	//Stores output
	private SparkSignal mSignal = SparkSignal.getNeutralSignal();

	private DashboardValue motors;

	private DashboardValue leftEncoder;
	private DashboardValue rightEncoder;

	private CSVWriter mWriter = CSVWriter.getInstance();

	protected Drive() {
		super("Drive");
		kWheelbaseWidth = 0;
		kTurnSlipFactor = 0;

		motors = new DashboardValue("driveSpeedUpdate");

		leftEncoder = new DashboardValue("leftdriveencoder");
		rightEncoder = new DashboardValue("rightdriveencoder");
	}

	/**
	 * @return DriveSignal
	 */
	public SparkSignal getDriveSignal() {
		return mSignal;
	}

	@Override
	public void start() {
		setNeutral();
	}

	/**
	 * <h1>Updates the drivetrain and its {@link DriveSignal}</h1>
	 * 
	 * <br>
	 * Contains a state machine that switches. based on {@link DriveState} and updates the
	 * {@link DriveController} with the current {@link RobotState}. The controllers then output 
	 * a {@link SparkSignal}, which is then used to {@link Drive#setDriveOutputs}.
	 * <br><br>
	 * 
	 * States and behavior:
	 *	<ul>
	 * 		<li>
	 * 			{@link DriveState#CHEZY}: 
	 * 			Sets drive outputs using a {@link DriveSignal} from a {@link CheesyDriveHelper}.
	 * 		</li>	
	 * 		<li>
	 * 			{@link DriveState#OFF_BOARD_CONTROLLER}:
	 * 			Updates the {@link DriveController}, but if no controller exists prints a warning and sets the 
	 * 			{@link Commands#wantedDriveState} to {@link DriveState#NEUTRAL}.
	 * 		</li>	
	 * 		<li>
	 * 			{@link DriveState#ON_BOARD_CONTROLLER}:
	 * 			See {@link DriveState#OFF_BOARD_CONTROLLER}.
	 * 		</li>	
	 * 		<li>
	 * 			{@link DriveState#OPEN_LOOP}:
	 * 			Directly accesses {@link Commands#robotSetpoints} for the {@code DrivePowerSetpoint}.
	 * 		</li>
	 * 		<li>
	 * 			{@link DriveState#NEUTRAL}:
	 * 			Sets drive outputs to {@link DriveSignal#getNeutralSignal()}. Will also 
	 * 			{@link Drive#resetController} if in a new state and set {@link DriveState#CHEZY} if in
	 * 			{@code TELEOP}.
	 * 		</li>
	 * 	</ul>
	 * 
	 * @param state
	 *            {@link RobotState}
	 * @param commands
	 *            {@link Commands}
	 */
	@Override
	public void update(Commands commands, RobotState state) {
		mCachedRobotState = state;
		mCachedPose = state.drivePose.copy();
		boolean mIsNewState = !(mState == commands.wantedDriveState);
		mState = commands.wantedDriveState;

		switch(mState) {
			case CHEZY:
				/*DriveSignal deletthis = DriveSignal.getNeutralSignal();
				deletthis.leftMotor.setPercentOutput(.1);
				deletthis.rightMotor.setPercentOutput(.1);
				setDriveOutputs(deletthis);*/
				setDriveOutputs(mCDH.cheesyDrive(commands, mCachedRobotState));
				break;
			case VISION_ASSIST:
				setDriveOutputs(mVDH.visionDrive(commands, mCachedRobotState));
				break;
			case OFF_BOARD_CONTROLLER:
				//Falls through
                setDriveOutputs(mController.update(mCachedRobotState));
				break;
			case ON_BOARD_CONTROLLER:
				if(mController == null) {
					Logger.getInstance().logSubsystemThread(Level.WARNING, "No onboard controller to use!");
					commands.wantedDriveState = DriveState.NEUTRAL;
				} else {
					setDriveOutputs(mController.update(mCachedRobotState));
				}
				break;
			case OPEN_LOOP:
				if(commands.robotSetpoints.drivePowerSetpoint.isPresent()) {
					setDriveOutputs(commands.robotSetpoints.drivePowerSetpoint.get());
				}
				break;
			case NEUTRAL:
				if(!newController && mIsNewState) {
					resetController();
				}
				setDriveOutputs(SparkSignal.getNeutralSignal());

				if(mCachedRobotState.gamePeriod.equals(RobotState.GamePeriod.TELEOP)) {
					if(mIsNewState) {
						resetController();
					}
					commands.wantedDriveState = DriveState.CHEZY;
				}
				break;
		}

		mState = commands.wantedDriveState;

		leftEncoder.updateValue(state.drivePose.leftEnc);
		rightEncoder.updateValue(state.drivePose.rightEnc);

		Logger.getInstance().logSubsystemThread(Level.FINEST, "Left drive encoder", leftEncoder);
		Logger.getInstance().logSubsystemThread(Level.FINEST, "Right drive encoder", rightEncoder);

		DashboardManager.getInstance().publishKVPair(leftEncoder);
		DashboardManager.getInstance().publishKVPair(rightEncoder);

		DashboardManager.getInstance().publishKVPair(motors);

		mWriter.addData("driveLeftEnc", state.drivePose.leftEnc);
		mWriter.addData("driveLeftEncVelocity", state.drivePose.leftEncVelocity);
		mWriter.addData("driveRightEnc", state.drivePose.rightEnc);
		mWriter.addData("driveRightEncVelocity", state.drivePose.rightEncVelocity);
		mWriter.addData("driveHeading", state.drivePose.heading);
		mWriter.addData("driveHeadingVelocity", state.drivePose.headingVelocity);
		state.drivePose.leftError.ifPresent(integer -> mWriter.addData("driveLeftError", (double) integer));
		state.drivePose.rightError.ifPresent(integer -> mWriter.addData("driveRightError", (double) integer));
		mWriter.addData("driveLeftSetpoint", mSignal.leftMotor.getSetpoint());
		mWriter.addData("driveRightSetpoint", mSignal.rightMotor.getSetpoint());
	}

	@Override
	public void stop() {
	}

	private void setDriveOutputs(SparkSignal signal) {
		mSignal = signal;
	}

	/**
	 * Used when external reset of drivetrain is desired
	 */
	public void setNeutral() {
		mController = null;
		mState = DriveState.NEUTRAL;
		setDriveOutputs(SparkSignal.getNeutralSignal());
	}

	public void setSparkMaxController(SparkSignal signal) {
		mController = new SparkMaxDriveController(signal);
		newController = true;
	}
	public void setVisionAngleSetpoint() {
		mController = new VisionTurnAngleController(mCachedPose);
		newController = true;
	}
	public void setTurnAngleSetpoint(double heading) {
		mController = new BangBangTurnAngleController(mCachedPose, heading);
		newController = true;
	}

	/**
	 * Motion profile hype
	 * 
	 * @param path {@link Path} to follow
	 * @param inverted Boolean to invert path
	 */
	public void setTrajectoryController(Path path, boolean inverted) {
		mController = new AdaptivePurePursuitController(DrivetrainConstants.kPathFollowingLookahead, DrivetrainConstants.kPathFollowingMaxAccel,
				DrivetrainConstants.kNormalLoopsDt, path,
				inverted, 0);
		mController.update(mCachedRobotState);
		newController = true;
	}

	public void setTrajectoryController(Path path, double lookahead, boolean inverted) {
		mController = new AdaptivePurePursuitController(lookahead, DrivetrainConstants.kPathFollowingMaxAccel, DrivetrainConstants.kNormalLoopsDt, path,
				inverted, 0);
		mController.update(mCachedRobotState);
		newController = true;
	}

	public void setTrajectoryController(Path path, double lookahead, boolean inverted, double tolerance) {
		mController = new AdaptivePurePursuitController(lookahead, DrivetrainConstants.kPathFollowingMaxAccel, DrivetrainConstants.kNormalLoopsDt, path,
				inverted, tolerance);
		mController.update(mCachedRobotState);
		newController = true;
	}

	public void setDriveStraight(double distance) {
		mController = new DriveStraightController(mCachedPose, distance);
		newController = true;
	}

	public void setCascadingGyroEncoderTurnAngleController(double angle) {
		mController = new CascadingGyroEncoderTurnAngleController(mCachedPose, angle);
		newController = true;
	}

	public void setTimedDrive(double voltage, double time) {
		mController = new TimedDriveController(voltage, time);
		newController = true;
	}

	//Wipes current controller
	public void resetController() {
		mController = null;
	}

	/**
	 * @return The pose according to the current sensor state
	 */
	public Pose getPose() {
		//If drivetrain has not had first update yet, return initial robot pose of 0,0,0,0,0,0
		if(mCachedRobotState == null) {
			return new Pose(0, 0, 0, 0, 0, 0, 0, 0);
		}
		return mCachedPose;
	}

	public Drive.DriveController getController() {
		return mController;
	}

	public boolean controllerOnTarget() {
		return (mController == null || mController.onTarget());
	}

	public boolean hasController() {
		return mController != null;
	}

	/**
	 * <h1>Interface for drive controllers</h1>
	 *
	 * Contains an {@code update} method that takes a {@link RobotState} and generates a {@link DriveSignal}.
	 */
	public interface DriveController {
		SparkSignal update(RobotState state);

		Pose getSetpoint();

		boolean onTarget();
	}

	@Override
	public String getStatus() {
		return "Drive State: " + mState + "\nOutput Control Mode: " + mSignal.leftMotor.getControlType() + "\nLeft Setpoint: " + mSignal.leftMotor.getSetpoint()
				+ "\nRight Setpoint: " + mSignal.rightMotor.getSetpoint() + "\nLeft Enc: " + mCachedPose.leftEnc + "\nRight Enc: " + mCachedPose.rightEnc
				+ "\nGyro: " + mCachedPose.heading + "\n";
	}
}