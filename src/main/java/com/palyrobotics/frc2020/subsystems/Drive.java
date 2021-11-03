package com.palyrobotics.frc2020.subsystems;

import java.util.List;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.PortConstants;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.*;
import com.palyrobotics.frc2020.subsystems.controllers.drive.AlignDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.drive.ChezyDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.drive.RamseteDriveController;
import com.palyrobotics.frc2020.subsystems.controllers.drive.YawDriveController;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.DriveOutputs;
import com.palyrobotics.frc2020.util.control.Falcon;

import edu.wpi.first.wpilibj.geometry.Pose2d;

/**
 * Represents the drivetrain. Uses {@link #mController} to generate {@link #mOutputs}.
 */
public class

Drive extends SubsystemBase {

	private static final String kLoggerTag = Util.classToJsonName(Drive.class);
	public static final int
	// Blocks config calls for specified timeout
	kTimeoutMs = 150,
			// Different from slot index.
			// 0 for Primary closed-loop. 1 for auxiliary closed-loop.
			kPidIndex = 0;
	public static double kVoltageCompensation = 12.0;
	private static final PortConstants sPortConstants = Configs.get(PortConstants.class);

	public enum State {
		NEUTRAL, TELEOP, OUTPUTS, FOLLOW_PATH, VISION_ALIGN, TURN
	}

	private final Falcon leftMasterFalcon = new Falcon(sPortConstants.nariDriveLeftMasterId, "Drive Left Master"),
			leftSlaveFalcon = new Falcon(sPortConstants.nariDriveLeftSlaveId, "Drive Left Slave");
	private final Falcon rightMasterFalcon = new Falcon(sPortConstants.nariDriveRightMasterId, "Drive Right Master"),
			rightSlaveFalcon = new Falcon(sPortConstants.nariDriveRightSlaveId, "Drive Right Slave");

	private final List<Falcon> falcons = List.of(leftMasterFalcon, leftSlaveFalcon,
			rightMasterFalcon, rightSlaveFalcon);

	private final PigeonIMU gyro = new PigeonIMU(sPortConstants.nariDriveGyroId);

	public void configureDriveHardware() {
		/* Falcons */
		for (Falcon falcon : falcons) {
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
		leftMasterFalcon.setInverted(false);
		leftMasterFalcon.setFrameTimings(5, 5);
		leftSlaveFalcon.follow(leftMasterFalcon);
		leftSlaveFalcon.setInverted(InvertType.FollowMaster);
		leftSlaveFalcon.setFrameTimings(40, 40);
		// Right
		rightMasterFalcon.setInverted(true);
		rightMasterFalcon.setFrameTimings(5, 5);
		rightSlaveFalcon.follow(rightMasterFalcon);
		rightSlaveFalcon.setInverted(InvertType.FollowMaster);
		rightSlaveFalcon.setFrameTimings(40, 40);
		/* Gyro */
		// 10 ms update period for yaw degrees and yaw angular velocity in degrees per second
		setPigeonStatusFramePeriods(gyro);
		/* Falcons and Gyro */
		resetDriveSensors(new Pose2d());
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

	public void updateDrivetrain() {
		falcons.forEach(Falcon::handleReset);
		leftMasterFalcon.setOutput(getDriveSignal().leftOutput);
		rightMasterFalcon.setOutput(getDriveSignal().rightOutput);
		handleReset(gyro);
	}

	public void resetDriveSensors(Pose2d pose) {
		double heading = pose.getRotation().getDegrees();
		gyro.setYaw(heading, kTimeoutMs);
		leftMasterFalcon.setSelectedSensorPosition(0);
		rightMasterFalcon.setSelectedSensorPosition(0);
		Log.info(kLoggerTag, String.format("Drive sensors reset, gyro heading: %s", heading));
	}

	public void setDriveNeutralMode(NeutralMode neutralMode) {
		leftMasterFalcon.setNeutralMode(neutralMode);
		rightMasterFalcon.setNeutralMode(neutralMode);
	}

	private void setPigeonStatusFramePeriods(PigeonIMU gyro) {
		gyro.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, 5, kTimeoutMs);
		gyro.setStatusFramePeriod(PigeonIMU_StatusFrame.BiasedStatus_2_Gyro, 5, kTimeoutMs);
	}

	private void handleReset(PigeonIMU pigeon) {
		if (pigeon.hasResetOccurred()) {
			Log.error(kLoggerTag, "Pigeon reset");
			setPigeonStatusFramePeriods(pigeon);
		}
	}
}
