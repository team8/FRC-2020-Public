package com.palyrobotics.frc2020.util.control;

public abstract class ProfiledControllerBase<TController> extends SimpleControllerBase<TController> {

	protected ProfiledControllerBase(TController controller) {
		super(controller);
	}

	@Override
	protected void updateGains(boolean isFirstInitialization, int slot, Gains newGains, Gains lastGains) {
		super.updateGains(isFirstInitialization, slot, newGains, lastGains);
		if (newGains instanceof ProfiledGains) { // TODO maybe we could set this up such that we do not check type
			ProfiledGains lastProfiledGains = (ProfiledGains) lastGains, newProfiledGains = (ProfiledGains) newGains;
			if (Double.compare(lastProfiledGains.acceleration * mRobotConfig.motorOutputMultiplier,
					newProfiledGains.acceleration * mRobotConfig.motorOutputMultiplier) != 0) setProfiledAcceleration(
							slot, newProfiledGains.acceleration * mRobotConfig.motorOutputMultiplier);
			if (Double.compare(lastProfiledGains.velocity * mRobotConfig.motorOutputMultiplier,
					newProfiledGains.velocity * mRobotConfig.motorOutputMultiplier) != 0) setProfiledCruiseVelocity(
							slot, newProfiledGains.velocity * mRobotConfig.motorOutputMultiplier);
			if (Double.compare(lastProfiledGains.allowableError,
					newProfiledGains.allowableError) != 0) setProfiledAllowableError(slot,
							newProfiledGains.allowableError);
			if (Double.compare(lastProfiledGains.minimumOutputVelocity,
					newProfiledGains.minimumOutputVelocity) != 0) setProfiledMinimumVelocityOutput(slot,
							newProfiledGains.minimumOutputVelocity);
		}
	}

	abstract void setProfiledAcceleration(int slot, double acceleration);

	abstract void setProfiledCruiseVelocity(int slot, double cruiseVelocity);

	protected abstract void setProfiledAllowableError(int slot, double allowableError);

	protected abstract void setProfiledMinimumVelocityOutput(int slot, double minimumOutputVelocity);
}
