package com.palyrobotics.frc2019.util.control;

import com.palyrobotics.frc2019.config.RobotConfig;
import com.palyrobotics.frc2019.util.config.Configs;
import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ControlType;
import edu.wpi.first.wpilibj.DriverStation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A wrapper around a Spark Max that only updates inputs when they have changed.
 * This also supports updating gains smartly.
 * Control types are automatically mapped to PID slots on the Spark controller.
 *
 * @author Quintin Dwight
 */
public class LazySparkMax extends CANSparkMax {

    private static Map<ControlType, Integer> sControlTypeToSlot = Map.of(
            ControlType.kSmartMotion, 1,
            ControlType.kSmartVelocity, 2
    );

    private double mLastReference, mLastArbitraryPercentOutput;
    private int mLastSlot;
    private ControlType mLastControlType;
    private Map<Integer, Gains> mLastGains = new HashMap<>();
    private RobotConfig mRobotConfig = Configs.get(RobotConfig.class);

    public LazySparkMax(int deviceNumber) {
        super(deviceNumber, MotorType.kBrushless);
    }

    public boolean set(ControlType type, double reference, double arbitraryPercentOutput, Gains gains) {
        // Checks to make sure we are using this properly
        boolean isSmart = type == ControlType.kSmartMotion || type == ControlType.kSmartVelocity,
                requiresGains = isSmart || type == ControlType.kPosition || type == ControlType.kVelocity;
        if (requiresGains && gains == null)
            throw new IllegalArgumentException(String.format("%s requires gains!", type));
        if (!requiresGains && gains != null)
            throw new IllegalArgumentException(String.format("%s should have no gains passed!", type));
        if (isSmart && !(gains instanceof SmartGains))
            throw new IllegalArgumentException("Setting smart motion or smart velocity requires smart gains!");
        // Slot is determined based on control type
        // TODO add feature to add custom slots
        int slot = sControlTypeToSlot.getOrDefault(type, 0);
        updateGainsIfNeeded(gains, slot);
        boolean areGainsEqual = !requiresGains || Objects.equals(gains, mLastGains.get(slot));
        if (!areGainsEqual || slot != mLastSlot || type != mLastControlType || reference != mLastReference || arbitraryPercentOutput != mLastArbitraryPercentOutput) {
            if (getPIDController().setReference(reference, type, slot, arbitraryPercentOutput, ArbFFUnits.kPercentOut) == CANError.kOk) {
                mLastSlot = slot;
                mLastControlType = type;
                mLastReference = reference;
                mLastArbitraryPercentOutput = arbitraryPercentOutput;
                if (!areGainsEqual) { // Special check since the copy function creates garbage and should only be done when necessary. All other variables are trivial to set.
                    mLastGains.put(slot, Configs.copy(gains));
                }
                return true;
//                System.out.printf("%s, %d, %f, %f, %s%n", type, slot, reference, arbitraryPercentOutput, Configs.toJson(gains));
            } else {
                DriverStation.reportError(String.format("Error updating output on spark max with ID: %d", getDeviceId()), new RuntimeException().getStackTrace());
            }
        }
        return false;
    }

    private void updateGainsIfNeeded(Gains gains, int slot) {
        if (gains != null) {
            CANPIDController controller = getPIDController();
            boolean firstInitialization = !mLastGains.containsKey(slot);
            if (firstInitialization) { // Empty gains for default value instead of null
                mLastGains.put(slot, (slot == 1 || slot == 2) ? new SmartGains() : new Gains()); // TODO a little ugly
            }
            Gains lastGains = mLastGains.get(slot);
            if (Double.compare(lastGains.p, gains.p) != 0) controller.setP(gains.p, slot);
            if (Double.compare(lastGains.i, gains.i) != 0) controller.setI(gains.i, slot);
            if (Double.compare(lastGains.d, gains.d) != 0) controller.setD(gains.d, slot);
            if (Double.compare(lastGains.f, gains.f) != 0) controller.setFF(gains.f, slot);
            if (Double.compare(lastGains.iZone, gains.iZone) != 0) controller.setIZone(gains.iZone, slot);
            if (gains instanceof SmartGains) { // TODO maybe we could set this up such that we do not check type
                SmartGains lastSmartGains = (SmartGains) lastGains, smartGains = (SmartGains) gains;
                if (Double.compare(lastSmartGains.acceleration * mRobotConfig.smartMotionMultiplier, smartGains.acceleration * mRobotConfig.smartMotionMultiplier) != 0)
                    controller.setSmartMotionMaxAccel(smartGains.acceleration * mRobotConfig.smartMotionMultiplier, slot);
                if (Double.compare(lastSmartGains.velocity * mRobotConfig.smartMotionMultiplier, smartGains.velocity * mRobotConfig.smartMotionMultiplier) != 0)
                    controller.setSmartMotionMaxVelocity(smartGains.velocity * mRobotConfig.smartMotionMultiplier, slot);
                if (Double.compare(lastSmartGains.allowableError, smartGains.allowableError) != 0)
                    controller.setSmartMotionAllowedClosedLoopError(smartGains.allowableError, slot);
                if (Double.compare(lastSmartGains.minimumOutputVelocity, smartGains.minimumOutputVelocity) != 0)
                    controller.setSmartMotionMinOutputVelocity(smartGains.minimumOutputVelocity, slot);
                if (firstInitialization) {
                    controller.setOutputRange(-1.0, 1.0, slot);
                    controller.setSmartMotionAccelStrategy(CANPIDController.AccelStrategy.kSCurve, slot); // TODO this does not even do anything as of 1.4.1
                }
            }
        }
    }
}
