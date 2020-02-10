package com.palyrobotics.frc2020.util.control;

import static com.palyrobotics.frc2020.robot.HardwareWriter.kTimeoutMs;
import static java.util.Map.entry;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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
				mController.configMotionSCurveStrength(4, kTimeoutMs);
			}
		}

		@Override
		void setProfiledAcceleration(int slot, double acceleration) {
			mController.configMotionAcceleration(round(acceleration), kTimeoutMs);
		}

		@Override
		void setProfiledCruiseVelocity(int slot, double cruiseVelocity) {
			mController.configMotionCruiseVelocity(round(cruiseVelocity), kTimeoutMs);
		}

		@Override
		protected void setProfiledAllowableError(int slot, double allowableError) {
			mController.configAllowableClosedloopError(slot, round(allowableError), kTimeoutMs);
		}

		@Override
		protected void setProfiledMinimumVelocityOutput(int slot, double minimumOutputVelocity) {
			// Not supported by Talons
		}

		@Override
		boolean setReference(ControllerOutput.Mode mode, int slot, double reference, double arbitraryPercentOutput) {
			ControlMode controllerMode = kModeToController.get(mode);
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
			mController.set(
					controllerMode, convertedReference,
					DemandType.ArbitraryFeedForward, arbitraryPercentOutput);
			return true;
		}

		@Override
		int getId() {
			return mController.getDeviceID();
		}

		@Override
		void setP(int slot, double p) {
			mController.config_kP(slot, p, kTimeoutMs);
		}

		@Override
		void setI(int slot, double i) {
			mController.config_kI(slot, i, kTimeoutMs);
		}

		@Override
		void setD(int slot, double d) {
			mController.config_kD(slot, d, kTimeoutMs);
		}

		@Override
		void setF(int slot, double f) {
			mController.config_kF(slot, f, kTimeoutMs);
		}

		@Override
		void setIZone(int slot, double iZone) {
			mController.config_IntegralZone(slot, round(iZone), kTimeoutMs);
		}

		@Override
		void setIMax(int slot, double iMax) {
			mController.configMaxIntegralAccumulator(slot, iMax, kTimeoutMs);
		}
	}

	protected static final Map<ControllerOutput.Mode, ControlMode> kModeToController = Map.ofEntries(
			entry(ControllerOutput.Mode.PERCENT_OUTPUT, ControlMode.PercentOutput),
			entry(ControllerOutput.Mode.POSITION, ControlMode.Position),
			entry(ControllerOutput.Mode.VELOCITY, ControlMode.Velocity),
			entry(ControllerOutput.Mode.PROFILED_POSITION, ControlMode.MotionMagic),
			entry(ControllerOutput.Mode.PROFILED_VELOCITY, ControlMode.MotionProfile));
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
