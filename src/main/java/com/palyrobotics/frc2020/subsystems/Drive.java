package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.subsystems.controllers.DriveRamseteController;
import com.palyrobotics.frc2020.subsystems.controllers.VisionClosedController;
import com.palyrobotics.frc2020.util.CheesyDriveHelper;
import com.palyrobotics.frc2020.util.SparkDriveSignal;
import com.palyrobotics.frc2020.util.VisionDriveHelper;
import edu.wpi.first.wpilibj.trajectory.Trajectory;

/**
 * Represents the drivetrain Uses controllers or cheesydrivehelper/proportionaldrivehelper to calculate DriveSignal
 *
 * @author Not Nihar
 */
public class Drive extends Subsystem {

    private static Drive sInstance = new Drive();
    private DriveState mState = DriveState.NEUTRAL;
    // Helper class to calculate teleop output
    private CheesyDriveHelper mCDH = new CheesyDriveHelper();
    private VisionDriveHelper mVDH = new VisionDriveHelper();
    private Drive.DriveController mController;
    // Used for off board controllers to be called only once
    private boolean newController;
    private RobotState mRobotState;
    private SparkDriveSignal mSignal = new SparkDriveSignal();

    protected Drive() {
        super("drive");
    }

    public static Drive getInstance() {
        return sInstance;
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

    public void setTrajectoryController(Trajectory trajectory) {
        mController = new DriveRamseteController(trajectory);
        mController.update(mRobotState);
        newController = true;
    }

    public void setVisionClosedDriveController() {
        mController = new VisionClosedController();
        newController = true;
    }

    public void resetController() {
        mController = null;
    }

    public Drive.DriveController getController() {
        return mController;
    }

    public boolean isOnTarget() {
        return (mController == null || mController.onTarget());
    }

    public boolean hasController() {
        return mController != null;
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

    /**
     * <h1>Interface for drive controllers</h1>
     * <p>
     * Contains an {@code update} method that takes a {@link RobotState} and generates a {@link SparkDriveSignal}.
     */
    public interface DriveController {

        SparkDriveSignal update(RobotState state);

        boolean onTarget();
    }
}
