package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.subsystem.TurretConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Turret extends SubsystemBase {

	public enum TurretState {
		IDLE, ROTATING_LEFT, ROTATING_RIGHT
	}

	private static Turret sInstance = new Turret();
	private TurretConfig mConfig = Configs.get(TurretConfig.class);
	private ControllerOutput mOutput = new ControllerOutput();
	private boolean calibrationWanted = false;

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
