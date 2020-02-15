package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.controllers.drive.AlignDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.drive.ChezyDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.drive.RamseteDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.drive.YawDriveController;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.DriveOutputs;

/**
 * Represents the drivetrain. Uses {@link #mController} to generate {@link #mOutputs}.
 */
public class Drive extends SubsystemBase {

	public enum State {
		NEUTRAL, TELEOP, OUTPUTS, FOLLOW_PATH, VISION_ALIGN, TURN
	}

	public abstract static class DriveController {

		protected final DriveConfig mConfig = Configs.get(DriveConfig.class);

		protected DriveOutputs mOutputs = new DriveOutputs();

		public final DriveOutputs update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
			updateSignal(commands, state);
			return mOutputs;
		}

		/**
		 * Should set {@link #mOutputs} to reflect what is currently wanted by {@link Commands}.
		 */
		public abstract void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state);
	}

	private static Drive sInstance = new Drive();
	private Drive.DriveController mController;
	private State mState;
	private DriveOutputs mOutputs = new DriveOutputs();

	private Drive() {
	}

	public static Drive getInstance() {
		return sInstance;
	}

	public DriveOutputs getDriveSignal() {
		return mOutputs;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		State wantedState = commands.getDriveWantedState();
		boolean isNewState = mState != wantedState;
		mState = wantedState;
		if (isNewState) {
			switch (wantedState) {
				case NEUTRAL:
					mController = null;
					break;
				case TELEOP:
					mController = new ChezyDriveController();
					break;
				case OUTPUTS:
					mController = new DriveController() {

						@Override
						public void updateSignal(Commands commands, RobotState state) {
							mOutputs = commands.getDriveWantedSignal();
						}
					};
					break;
				case FOLLOW_PATH:
					mController = new RamseteDriveController();
					break;
				case TURN:
					mController = new YawDriveController();
					break;
				case VISION_ALIGN:
					mController = new AlignDriveController();
			}
		}
		if (mController == null) {
			mOutputs.leftOutput.setIdle();
			mOutputs.rightOutput.setIdle();
		} else {
			mOutputs = mController.update(commands, state);
		}
	}
}
