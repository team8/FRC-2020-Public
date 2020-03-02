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
public class Spark extends CANSparkMax implements Controller {

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
		protected boolean setReference(ControllerOutput.Mode mode, int slot, double reference, double arbitraryPercentOutput) {
			ControlType controllerType = kModeToController.get(mode);
			return mPidController.setReference(reference, controllerType, slot, arbitraryPercentOutput,
					ArbFFUnits.kPercentOut) == CANError.kOk;
		}

		@Override
		protected void setP(int slot, double p) {
			mPidController.setP(p, slot);
		}

		@Override
		protected void setI(int slot, double i) {
			mPidController.setI(i, slot);
		}

		@Override
		protected void setD(int slot, double d) {
			mPidController.setD(d, slot);
		}

		@Override
		protected void setF(int slot, double f) {
			mPidController.setFF(f, slot);
		}

		@Override
		protected void setIZone(int slot, double iZone) {
			mPidController.setIZone(iZone, slot);
		}

		@Override
		protected void setIMax(int slot, double iMax) {
			mPidController.setIMaxAccum(iMax, slot);
		}

		@Override
		protected void setFrameTimings() {
			mController.setControlFramePeriodMs(mControlFrameMs);
			mController.setPeriodicFramePeriod(PeriodicFrame.kStatus0, mStatusFrameMs);
			mController.setPeriodicFramePeriod(PeriodicFrame.kStatus1, mStatusFrameMs);
			mController.setPeriodicFramePeriod(PeriodicFrame.kStatus2, mStatusFrameMs);
		}
	}

	protected static final Map<ControllerOutput.Mode, ControlType> kModeToController = Map.ofEntries(
			entry(ControllerOutput.Mode.PERCENT_OUTPUT, ControlType.kDutyCycle),
			entry(ControllerOutput.Mode.POSITION, ControlType.kPosition),
			entry(ControllerOutput.Mode.VELOCITY, ControlType.kVelocity),
			entry(ControllerOutput.Mode.PROFILED_POSITION, ControlType.kSmartMotion),
			entry(ControllerOutput.Mode.PROFILED_VELOCITY, ControlType.kSmartVelocity));
	private final SparkController mController = new SparkController(this);
	private final String mName;

	public Spark(int deviceId, String name) {
		super(deviceId, MotorType.kBrushless);
		mName = name;
		clearFaults();
	}

	public void setOutputRange(double minimumOutput, double maximumOutput) {
		CANPIDController controller = getController();
		for (int slotIndex = 0; slotIndex <= 3; slotIndex++) controller.setOutputRange(minimumOutput, maximumOutput, slotIndex);
	}

	public String getName() {
		return String.format("(Spark #%d), %s", getDeviceId(), mName);
	}

	/**
	 * As of 2/17/20 Spark controllers {@link CANSparkMax#getPIDController()} returns a new object. So,
	 * it causes issues in loops and should be cached instead.
	 *
	 * @return Cached {@link CANPIDController} instance.
	 */
	public CANPIDController getController() {
		return mController.mPidController;
	}

	public boolean setOutput(ControllerOutput output) {
		return mController.setOutput(output);
	}
}
