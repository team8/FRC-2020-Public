package com.palyrobotics.frc2020.util.control;

import static java.util.Map.entry;

import java.util.Map;

import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANPIDController.AccelStrategy;
import com.revrobotics.CANPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;

/**
 * A wrapper around a Spark Max that only updates inputs when they have changed. This also supports
 * updating gains smartly. Control types are automatically mapped to PID slots on the Spark
 * controller.
 *
 * @author Quintin
 */
public class Spark extends CANSparkMax {

	static class SparkController extends ProfiledControllerBase<Spark> {

		private CANPIDController mPidController;

		protected SparkController(Spark spark) {
			super(spark);
			mPidController = spark.getPIDController();
		}

		@Override
		protected void updateGains(boolean isFirstInitialization, int slot, Gains newGains, Gains lastGains) {
			super.updateGains(isFirstInitialization, slot, newGains, lastGains);
			if (isFirstInitialization) {
				mPidController.setSmartMotionAccelStrategy(AccelStrategy.kSCurve, slot);
			}
		}

		@Override
		void setProfiledAcceleration(int slot, double acceleration) {
			mPidController.setSmartMotionMaxAccel(acceleration, slot);
		}

		@Override
		void setProfiledCruiseVelocity(int slot, double cruiseVelocity) {
			mPidController.setSmartMotionMaxVelocity(cruiseVelocity, slot);
		}

		@Override
		protected void setProfiledAllowableError(int slot, double allowableError) {
			mPidController.setSmartMotionAllowedClosedLoopError(allowableError, slot);
		}

		@Override
		protected void setProfiledMinimumVelocityOutput(int slot, double minimumOutputVelocity) {
			mPidController.setSmartMotionMinOutputVelocity(minimumOutputVelocity, slot);
		}

		@Override
		boolean setReference(ControllerOutput.Mode mode, int slot, double reference, double arbitraryPercentOutput) {
			ControlType controllerType = kModeToController.get(mode);
			return mPidController.setReference(reference, controllerType, slot, arbitraryPercentOutput,
					ArbFFUnits.kPercentOut) == CANError.kOk;
		}

		@Override
		int getId() {
			return mController.getDeviceId();
		}

		@Override
		void setP(int slot, double p) {
			mPidController.setP(p, slot);
		}

		@Override
		void setI(int slot, double i) {
			mPidController.setI(i, slot);
		}

		@Override
		void setD(int slot, double d) {
			mPidController.setD(d, slot);
		}

		@Override
		void setF(int slot, double f) {
			mPidController.setFF(f, slot);
		}

		@Override
		void setIZone(int slot, double iZone) {
			mPidController.setIZone(iZone, slot);
		}
	}

	protected static final Map<ControllerOutput.Mode, ControlType> kModeToController = Map.ofEntries(
			entry(ControllerOutput.Mode.PERCENT_OUTPUT, ControlType.kDutyCycle),
			entry(ControllerOutput.Mode.POSITION, ControlType.kPosition),
			entry(ControllerOutput.Mode.VELOCITY, ControlType.kVelocity),
			entry(ControllerOutput.Mode.PROFILED_POSITION, ControlType.kSmartMotion),
			entry(ControllerOutput.Mode.PROFILED_VELOCITY, ControlType.kSmartVelocity));

	private final SparkController mController = new SparkController(this);

	public Spark(int deviceId) {
		super(deviceId, MotorType.kBrushless);
	}

	public boolean setOutput(ControllerOutput output) {
		return mController.setOutput(output);
	}
}
