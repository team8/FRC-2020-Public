package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.controllers.ChezyDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.RamseteDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.VisionDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.YawDriveController;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.DriveOutputs;

/**
 * Represents the drivetrain. Uses controllers or cheesy drive
 * helper/proportional drive helper to calculate a drive signal.
 */
public class Drive extends SubsystemBase {

	public enum DriveState {
		NEUTRAL, TELEOP, SIGNAL, FOLLOW_PATH, VISION_ALIGN, TURN
	}

	public abstract static class DriveController {

		protected final DriveConfig mDriveConfig = Configs.get(DriveConfig.class);

		protected DriveOutputs mDriveOutputs = new DriveOutputs();

		public final DriveOutputs update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
			updateSignal(commands, state);
			return mDriveOutputs;
		}

		/**
		 * Should set {@link #mDriveOutputs} to reflect what is currently wanted by
		 * {@link Commands}.
		 */
		public abstract void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state);
	}

	private static Drive sInstance = new Drive();
	private Drive.DriveController mController;
	private DriveState mState;
	private DriveOutputs mSignal = new DriveOutputs();

	private Drive() {
	}

	public static Drive getInstance() {
		return sInstance;
	}

	public DriveOutputs getDriveSignal() {
		return mSignal;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		DriveState wantedState = commands.getDriveWantedState();
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
				case SIGNAL:
					mController = new DriveController() {

						@Override
						public void updateSignal(Commands commands, RobotState state) {
							mSignal = commands.getDriveWantedSignal();
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
					mController = new VisionDriveController();
			}
		}
		if (mController == null) {
			mSignal.leftOutput.setIdle();
			mSignal.rightOutput.setIdle();
		} else {
			mSignal = mController.update(commands, state);
		}
	}
}
