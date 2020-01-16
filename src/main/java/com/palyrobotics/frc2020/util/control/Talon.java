package com.palyrobotics.frc2020.util.control;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.palyrobotics.frc2020.robot.HardwareWriter;

public class Talon extends TalonSRX {

	class TalonController extends ProfiledControllerBase {

		@Override
		protected void updateGains(boolean isFirstInitialization, int slot, Gains newGains, Gains lastGains) {
			super.updateGains(isFirstInitialization, slot, newGains, lastGains);
			if (isFirstInitialization) {
				configMotionSCurveStrength(4, HardwareWriter.TIMEOUT_MS);
			}
		}

		@Override
		void setProfiledAcceleration(int slot, double acceleration) {
			configMotionAcceleration(round(acceleration), HardwareWriter.TIMEOUT_MS);
		}

		@Override
		void setProfiledCruiseVelocity(int slot, double cruiseVelocity) {
			configMotionCruiseVelocity(round(cruiseVelocity), HardwareWriter.TIMEOUT_MS);
		}

		@Override
		protected void setProfiledAllowableError(int slot, double allowableError) {
			configAllowableClosedloopError(slot, round(allowableError), HardwareWriter.TIMEOUT_MS);
		}

		@Override
		protected void setProfiledMinimumVelocityOutput(int slot, double minimumOutputVelocity) {
			// Not supported by Talons
		}

		@Override
		boolean setReference(ControllerOutput.Mode mode, int slot, double reference, double arbitraryPercentOutput) {
			ControlMode controllerMode = MODE_TO_CONTROLLER.get(mode);
			Talon.this.set(controllerMode, reference, DemandType.ArbitraryFeedForward, arbitraryPercentOutput);
			return true;
		}

		@Override
		int getId() {
			return getDeviceID();
		}

		@Override
		void setP(int slot, double p) {
			config_kP(slot, p, HardwareWriter.TIMEOUT_MS);
		}

		@Override
		void setI(int slot, double i) {
			config_kI(slot, i, HardwareWriter.TIMEOUT_MS);
		}

		@Override
		void setD(int slot, double d) {
			config_kD(slot, d, HardwareWriter.TIMEOUT_MS);
		}

		@Override
		void setF(int slot, double f) {
			config_kF(slot, f, HardwareWriter.TIMEOUT_MS);
		}

		@Override
		void setIZone(int slot, double iZone) {
			config_IntegralZone(slot, round(iZone), HardwareWriter.TIMEOUT_MS);
		}
	}

	public static final Map<ControllerOutput.Mode, ControlMode> MODE_TO_CONTROLLER = Map.of(
			ControllerOutput.Mode.PERCENT_OUTPUT, ControlMode.PercentOutput, ControllerOutput.Mode.POSITION,
			ControlMode.Position, ControllerOutput.Mode.VELOCITY, ControlMode.Velocity,
			ControllerOutput.Mode.PROFILED_POSITION, ControlMode.MotionMagic, ControllerOutput.Mode.PROFILED_VELOCITY,
			ControlMode.MotionProfile);
	private final ProfiledControllerBase mController = new TalonController();

	public Talon(int deviceNumber) {
		super(deviceNumber);
	}

	public static int round(double d) {
		return (int) Math.round(d);
	}

	public boolean setOutput(ControllerOutput output) {
		return mController.setOutput(output);
	}
}
