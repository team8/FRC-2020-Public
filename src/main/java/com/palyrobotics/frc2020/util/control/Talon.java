package com.palyrobotics.frc2020.util.control;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.palyrobotics.frc2020.robot.HardwareWriter;

public class Talon extends TalonSRX {

	static class TalonController extends ProfiledControllerBase<BaseTalon> {

		protected double mPositionConversion, mVelocityConversion;

		protected TalonController(BaseTalon talon) {
			super(talon);
		}

		@Override
		protected void updateGains(boolean isFirstInitialization, int slot, Gains newGains, Gains lastGains) {
			super.updateGains(isFirstInitialization, slot, newGains, lastGains);
			if (isFirstInitialization) {
				mController.configMotionSCurveStrength(4, HardwareWriter.TIMEOUT_MS);
			}
		}

		@Override
		void setProfiledAcceleration(int slot, double acceleration) {
			mController.configMotionAcceleration(round(acceleration), HardwareWriter.TIMEOUT_MS);
		}

		@Override
		void setProfiledCruiseVelocity(int slot, double cruiseVelocity) {
			mController.configMotionCruiseVelocity(round(cruiseVelocity), HardwareWriter.TIMEOUT_MS);
		}

		@Override
		protected void setProfiledAllowableError(int slot, double allowableError) {
			mController.configAllowableClosedloopError(slot, round(allowableError), HardwareWriter.TIMEOUT_MS);
		}

		@Override
		protected void setProfiledMinimumVelocityOutput(int slot, double minimumOutputVelocity) {
			// Not supported by Talons
		}

		@Override
		boolean setReference(ControllerOutput.Mode mode, int slot, double reference, double arbitraryPercentOutput) {
			ControlMode controllerMode = MODE_TO_CONTROLLER.get(mode);
			double convertedReference;
			switch (mode) {
				case VELOCITY:
				case PROFILED_VELOCITY:
					convertedReference = reference * mVelocityConversion;
					break;
				case POSITION:
				case PROFILED_POSITION:
					convertedReference = reference * mPositionConversion;
					break;
				default:
					convertedReference = reference;
					break;
			}
			mController.set(controllerMode, convertedReference, DemandType.ArbitraryFeedForward,
					arbitraryPercentOutput);
			return true;
		}

		@Override
		int getId() {
			return mController.getDeviceID();
		}

		@Override
		void setP(int slot, double p) {
			mController.config_kP(slot, p, HardwareWriter.TIMEOUT_MS);
		}

		@Override
		void setI(int slot, double i) {
			mController.config_kI(slot, i, HardwareWriter.TIMEOUT_MS);
		}

		@Override
		void setD(int slot, double d) {
			mController.config_kD(slot, d, HardwareWriter.TIMEOUT_MS);
		}

		@Override
		void setF(int slot, double f) {
			mController.config_kF(slot, f, HardwareWriter.TIMEOUT_MS);
		}

		@Override
		void setIZone(int slot, double iZone) {
			mController.config_IntegralZone(slot, round(iZone), HardwareWriter.TIMEOUT_MS);
		}
	}

	protected static final Map<ControllerOutput.Mode, ControlMode> MODE_TO_CONTROLLER = Map.ofEntries(
			Map.entry(ControllerOutput.Mode.PERCENT_OUTPUT, ControlMode.PercentOutput),
			Map.entry(ControllerOutput.Mode.POSITION, ControlMode.Position),
			Map.entry(ControllerOutput.Mode.VELOCITY, ControlMode.Velocity),
			Map.entry(ControllerOutput.Mode.PROFILED_POSITION, ControlMode.MotionMagic),
			Map.entry(ControllerOutput.Mode.PROFILED_VELOCITY, ControlMode.MotionProfile));
	private final TalonController mController = new TalonController(this);

	public Talon(int deviceId) {
		super(deviceId);
	}

	public static int round(double d) {
		return (int) Math.round(d);
	}

	public boolean setOutput(ControllerOutput output) {
		return mController.setOutput(output);
	}
}
