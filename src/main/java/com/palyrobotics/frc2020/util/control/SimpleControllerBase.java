package com.palyrobotics.frc2020.util.control;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.util.config.Configs;

public abstract class SimpleControllerBase<TController extends Controller> {

	private static final int kDefaultSlot = 0, kProfiledPositionSlot = 1, kProfiledVelocitySlot = 2, kVelocitySlot = 3;
	private static Map<ControllerOutput.Mode, Integer> sModeToSlot = Map.of(
			ControllerOutput.Mode.PROFILED_POSITION, kProfiledPositionSlot,
			ControllerOutput.Mode.PROFILED_VELOCITY, kProfiledVelocitySlot,
			ControllerOutput.Mode.VELOCITY, kVelocitySlot);
	protected RobotConfig mRobotConfig = Configs.get(RobotConfig.class);
	protected TController mController;
	protected boolean mHasCustomFrames;
	protected int mControlFrameMs, mStatusFrameMs;
	private double mLastReference, mLastArbitraryPercentOutput;
	private int mLastSlot;
	private ControllerOutput.Mode mLastMode;
	private Map<Integer, Gains> mLastGains = new HashMap<>();

	protected SimpleControllerBase(TController controller) {
		mController = controller;
	}

	public boolean setOutput(ControllerOutput output) {
		ControllerOutput.Mode mode = output.getControlMode();
//		Robot.mDebugger.addPoint("getControlMode");
		// Checks to make sure we are using this properly
		boolean isProfiled = mode == ControllerOutput.Mode.PROFILED_POSITION || mode == ControllerOutput.Mode.PROFILED_VELOCITY,
				requiresGains = isProfiled || mode == ControllerOutput.Mode.POSITION || mode == ControllerOutput.Mode.VELOCITY;
		Gains gains = output.getGains();
//		Robot.mDebugger.addPoint("getGains");
		// Slot is determined based on control mode
		// TODO add feature to add custom slots
		int slot = sModeToSlot.getOrDefault(mode, kDefaultSlot);
		updateGainsIfChanged(gains, slot);
//		Robot.mDebugger.addPoint("updateGainsIfChanged");
		boolean areGainsEqual = !requiresGains || Objects.equals(gains, mLastGains.get(slot));
//		Robot.mDebugger.addPoint("areGainsEqual");
		double reference = output.getReference(), arbitraryPercentOutput = output.getArbitraryDemand();
//		Robot.mDebugger.addPoint("getReference and arbitraryPercentOutput");
		if (!areGainsEqual || slot != mLastSlot || mode != mLastMode || reference != mLastReference || arbitraryPercentOutput != mLastArbitraryPercentOutput) {
			if (setReference(mode, slot, reference * mRobotConfig.motorOutputMultiplier, arbitraryPercentOutput)) {
				mLastSlot = slot;
				mLastMode = mode;
				mLastReference = reference;
				mLastArbitraryPercentOutput = arbitraryPercentOutput;
				if (!areGainsEqual) {
					// Special check since the copy function creates garbage and should only be done
					// when necessary.
					// All other variables are trivial to set.
					if (isProfiled) {
						var profiledGains = (ProfiledGains) gains;
						mLastGains.put(slot, new ProfiledGains(gains.p, gains.i, gains.d, gains.f, gains.iZone, gains.iMax, profiledGains.acceleration, profiledGains.velocity, profiledGains.allowableError, profiledGains.minimumOutputVelocity));
					} else {
						mLastGains.put(slot, new Gains(gains.p, gains.i, gains.d, gains.f, gains.iZone, gains.iMax));
					}
//					Robot.mDebugger.addPoint("mLastGains.put");
				}
//				System.out.printf("%s, %d, %f%n", mode, slot, reference);
				return true;
			} else {
				Log.error("controller", String.format("Error updating output on controller: %s", getName()));
			}
		}
		return false;
	}

	protected void updateGainsIfChanged(Gains gains, int slot) {
		if (gains != null) {
			boolean isFirstInitialization = !mLastGains.containsKey(slot);
			if (isFirstInitialization) { // Empty gains for default value instead of null
				mLastGains.put(slot, (slot == kProfiledPositionSlot || slot == kProfiledVelocitySlot) ? new ProfiledGains() : new Gains());
			}
			Gains lastGains = mLastGains.get(slot);
			updateGains(isFirstInitialization, slot, gains, lastGains);
		}
	}

	protected abstract boolean setReference(ControllerOutput.Mode mode, int slot, double reference, double arbitraryPercentOutput);

	protected String getName() {
		return mController.getName();
	}

	protected void updateGains(boolean isFirstInitialization, int slot, Gains newGains, Gains lastGains) {
		if (Double.compare(lastGains.p, newGains.p) != 0) setP(slot, newGains.p);
		if (Double.compare(lastGains.i, newGains.i) != 0) setI(slot, newGains.i);
		if (Double.compare(lastGains.d, newGains.d) != 0) setD(slot, newGains.d);
		if (Double.compare(lastGains.f, newGains.f) != 0) setF(slot, newGains.f);
		if (Double.compare(lastGains.iZone, newGains.iZone) != 0) setIZone(slot, newGains.iZone);
		if (Double.compare(lastGains.iMax, newGains.iMax) != 0) setIMax(slot, newGains.iMax);
	}

	protected abstract void setP(int slot, double p);

	protected abstract void setI(int slot, double i);

	protected abstract void setD(int slot, double d);

	protected abstract void setF(int slot, double f);

	protected abstract void setIZone(int slot, double iZone);

	protected abstract void setIMax(int slot, double iMax);

	/**
	 * Force telling the speed controller its custom communication periods if they exist.
	 *
	 * @see #configFrameTimings(int, int) to define custom periods.
	 */
	public final void updateFrameTimings() {
		if (mHasCustomFrames) {
			setFrameTimings();
		}
	}

	/**
	 * Configures how often this controller communicates with the roboRIO over CAN.
	 *
	 * @param controlFrameMs Period for commands sent to controller
	 * @param statusFrameMs  Period for received information from the controller. Needs to be similar to
	 *                       closed loop control loop period, since it contains encoder information
	 */
	protected void configFrameTimings(int controlFrameMs, int statusFrameMs) {
		mHasCustomFrames = true;
		mControlFrameMs = controlFrameMs;
		mStatusFrameMs = statusFrameMs;
		setFrameTimings();
	}

	/**
	 * Implementation per controller for setting frame communication periods.
	 */
	protected abstract void setFrameTimings();
}
