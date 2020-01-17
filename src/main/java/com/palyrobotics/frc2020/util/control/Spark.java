package com.palyrobotics.frc2020.util.control;

import java.util.Map;

import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

/**
* A wrapper around a Spark Max that only updates inputs when they have changed.
* This also supports updating gains smartly. Control types are automatically
* mapped to PID slots on the Spark controller.
*
* @author Quintin Dwight
*/
public class Spark extends CANSparkMax {

	public static final Map<ControllerOutput.Mode, ControlType> MODE_TO_CONTROLLER = Map.of(
			ControllerOutput.Mode.PERCENT_OUTPUT, ControlType.kDutyCycle, ControllerOutput.Mode.POSITION,
			ControlType.kPosition, ControllerOutput.Mode.VELOCITY, ControlType.kVelocity,
			ControllerOutput.Mode.PROFILED_POSITION, ControlType.kSmartMotion, ControllerOutput.Mode.PROFILED_VELOCITY,
			ControlType.kSmartVelocity);

	private final CANPIDController mHardwareController = getPIDController();
	private final ProfiledControllerBase mController = new ProfiledControllerBase() {

		@Override
		protected void updateGains(boolean isFirstInitialization, int slot, Gains newGains, Gains lastGains) {
			super.updateGains(isFirstInitialization, slot, newGains, lastGains);
			if (isFirstInitialization) {
				mHardwareController.setOutputRange(-1.0, 1.0, slot);
				mHardwareController.setSmartMotionAccelStrategy(CANPIDController.AccelStrategy.kSCurve, slot);
			}
		}

		@Override
		void setProfiledAcceleration(int slot, double acceleration) {
			mHardwareController.setSmartMotionMaxAccel(acceleration, slot);
		}

		@Override
		void setProfiledCruiseVelocity(int slot, double cruiseVelocity) {
			mHardwareController.setSmartMotionMaxVelocity(cruiseVelocity, slot);
		}

		@Override
		protected void setProfiledAllowableError(int slot, double allowableError) {
			mHardwareController.setSmartMotionAllowedClosedLoopError(allowableError, slot);
		}

		@Override
		protected void setProfiledMinimumVelocityOutput(int slot, double minimumOutputVelocity) {
			mHardwareController.setSmartMotionMinOutputVelocity(minimumOutputVelocity, slot);
		}

		@Override
		boolean setReference(ControllerOutput.Mode mode, int slot, double reference, double arbitraryPercentOutput) {
			ControlType controllerType = MODE_TO_CONTROLLER.get(mode);
			return mHardwareController.setReference(reference, controllerType, slot, arbitraryPercentOutput,
					ArbFFUnits.kPercentOut) == CANError.kOk;
		}

		@Override
		int getId() {
			return getDeviceId();
		}

		@Override
		void setP(int slot, double p) {
			mHardwareController.setP(p, slot);
		}

		@Override
		void setI(int slot, double i) {
			mHardwareController.setI(i, slot);
		}

		@Override
		void setD(int slot, double d) {
			mHardwareController.setD(d, slot);
		}

		@Override
		void setF(int slot, double f) {
			mHardwareController.setFF(f, slot);
		}

		@Override
		void setIZone(int slot, double iZone) {
			mHardwareController.setIZone(iZone, slot);
		}
	};

	public Spark(int deviceId) {
		super(deviceId, MotorType.kBrushless);
	}

	public boolean setOutput(ControllerOutput output) {
		return mController.setOutput(output);
	}
}
