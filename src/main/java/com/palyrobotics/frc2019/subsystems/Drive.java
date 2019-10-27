package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.subsystems.controllers.*;
import com.palyrobotics.frc2019.util.CheesyDriveHelper;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;
import com.palyrobotics.frc2019.util.VisionDriveHelper;
import com.palyrobotics.frc2019.util.trajectory.Path;

/**
 * Represents the drivetrain Uses controllers or cheesydrivehelper/proportionaldrivehelper to calculate DriveSignal
 *
 * @author Not Nihar
 */
public class Drive extends Subsystem {
    private static Drive sInstance = new Drive();

    public static Drive getInstance() {
        return sInstance;
    }

    /**
     * <h1>Various control states for the drivetrain</h1>
     *
     * <p>
     * <p>
     * {@code CHEZY} creates a {@link CheesyDriveHelper} drive with joystick values.
     * {@code OFF_BOARD_CONTROLLER} creates a CANTalon offboard loop.
     * {@code ON_BOARD_CONTROLLER} makes a control loop calculated in code with an open loop.
     * It uses drive outputs passed in through commands.
     * {@code NEUTRAL} does nothing.
     */
    public enum DriveState {
        CHEZY, OFF_BOARD_CONTROLLER, ON_BOARD_CONTROLLER, OPEN_LOOP, NEUTRAL, VISION_ASSIST, CLOSED_VISION_ASSIST
    }

    private DriveState mState = DriveState.NEUTRAL;

    // Helper class to calculate teleop output
    private CheesyDriveHelper mCDH = new CheesyDriveHelper();

    private VisionDriveHelper mVDH = new VisionDriveHelper();

    private Drive.DriveController mController;
    // Used for off board controllers to be called only once
    private boolean newController;

    // Encoder DPP
//    private final double kWheelbaseWidth; //Get from CAD
//    private final double kTurnSlipFactor; //Measure empirically

    // Cache poses to not be allocating at 200Hz
    private Pose mPose = new Pose();
    private RobotState mRobotState;
    private SparkDriveSignal mSignal = new SparkDriveSignal();

    protected Drive() {
        super("drive");
    }

    /**
     * @return DriveSignal
     */
    public SparkDriveSignal getDriveSignal() {
        return mSignal;
    }

    @Override
    public void reset() {
        setNeutral();
        mRobotState = RobotState.getInstance();
    }

    /**
     * <h1>Updates the drivetrain and its {@link SparkDriveSignal}</h1>
     *
     * <br>
     * Contains a state machine that switches. based on {@link DriveState} and updates the
     * {@link DriveController} with the current {@link RobotState}. The controllers then output
     * a {@link SparkDriveSignal}, which is then used to {@link Drive#setDriveOutputs}.
     * <br><br>
     * <p>
     * States and behavior:
     * <ul>
     * 	<li>
     *        {@link DriveState#CHEZY}:
     * 		Sets drive outputs using a {@link SparkDriveSignal} from a {@link CheesyDriveHelper}.
     * 	</li>
     * 	<li>
     *        {@link DriveState#OFF_BOARD_CONTROLLER}:
     * 		Updates the {@link DriveController}, but if no controller exists prints a warning and sets the
     *        {@link Commands#wantedDriveState} to {@link DriveState#NEUTRAL}.
     * 	</li>
     * 	<li>
     *        {@link DriveState#ON_BOARD_CONTROLLER}:
     * 		See {@link DriveState#OFF_BOARD_CONTROLLER}.
     * 	</li>
     * 	<li>
     *        {@link DriveState#OPEN_LOOP}:
     * 		Directly accesses {@link Commands#robotSetPoints} for the {@code DrivePowerSetpoint}.
     * 	</li>
     * 	<li>
     *        {@link DriveState#NEUTRAL}:
     * 		Sets drive outputs to a blank {@link SparkDriveSignal}. Will also
     *        {@link Drive#resetController} if in a new state and set {@link DriveState#CHEZY} if in
     *        {@code TELEOP}.
     * 	</li>
     * </ul>
     *
     * @param state    {@link RobotState}
     * @param commands {@link Commands}
     */
    @Override
    public void update(Commands commands, RobotState state) {
        mRobotState = state;
        state.drivePose.copyTo(mPose);
        boolean mIsNewState = mState != commands.wantedDriveState;
        mState = commands.wantedDriveState;
        switch (mState) {
            case CHEZY:
                setDriveOutputs(mCDH.cheesyDrive(commands, mRobotState));
                break;
            case VISION_ASSIST:
                setDriveOutputs(mVDH.visionDrive(commands, mRobotState));
                break;
            case CLOSED_VISION_ASSIST:
            case OFF_BOARD_CONTROLLER:
                setDriveOutputs(mController.update(mRobotState));
                break;
            case ON_BOARD_CONTROLLER:
                if (mController == null) {
//					Logger.getInstance().logSubsystemThread(Level.WARNING, "No onboard controller to use!");
                    commands.wantedDriveState = DriveState.NEUTRAL;
                } else {
                    setDriveOutputs(mController.update(mRobotState));
                }
                break;
            case OPEN_LOOP:
                if (commands.robotSetPoints.drivePowerSetPoint != null) {
                    setDriveOutputs(commands.robotSetPoints.drivePowerSetPoint);
                }
                break;
            case NEUTRAL:
                if (!newController && mIsNewState) {
                    resetController();
                }
                setDriveOutputs(new SparkDriveSignal());
                if (mRobotState.gamePeriod.equals(RobotState.GamePeriod.TELEOP)) {
                    if (mIsNewState) {
                        resetController();
                    }
                    commands.wantedDriveState = DriveState.CHEZY;
                }
                break;
        }

        mState = commands.wantedDriveState;

//        CSVWriter.addData("driveLeftEnc", state.drivePose.leftEncoderPosition);
//        CSVWriter.addData("driveLeftEncVelocity", state.drivePose.leftEncoderVelocity);
//        CSVWriter.addData("driveRightEnc", state.drivePose.rightEncoderPosition);
//        CSVWriter.addData("driveRightEncVelocity", state.drivePose.rightEncoderVelocity);
//        CSVWriter.addData("driveHeading", state.drivePose.heading);
//        CSVWriter.addData("driveHeadingVelocity", state.drivePose.headingVelocity);
//        CSVWriter.addData("driveLeftSetPoint", mSignal.leftOutput.getReference());
//        CSVWriter.addData("driveRightSetPoint", mSignal.rightOutput.getReference());
    }

    private void setDriveOutputs(SparkDriveSignal signal) {
        mSignal = signal;
    }

    /**
     * Used when external reset of drivetrain is desired
     */
    public void setNeutral() {
        mController = null;
        mState = DriveState.NEUTRAL;
        setDriveOutputs(new SparkDriveSignal());
    }

    public void setVisionAngleSetPoint() {
        mController = new VisionTurnAngleController(mPose);
        newController = true;
    }

    public void setTurnAngleSetPoint(double heading) {
        mController = new BangBangTurnAngleController(mPose, heading);
        newController = true;
    }

    /**
     * Motion profile hype
     *
     * @param path     {@link Path} to follow
     * @param inverted Boolean to invert path
     */
    public void setTrajectoryController(Path path, boolean inverted) {
        mController = new AdaptivePurePursuitController(DrivetrainConstants.kPathFollowingLookahead, DrivetrainConstants.kPathFollowingMaxAccel,
                DrivetrainConstants.kNormalLoopsDt, path,
                inverted, 0);
        mController.update(mRobotState);
        newController = true;
    }

    public void setTrajectoryController(Path path, double lookahead, boolean inverted) {
        mController = new AdaptivePurePursuitController(lookahead, DrivetrainConstants.kPathFollowingMaxAccel, DrivetrainConstants.kNormalLoopsDt, path,
                inverted, 0);
        mController.update(mRobotState);
        newController = true;
    }

    public void setTrajectoryController(Path path, double lookahead, boolean inverted, double tolerance) {
        mController = new AdaptivePurePursuitController(lookahead, DrivetrainConstants.kPathFollowingMaxAccel, DrivetrainConstants.kNormalLoopsDt, path,
                inverted, tolerance);
        mController.update(mRobotState);
        newController = true;
    }

    public void setDriveStraight(double distance) {
        mController = new DriveStraightController(mPose, distance);
        newController = true;
    }

    public void setCascadingGyroEncoderTurnAngleController(double angle) {
        mController = new CascadingGyroEncoderTurnAngleController(mPose, angle);
        newController = true;
    }

    public void setTimedDrive(double voltage, double time) {
        mController = new TimedDriveController(voltage, time);
        newController = true;
    }

    public void setVisionClosedDriveController() {
        mController = new VisionClosedController();
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
        return mRobotState == null
                ? new Pose()
                : mPose;
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
     * <p>
     * Contains an {@code update} method that takes a {@link RobotState} and generates a {@link SparkDriveSignal}.
     */
    public interface DriveController {
        SparkDriveSignal update(RobotState state);

        Pose getSetPoint();

        boolean onTarget();
    }

    @Override
    public String getStatus() {
        return String.format("Drive State: %s%nOutput Control Mode: %s%nLeft Set Point: %s%nRight Set Point: %s%nLeft Position: %s%nRight Position: %s%nGyro: %s%n", mState, mSignal.leftOutput.getControlType(), mSignal.leftOutput.getReference(), mSignal.rightOutput.getReference(), mPose.leftEncoderPosition, mPose.rightEncoderPosition, mPose.heading);
    }
}
