package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.TurretConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;

import edu.wpi.first.wpilibj.controller.PIDController;

public class Turret extends SubsystemBase {

	public enum State {
		IDLE, ROTATING_LEFT, ROTATING_RIGHT, VISION_ALIGN
	}

	private static Turret sInstance = new Turret();
	private TurretConfig mConfig = Configs.get(TurretConfig.class);
	private ControllerOutput mOutput = new ControllerOutput();
	private Limelight mLimelight = new Limelight();
	private PIDController mPIDController = new PIDController(mConfig.turnGains.p, mConfig.turnGains.i, mConfig.turnGains.d);
	private boolean calibrationWanted = false;

	/*might use controllers*/

	public abstract static class TurretController {

		protected final TurretConfig mConfig = Configs.get(TurretConfig.class);
		protected ControllerOutput mOutput = new ControllerOutput();

		public final ControllerOutput update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
			updateSignal(commands, state);
			return mOutput;
		}

		public abstract void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state);
	}

	private Turret() {
	}

	public static Turret getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState robotState) {
		switch (commands.turretWantedState) {
			case IDLE:
				mOutput.setIdle();
				break;
			case ROTATING_LEFT:
				mOutput.setPercentOutput(mConfig.rotatingOutput);
				break;
			case ROTATING_RIGHT:
				mOutput.setPercentOutput(-mConfig.rotatingOutput);
				break;
			case VISION_ALIGN:
				if (robotState.drivePose.getTranslation().getY() > mConfig.distanceToMiddleOfField) { //might be translation x
					mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_ON);
					mLimelight.setCamMode(LimelightControlMode.CamMode.VISION);
					mLimelight.setPipeline(0);
				}
				double PIDInput = mLimelight.isTargetFound() ? mLimelight.getYawToTarget() : robotState.driveYawDegrees;
				double percentOutput = -mPIDController.calculate(PIDInput);
				percentOutput = Util.clamp(percentOutput, -percentOutput, percentOutput);
				if (Math.abs(robotState.turretYawDegrees) - mConfig.maximumAngle < mConfig.acceptableYawError) {
					mOutput.setPercentOutput(0);
				} else {
					mOutput.setPercentOutput(percentOutput);
				}
		}

		// Calibration
		calibrationWanted = commands.turretCalibrationWanted;
	}

	public ControllerOutput getOutput() {
		return mOutput;
	}

	public boolean getCalibrationWanted() {
		return calibrationWanted;
	}
}
