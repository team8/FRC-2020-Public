package com.palyrobotics.frc2020.subsystems;

import static com.palyrobotics.frc2020.robot.HardwareWriter.*;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.HardwareAdapter;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.controllers.drive.AlignDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.drive.ChezyDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.drive.RamseteDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.drive.YawDriveController;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.DriveOutputs;
import com.palyrobotics.frc2020.util.control.Falcon;

import edu.wpi.first.wpilibj.geometry.Pose2d;

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
	private HardwareAdapter.DriveHardware hardware = HardwareAdapter.DriveHardware.getInstance();
	private DriveOutputs mOutputs = new DriveOutputs();

	private Drive() {
	}

	public static Drive getInstance() {
		return sInstance;
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

	@Override
	public void writeHardware(RobotState state) {
		hardware.falcons.forEach(Falcon::handleReset);
		hardware.leftMasterFalcon.setOutput(mOutputs.leftOutput);
		hardware.rightMasterFalcon.setOutput(mOutputs.rightOutput);
		handleReset(hardware.gyro);
	}

	@Override
	public void configureHardware() {
		/* Falcons */
		for (Falcon falcon : hardware.falcons) {
			falcon.configFactoryDefault(kTimeoutMs);
			falcon.enableVoltageCompensation(true);
			falcon.configVoltageCompSaturation(kVoltageCompensation, kTimeoutMs);
			falcon.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, kPidIndex, kTimeoutMs);
			falcon.configIntegratedSensorInitializationStrategy(SensorInitializationStrategy.BootToZero, kTimeoutMs);
			falcon.configSupplyCurrentLimit(new SupplyCurrentLimitConfiguration(
					true, 40.0, 50.0, 1.0));
			falcon.configOpenloopRamp(0.1, kTimeoutMs);
			falcon.configClosedloopRamp(0.1, kTimeoutMs);
			falcon.configSensorConversions(DriveConstants.kDriveMetersPerTick, DriveConstants.kDriveMetersPerSecondPerTickPer100Ms);
		}
		// Left
		hardware.leftMasterFalcon.setInverted(false);
		hardware.leftMasterFalcon.setFrameTimings(5, 5);
		hardware.leftSlaveFalcon.follow(hardware.leftMasterFalcon);
		hardware.leftSlaveFalcon.setInverted(InvertType.FollowMaster);
		hardware.leftSlaveFalcon.setFrameTimings(40, 40);
		// Right
		hardware.rightMasterFalcon.setInverted(true);
		hardware.rightMasterFalcon.setFrameTimings(5, 5);
		hardware.rightSlaveFalcon.follow(hardware.rightMasterFalcon);
		hardware.rightSlaveFalcon.setInverted(InvertType.FollowMaster);
		hardware.rightSlaveFalcon.setFrameTimings(40, 40);
		/* Gyro */
		// 10 ms update period for yaw degrees and yaw angular velocity in degrees per second
		setPigeonStatusFramePeriods(hardware.gyro);
		/* Falcons and Gyro */
		resetDriveSensors(new Pose2d());
	}
}
