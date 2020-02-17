package com.palyrobotics.frc2020.behavior.routines.drive;

import static com.palyrobotics.frc2020.util.Util.newWaypoint;

import java.util.Set;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.geometry.Pose2d;

/**
 * Sets {@link Commands#driveWantedOdometryPose} and waits in neutral until the requests are seen
 * within {@link RobotState}. Note: As of 1/13/20, setting encoder positions on a Spark does not
 * happen immediately. CTRE controllers have a timeout value so it blocks until they are set
 * properly. Has a timeout just in case there is a controller set fault.
 */
// TODO: remove timeout? If we fail, we should not continue with our auto.
public class DriveSetOdometryRoutine extends TimeoutRoutineBase {

	public static final double kTimeout = 0.5;
	private Pose2d mTargetPose;

	public DriveSetOdometryRoutine() {
		this(0.0, 0.0, 0.0);
	}

	public DriveSetOdometryRoutine(double xInches, double yInches, double yawDegrees) {
		super(kTimeout);
		mTargetPose = newWaypoint(xInches, yInches, yawDegrees);
	}

	public Pose2d getTargetPose() {
		return mTargetPose;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return state.drivePoseMeters.equals(mTargetPose);
	}

	@Override
	public void onTimeout() {
		Log.error(getName(),
				"Timed out! This means the controllers internal state was not updated properly and indicates a controller fault.");
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.driveWantedOdometryPose = mTargetPose;
		// This is required since an existing controller which depends on robot state
		// will not be notified when sensors are updated.
		// So, we break out and get into a neutral state. This way, controllers that
		// depend on robot state odometry or yaw are recreated after they are reset.
		commands.setDriveNeutral();
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}
}
