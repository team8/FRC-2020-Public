package com.palyrobotics.frc2020.util.control;

import static com.palyrobotics.frc2020.robot.HardwareWriter.kTimeoutMs;
import static java.util.Map.entry;

import java.util.Map;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.robot.HardwareWriter;

public class Talon extends TalonSRX implements Controller {

	static class BaseTalonController<T extends BaseTalon & Controller> extends ProfiledControllerBase<T> {

		protected double mPositionConversion, mVelocityConversion;

		protected BaseTalonController(T talon) {
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
		protected boolean setReference(ControllerOutput.Mode mode, int slot, double reference, double arbitraryPercentOutput) {
			ControlMode controllerMode = kModeToController.get(mode);
			double convertedReference;
			switch (mode) {
				case VELOCITY:
				case PROFILED_VELOCITY:
					convertedReference = reference / mVelocityConversion;
					break;
				case POSITION:
				case PROFILED_POSITION:
					convertedReference = reference / mPositionConversion;
					break;
				default:
					convertedReference = reference;
					break;
			}
			mController.selectProfileSlot(slot, HardwareWriter.kPidIndex);
			mController.set(controllerMode, convertedReference, DemandType.ArbitraryFeedForward, arbitraryPercentOutput);
			return true;
		}

		@Override
		protected void setP(int slot, double p) {
			mController.config_kP(slot, p, kTimeoutMs);
		}

		@Override
		protected void setI(int slot, double i) {
			mController.config_kI(slot, i, kTimeoutMs);
		}

		@Override
		protected void setD(int slot, double d) {
			mController.config_kD(slot, d, kTimeoutMs);
		}

		@Override
		protected void setF(int slot, double f) {
			mController.config_kF(slot, f, kTimeoutMs);
		}

		@Override
		protected void setIZone(int slot, double iZone) {
			mController.config_IntegralZone(slot, round(iZone), kTimeoutMs);
		}

		@Override
		protected void setIMax(int slot, double iMax) {
			mController.configMaxIntegralAccumulator(slot, iMax, kTimeoutMs);
		}

		@Override
		protected void setFrameTimings() {
			/* Update period of commands sent to controller */
			mController.setControlFramePeriod(ControlFrame.Control_3_General, mControlFrameMs);
			/* Update period of feedback received from controller */
			// Applied motor output, fault information, limit switch information
			mController.setStatusFramePeriod(StatusFrame.Status_1_General, mStatusFrameMs, kTimeoutMs);
			// Selected sensor position and velocity, supply current measurement, sticky fault information
			mController.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, mStatusFrameMs, kTimeoutMs);
		}
	}

	protected static final Map<ControllerOutput.Mode, ControlMode> kModeToController = Map.ofEntries(
			entry(ControllerOutput.Mode.PERCENT_OUTPUT, ControlMode.PercentOutput),
			entry(ControllerOutput.Mode.POSITION, ControlMode.Position),
			entry(ControllerOutput.Mode.VELOCITY, ControlMode.Velocity),
			entry(ControllerOutput.Mode.PROFILED_POSITION, ControlMode.MotionMagic),
			entry(ControllerOutput.Mode.PROFILED_VELOCITY, ControlMode.MotionProfile));
	private final BaseTalonController<Talon> mController = new BaseTalonController<>(this);
	private final String mName;

	public Talon(int deviceId, String name) {
		super(deviceId);
		mName = name;
		clearStickyFaults(kTimeoutMs);
	}

	public boolean setOutput(ControllerOutput output) {
		return mController.setOutput(output);
	}

	/**
	 * When controllers reset over CAN, frame periods are cleared. This handles resetting them to their
	 * configured values before.
	 */
	public void handleReset() {
		if (hasResetOccurred()) {
			Log.error("reset", String.format("%s reset", mController.getName()));
			mController.updateFrameTimings();
		}
	}

	public void configFrameTimings(int controlFrameMs, int statusFrameMs) {
		mController.configFrameTimings(controlFrameMs, statusFrameMs);
	}

	public String getName() {
		return String.format("(Talon #%d), %s", getDeviceID(), mName);
	}

	public static int round(double d) {
		return (int) Math.round(d);
	}
}
