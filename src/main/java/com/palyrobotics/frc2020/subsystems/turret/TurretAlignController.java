package com.palyrobotics.frc2020.subsystems.turret;

import com.palyrobotics.frc2020.config.subsystem.TurretConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Turret;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.Limelight;
import com.palyrobotics.frc2020.vision.LimelightControlMode;

import edu.wpi.first.wpilibj.controller.PIDController;

public class TurretAlignController extends Turret.TurretController {

	private Limelight mLimelight = new Limelight();
	private TurretConfig mConfig = Configs.get(TurretConfig.class);
	private PIDController mPIDController = new PIDController(mConfig.turnGains.p, mConfig.turnGains.i, mConfig.turnGains.d);

	public TurretAlignController() {
		mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_ON);
		mLimelight.setCamMode(LimelightControlMode.CamMode.VISION);
		mLimelight.setPipeline(0);
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		double percentOutput = mPIDController.calculate(mLimelight.getYawToTarget());
		percentOutput = Util.clamp(percentOutput, -percentOutput, percentOutput);
		mOutput.setPercentOutput(percentOutput);
	}
}
