package com.palyrobotics.frc2020.util.control;

public abstract class ProfiledControllerBase extends SimpleControllerBase {

    abstract void setProfiledCruiseVelocity(int slot, double cruiseVelocity);

    abstract void setProfiledAcceleration(int slot, double acceleration);

    protected abstract void setProfiledMinimumVelocityOutput(int slot, double minimumOutputVelocity);

    protected abstract void setProfiledAllowableError(int slot, double allowableError);

    @Override
    protected void updateGains(boolean isFirstInitialization, int slot, Gains newGains, Gains lastGains) {
        super.updateGains(isFirstInitialization, slot, newGains, lastGains);
        if (newGains instanceof SmartGains) { // TODO maybe we could set this up such that we do not check type
            SmartGains lastSmartGains = (SmartGains) lastGains, newSmartGains = (SmartGains) newGains;
            if (Double.compare(lastSmartGains.acceleration * mRobotConfig.smartMotionMultiplier, newSmartGains.acceleration * mRobotConfig.smartMotionMultiplier) != 0)
                setProfiledAcceleration(slot, newSmartGains.acceleration * mRobotConfig.smartMotionMultiplier);
            if (Double.compare(lastSmartGains.velocity * mRobotConfig.smartMotionMultiplier, newSmartGains.velocity * mRobotConfig.smartMotionMultiplier) != 0)
                setProfiledCruiseVelocity(slot, newSmartGains.velocity * mRobotConfig.smartMotionMultiplier);
            if (Double.compare(lastSmartGains.allowableError, newSmartGains.allowableError) != 0)
                setProfiledAllowableError(slot, newSmartGains.allowableError);
            if (Double.compare(lastSmartGains.minimumOutputVelocity, newSmartGains.minimumOutputVelocity) != 0)
                setProfiledMinimumVelocityOutput(slot, newSmartGains.minimumOutputVelocity);
        }
    }
}
